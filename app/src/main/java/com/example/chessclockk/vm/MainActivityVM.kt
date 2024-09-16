package com.example.chessclockk.vm

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chessclockk.usecase.TempoUseCase
import com.example.chessclockk.convertGameAndBonusTimeToTempo
import com.example.chessclockk.convertHHMMSSToMillis
import com.example.chessclockk.millisToHHMMSS
import com.example.chessclockk.vm.IMainActivityVM.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val tempoUseCase: TempoUseCase
) : ViewModel(), IMainActivityVM {

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

    //TODO companion object
    private var whiteMillisRemaining = TimeUnit.MINUTES.toMillis(3)
    private var blackMillisRemaining = TimeUnit.MINUTES.toMillis(3)
    private var bonusTime = TimeUnit.SECONDS.toMillis(2)
    private var defaultFormat = "3\" 2'"

    private val state = MainScreenState(
        timeFormat = defaultFormat,
        gameState = GameState.NEW_GAME,
        whiteMovesCount = 0,
        blackMovesCount = 0
    )

    private var gameState = mutableListOf<GameState>()
    private var blackMovesCount = 0
    private var whiteMovesCount = 0

    init {
        updateGameState(GameState.NEW_GAME)
        updateClocks()
        updateTimeFormat()
    }

    override fun onClockBlackPressed() {
        updateGameState(GameState.WHITE_MOVE)
        if (gameState.size > 2) {
            blackMillisRemaining += bonusTime
            blackMovesCount += 1
            _clockBlack.postValue(blackMillisRemaining.millisToHHMMSS())
            _state.postValue(state.copy(blackMovesCount = blackMovesCount))
        }
    }

    override fun onClockWhitePressed() {
        updateGameState(GameState.BLACK_MOVE)
        if (gameState.size > 2) {
            whiteMillisRemaining += bonusTime
            whiteMovesCount += 1
            _clockWhite.postValue(whiteMillisRemaining.millisToHHMMSS())
            _state.postValue(state.copy(whiteMovesCount = whiteMovesCount))
        }
    }

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

    override fun onCustomTimeSet(customTime: String, bonus: String) {
        tempoUseCase.saveTempo(customTime, bonus)
        updateTimeFormat()

        blackMillisRemaining = customTime.convertHHMMSSToMillis()
        whiteMillisRemaining = customTime.convertHHMMSSToMillis()
        updateClocks()

        bonusTime = bonus.convertHHMMSSToMillis()
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
        _clockBlack.postValue(blackMillisRemaining.millisToHHMMSS())
        _clockWhite.postValue(whiteMillisRemaining.millisToHHMMSS())
    }

    private fun updateTimeFormat() {
        val tempoAndBonus = tempoUseCase.retrieveTempo()
        _state.postValue(state.copy(timeFormat = tempoAndBonus.convertGameAndBonusTimeToTempo()))
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
            updateClockValue(remainingPlayerMillis.millisToHHMMSS())
            lastUpdateTime = currentTime
            delay(100)
        }
    }
}







