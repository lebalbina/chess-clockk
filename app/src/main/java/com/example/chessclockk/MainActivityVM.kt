package com.example.chessclockk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivityVM : ViewModel() {

    private val _clockBlack = MutableLiveData<String>()
    val clockBlackLiveData: LiveData<String>
        get() = _clockBlack

    private val _clockWhite = MutableLiveData<String>()
    val clockWhiteLiveData: LiveData<String>
        get() = _clockWhite

    private val _gameState = MutableLiveData<GameState>()
    val gameStateLiveData: LiveData<GameState>
        get() = _gameState

    private var isClockWhiteRunning = false
    private var whiteMillisRemaining: Long = 0L

    private var isClockBlackRunning = false
    private var blackMillisRemaining: Long = 0L

    private var clockIncreaseJob: Job? = null
    private var clockDecreaseJob: Job? = null

    private var gameState = mutableListOf<GameState>()

    enum class GameState {
        WHITE_MOVE, BLACK_MOVE, PAUSE, NEW_GAME
    }

    init {
        updateGameState(GameState.NEW_GAME)
    }

    fun clockBlackPressed() {
        setPlayerWhiteClock()
        runClockWhite()
    }

    fun clockWhitePressed() {
        setPlayerBlackClock()
        runClockBlack()
    }

    //TODO set limitation
    fun startIncrement(isLongPress: Boolean) {
        if (!isLongPress) {
            whiteMillisRemaining += 1000
            blackMillisRemaining += 1000
            updateClocks()
        } else {
            clockIncreaseJob = viewModelScope.launch {
                while (true) {
                    whiteMillisRemaining += 1000
                    blackMillisRemaining += 1000
                    updateClocks()
                    delay(100)
                }
            }
        }
    }

    fun stopIncrement() {
        clockIncreaseJob?.cancel()
    }

    //TODO set limitation
    fun startDecrease(isLongPress: Boolean) {
        if (!isLongPress) {
            whiteMillisRemaining -= 1000
            blackMillisRemaining -= 1000
            updateClocks()
        } else {
            clockDecreaseJob = viewModelScope.launch {
                while (true) {
                    whiteMillisRemaining -= 1000
                    blackMillisRemaining -= 1000
                    updateClocks()
                    delay(100)
                }
            }
        }
    }

    fun stopDecrease() {
        clockDecreaseJob?.cancel()
    }

    fun onPlayPauseBtnClicked(pause: Boolean) {
        when (gameState.last()) {
            GameState.WHITE_MOVE, GameState.BLACK_MOVE -> {
                pauseClocks()
            }

            GameState.PAUSE -> {
                val currentTurn = gameState.elementAt(gameState.size - 2)
                if (currentTurn == GameState.WHITE_MOVE || currentTurn == GameState.NEW_GAME) {
                    setPlayerWhiteClock()
                } else if (currentTurn == GameState.BLACK_MOVE) {
                    setPlayerBlackClock()
                }
            }
            GameState.NEW_GAME -> TODO()
        }
    }

    private fun updateGameState(state: GameState) {
        gameState.add(state)
        _gameState.postValue(state)
    }

    private fun setPlayerWhiteClock() {
        updateGameState(GameState.WHITE_MOVE)
        isClockBlackRunning = false
        isClockWhiteRunning = true
    }

    private fun setPlayerBlackClock() {
        updateGameState(GameState.BLACK_MOVE)
        isClockWhiteRunning = false
        isClockBlackRunning = true
    }

    private fun pauseClocks() {
        isClockWhiteRunning = false
        isClockBlackRunning = false
        updateGameState(GameState.PAUSE)
    }

    private fun updateClocks() {
        _clockBlack.postValue(millisecondsToString(blackMillisRemaining))
        _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
    }

    private fun runClockWhite() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    if (whiteMillisRemaining > 0 && isClockWhiteRunning) {
                        whiteMillisRemaining -= 100
                        delay(100)
                        withContext(Dispatchers.Main) {
                            _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
                        }
                    }
                }
            }
        }
    }

    private fun runClockBlack() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    while (blackMillisRemaining > 0 && isClockBlackRunning) {
                        blackMillisRemaining -= 100
                        delay(100)
                        withContext(Dispatchers.Main) {
                            _clockBlack.postValue(millisecondsToString(blackMillisRemaining))
                        }
                    }
                }
            }
        }
    }

    private fun millisecondsToString(millisecond: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisecond)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond) % 60
        val millis = millisecond % 1000
        return "%02d:%02d:%02d.%03d".format(hours, minutes, seconds, millis)
    }
}

