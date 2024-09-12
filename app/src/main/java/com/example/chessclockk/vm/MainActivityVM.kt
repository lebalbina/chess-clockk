package com.example.chessclockk.vm

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chessclockk.extractHHMMSSformatToMillis
import com.example.chessclockk.extractMMSSformatToMillis
import com.example.chessclockk.millisToFormattedString
import com.example.chessclockk.vm.IMainActivityVM.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

//TODO UseCase dla poszczegolnych funkcjonalnosci
class MainActivityVM : ViewModel(), IMainActivityVM {

    private val _clockBlack = MutableLiveData<String>()
    override val clockBlackLiveData: LiveData<String>
        get() = _clockBlack

    private val _clockWhite = MutableLiveData<String>()
    override val clockWhiteLiveData: LiveData<String>
        get() = _clockWhite

    private val _state = MutableLiveData<MainScreenState>()
    override val stateLiveData: LiveData<MainScreenState>
        get() = _state

    private val clockJob: Job by lazy {
        initializeClockJob()
    }

    private val state: MainScreenState = MainScreenState(
        timeFormat = "timeFormat",
        gameState = GameState.NEW_GAME,
        whiteMovesCount = 0,
        blackMovesCount = 0
    )

    //TODO z sharedPrefs wyciagnac
    private var whiteMillisRemaining: Long = 360000L
    private var blackMillisRemaining: Long = 360000L
    private var bonusTime: Long = 0L

    private var clockIncreaseJob: Job? = null
    private var clockDecreaseJob: Job? = null

    private var gameState = mutableListOf<GameState>()

    init {
        updateGameState(GameState.NEW_GAME)
        updateClocks()
    }

    override fun onClockBlackPressed() {
        updateGameState(GameState.WHITE_MOVE)
        if (gameState.size > 2) {
            blackMillisRemaining += bonusTime
            _state.postValue(state.copy(blackMovesCount = state.blackMovesCount + 1))
        }
    }

    override fun onClockWhitePressed() {
        updateGameState(GameState.BLACK_MOVE)
        if (gameState.size > 2) {
            whiteMillisRemaining += bonusTime
            _state.postValue(state.copy(whiteMovesCount = state.whiteMovesCount + 1))
        }
    }

    //TODO utworzyc zmienna, ktora przechowuje ostatnio ustawiony czas
    override fun onRestartClicked() {
        updateGameState(GameState.PAUSE)
    }

    override fun onRestartConfirmedClicked() {
        gameState.clear()
        updateGameState(GameState.NEW_GAME)
        whiteMillisRemaining = 360000L
        blackMillisRemaining = 360000L
        updateClocks()
    }

    override fun onPlayPauseBtnClicked() {
        when (gameState.last()) {
            GameState.WHITE_MOVE, GameState.BLACK_MOVE -> {
                updateGameState(GameState.PAUSE)
            }

            GameState.PAUSE -> {
                val currentTurn = gameState.elementAt(gameState.size - 2)
                if (currentTurn == GameState.WHITE_MOVE || currentTurn == GameState.NEW_GAME) {
                    updateGameState(GameState.WHITE_MOVE)
                } else if (currentTurn == GameState.BLACK_MOVE) {
                    updateGameState(GameState.BLACK_MOVE)
                }
            }

            GameState.NEW_GAME -> {
                /* do nothing */
            }
        }
    }

    //TODO sharedprefs do zapisu czasu
    override fun onCustomTimeSet(customTime: String, bonus: String) {
        blackMillisRemaining = customTime.extractHHMMSSformatToMillis()
        whiteMillisRemaining = customTime.extractHHMMSSformatToMillis()
        updateClocks()

        bonusTime = bonus.extractMMSSformatToMillis()
    }

    override fun onCleared() {
        super.onCleared()
        clockJob.cancel()
    }

    private fun updateGameState(gameState: GameState) {
        this.gameState.add(gameState)
        _state.postValue(state.copy(gameState = gameState))

        if (this.gameState.size == 2 && (gameState == GameState.WHITE_MOVE || gameState == GameState.BLACK_MOVE)) {
            if (!clockJob.isActive) {
                clockJob.start()
            }
        }
    }

    private fun updateClocks() {
        _clockBlack.postValue(blackMillisRemaining.millisToFormattedString())
        _clockWhite.postValue(whiteMillisRemaining.millisToFormattedString())
    }

    private fun initializeClockJob(): Job {
        return viewModelScope.launch(
            context = Dispatchers.IO,
            start = CoroutineStart.LAZY
        ) {
            while (isActive) {
                runClocks()
            }
        }
    }

    private suspend fun runClocks() {
        when (gameState.last()) {
            GameState.WHITE_MOVE -> {
                clockTick(
                    updateClockValue = { _clockWhite.postValue(it) },
                    updatePlayerMillis = { whiteMillisRemaining = it },
                    gameStateToCheck = GameState.WHITE_MOVE,
                    playerMillis = whiteMillisRemaining
                )
            }

            GameState.BLACK_MOVE -> {
                clockTick(
                    updateClockValue = { _clockBlack.postValue(it) },
                    updatePlayerMillis = { blackMillisRemaining = it },
                    gameStateToCheck = GameState.BLACK_MOVE,
                    playerMillis = blackMillisRemaining
                )
            }

            else -> delay(100)
        }
    }

    private suspend fun clockTick(
        updatePlayerMillis: (Long) -> Unit,
        updateClockValue: (String) -> Unit,
        playerMillis: Long,
        gameStateToCheck: GameState
    ) {
        val startTime = SystemClock.elapsedRealtime()
        var lastUpdateTime = startTime
        var remainingPlayerMillis = playerMillis

        while (remainingPlayerMillis > 0 && gameState.last() == gameStateToCheck) {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = currentTime - lastUpdateTime
            remainingPlayerMillis -= elapsedTime
            updatePlayerMillis(remainingPlayerMillis)
            updateClockValue(remainingPlayerMillis.millisToFormattedString())
            lastUpdateTime = currentTime
            delay(100)
        }
    }
}







