package com.example.chessclockk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

// TODO try to cancel() coroutine and restart if after play/pause button
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

    //state
    private val _gameStateMutableFlow = MutableStateFlow(GameState.NEW_GAME)
    val gameStateFlow: StateFlow<GameState> get() = _gameStateMutableFlow

    private var clockWhiteJob: Job? = null

    private var whiteMillisRemaining: Long = 360000L

    private var clockBlackJob: Job? = null
    private var blackMillisRemaining: Long = 360000L

    private var clockIncreaseJob: Job? = null
    private var clockDecreaseJob: Job? = null

    private var gameState = mutableListOf<GameState>()

    private val maxGameTime by lazy {
        val hoursInMillis = TimeUnit.HOURS.toMillis(9)
        val minutesInMillis = TimeUnit.MINUTES.toMillis(59)
        val secondsInMillis = TimeUnit.SECONDS.toMillis(59)
        hoursInMillis + minutesInMillis + secondsInMillis
    }

    init {
        updateGameState(GameState.NEW_GAME)
        updateClocks()

        clockWhiteJob = viewModelScope.launch(start = CoroutineStart.LAZY) {
            while (isActive) {
                if(gameStateFlow.value == GameState.WHITE_MOVE) {
                    withContext(Dispatchers.IO) {
                        while (whiteMillisRemaining > 0 && gameStateFlow.value == GameState.WHITE_MOVE) {
                            whiteMillisRemaining -= 100
                            delay(100)
                            withContext(Dispatchers.Main) {
                                _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
                            }
                        }
                    }

                }
                else {
                    delay(100)
                }
            }
        }

        clockWhiteJob?.start()

        clockBlackJob = viewModelScope.launch(start = CoroutineStart.LAZY) {
            while (isActive) {
                if(gameStateFlow.value == GameState.BLACK_MOVE) {
                    withContext(Dispatchers.IO) {
                        Log.d("chess", "current Thread ${Thread.currentThread()}, job = ${this.coroutineContext.job.hashCode()}")
                        while (blackMillisRemaining > 0 && gameStateFlow.value == GameState.BLACK_MOVE) {
                            blackMillisRemaining -= 100
                            delay(100)
                            withContext(Dispatchers.Main) {
                                _clockBlack.postValue(millisecondsToString(blackMillisRemaining))
                            }
                        }
                    }
                }
                else {
                    delay(100)
                }
            }
        }

        clockBlackJob?.start()
    }

    override fun onCleared() {
        super.onCleared()
        clockWhiteJob?.cancel()
        clockBlackJob?.cancel()
    }

    fun onClockBlackPressed() {
        setPlayerWhiteClock()
    }

    fun onClockWhitePressed() {
        setPlayerBlackClock()
    }

    fun onPlusBtnClicked(isLongPress: Boolean) {
        if (!isLongPress) {
            incrementClocks()
        } else {
            clockIncreaseJob = viewModelScope.launch {
                while (true) {
                    incrementClocks()
                    delay(100)
                }
            }
        }
    }

    fun onPlusBtnReleased() {
        clockIncreaseJob?.cancel()
    }

    fun onMinusBtnClicked(isLongPress: Boolean) {
        if (!isLongPress) {
            decrease()
        } else {
            clockDecreaseJob = viewModelScope.launch {
                while (true) {
                    decrease()
                    delay(100)
                }
            }
        }
    }

    fun onMinusBtnReleased() {
        clockDecreaseJob?.cancel()
    }

    fun onPlayPauseBtnClicked() {
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

    private fun decrease() {
        if (whiteMillisRemaining > 0 && blackMillisRemaining > 0) {
            whiteMillisRemaining -= 1000
            blackMillisRemaining -= 1000
            updateClocks()
        }
    }

    private fun incrementClocks() {
        if (whiteMillisRemaining < maxGameTime && blackMillisRemaining < maxGameTime) {
            whiteMillisRemaining += 1000
            blackMillisRemaining += 1000
            updateClocks()
        }
    }

    private fun updateGameState(state: GameState) {
        gameState.add(state)
        _gameStateMutableFlow.value = state
        _gameState.postValue(state)
    }

    private fun setPlayerWhiteClock() {
        updateGameState(GameState.WHITE_MOVE)
//        clockWhiteJob?.start()
    }

    private fun setPlayerBlackClock() {
        updateGameState(GameState.BLACK_MOVE)
//        clockBlackJob?.start()
    }

    private fun pauseClocks() {
        updateGameState(GameState.PAUSE)
    }

    private fun updateClocks() {
        _clockBlack.postValue(millisecondsToString(blackMillisRemaining))
        _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
    }

    private fun millisecondsToString(millisecond: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisecond)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond) % 60
        val millis = millisecond % 1000
        return "%01d:%02d:%02d.%03d".format(hours, minutes, seconds, millis)
    }
}


