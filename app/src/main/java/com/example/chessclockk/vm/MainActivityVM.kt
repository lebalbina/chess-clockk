package com.example.chessclockk.vm

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chessclockk.clock.SoundManager
import com.example.chessclockk.convertGameAndBonusTimeToTempo
import com.example.chessclockk.convertHHMMSSToMillis
import com.example.chessclockk.millisToHHMMSS
import com.example.chessclockk.usecase.TempoUseCase
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

//TODO Clock oddzielnym obiektem

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val tempoUseCase: TempoUseCase,
    private val soundManager: SoundManager
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

    private val _showRestartDialog = MutableLiveData<Boolean>()
    override val showRestartDialog: LiveData<Boolean>
        get() = _showRestartDialog

    private val clockJob: Job by lazy {
        initializeClockJob()
    }

    //TODO companion object
    private var whiteMillisRemaining = TimeUnit.MINUTES.toMillis(3)
    private var blackMillisRemaining = TimeUnit.MINUTES.toMillis(3)
    private var bonusTime = TimeUnit.SECONDS.toMillis(2)
    private var defaultFormat = "3\" 2'"

    private var state = MainScreenState(
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
        updateTimeFormat()
        initializeGame()
    }

    override fun onClockBlackPressed() {
        soundManager.playClick()
        updateGameState(GameState.WHITE_MOVE)
        if (gameState.size > 2) {
            incrementMovesCounterAndAddBonusBlack()
        }
    }

    override fun onClockWhitePressed() {
        soundManager.playClick()
        updateGameState(GameState.BLACK_MOVE)
        if (gameState.size > 2) {
            incrementMovesCounterAndAddBonusWhite()
        }
    }

    override fun onRestartClicked() {
        if (gameState.last() != GameState.PAUSE) updateGameState(GameState.PAUSE)
        _showRestartDialog.postValue(true)
    }

    override fun onRestartDismissedClicked() {
        _showRestartDialog.postValue(false)
    }

    override fun onRestartConfirmedClicked() {
        initializeGame()
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

            GameState.NEW_GAME, GameState.END_GAME_WHITE, GameState.END_GAME_BLACK -> {
                /* do nothing */
            }
        }
    }

    override fun onCustomTimeSetClick() {
        when (gameState.last()) {
            GameState.WHITE_MOVE, GameState.BLACK_MOVE ->
                updateGameState(GameState.PAUSE)

            else -> {
                //do nothing
            }
        }
    }

    //TODO jesli toczy sie gra, wyswietlic restart dialog
    override fun onCustomTimeSet(customTime: String, bonus: String) {
        tempoUseCase.saveTempo(customTime, bonus)
        updateTimeFormat()
        initializeGame()
    }

    override fun onCleared() {
        super.onCleared()
        clockJob.cancel()
        soundManager.release()
    }

    private fun updateGameState(gameState: GameState) {
        this.gameState.add(gameState)
        state = state.copy(gameState = gameState)
        _state.postValue(state)
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
        state = state.copy(timeFormat = tempoAndBonus.convertGameAndBonusTimeToTempo())
        _state.postValue(state)
    }

    private fun incrementMovesCounterAndAddBonusBlack() {
        blackMillisRemaining += bonusTime
        blackMovesCount += 1
        _clockBlack.postValue(blackMillisRemaining.millisToHHMMSS())
        state = state.copy(blackMovesCount = blackMovesCount)
        _state.postValue(state)
    }

    private fun incrementMovesCounterAndAddBonusWhite() {
        whiteMillisRemaining += bonusTime
        whiteMovesCount += 1
        _clockWhite.postValue(whiteMillisRemaining.millisToHHMMSS())
        state = state.copy(whiteMovesCount = whiteMovesCount)
        _state.postValue(state)
    }

    private fun initializeGame() {
        gameState.clear()
        updateGameState(GameState.NEW_GAME)
        val gameTempoAndBonus = tempoUseCase.retrieveTempo()
        whiteMillisRemaining = gameTempoAndBonus.first.convertHHMMSSToMillis()
        blackMillisRemaining = gameTempoAndBonus.first.convertHHMMSSToMillis()
        updateClocks()

        bonusTime = gameTempoAndBonus.second.convertHHMMSSToMillis()

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
        var lastBeepSound = startTime

        while (remainingPlayerMillis > 0 && gameState.last() == gameStateToCheck) {
            val currentTime = SystemClock.elapsedRealtime()
            val elapsedTime = currentTime - lastUpdateTime
            remainingPlayerMillis -= elapsedTime
            lastUpdateTime = currentTime
            updatePlayerMillis(remainingPlayerMillis)
            updateClockValue(remainingPlayerMillis.millisToHHMMSS())

            if (remainingPlayerMillis < 1000L) {
                updateGameState(
                    if (gameStateToCheck == GameState.WHITE_MOVE) GameState.END_GAME_WHITE
                    else GameState.END_GAME_BLACK
                )
                break
            }

            if (remainingPlayerMillis < 6000L && currentTime - lastBeepSound >= 1000L) {
                soundManager.playBeep()
                lastBeepSound = currentTime
            }
            delay(100)
        }
    }
}









