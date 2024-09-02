package com.example.chessclockk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
    val gameStateLiveData: LiveData<GameState> get() = _gameState

    private val clockJob: Job by lazy {
        initializeClockJob()
    }
    private var whiteMillisRemaining: Long = 360000L
    private var blackMillisRemaining: Long = 360000L

    private var clockIncreaseJob: Job? = null
    private var clockDecreaseJob: Job? = null

    private var gameState = mutableListOf<GameState>()

    //TODO companion object
    private val maxGameTime by lazy {
        val hoursInMillis = TimeUnit.HOURS.toMillis(9)
        val minutesInMillis = TimeUnit.MINUTES.toMillis(59)
        val secondsInMillis = TimeUnit.SECONDS.toMillis(59)
        hoursInMillis + minutesInMillis + secondsInMillis
    }

    init {
        updateGameState(GameState.NEW_GAME)
        updateClocks()
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
                whiteClockTick { _clockWhite.postValue(it) }
            }

            GameState.BLACK_MOVE -> {
                blackClockTick { _clockBlack.postValue(it) }
            }

            else -> delay(100)
        }
    }

    private suspend fun whiteClockTick(updateClockValue: (String) -> Unit) {
        while (whiteMillisRemaining > 0 && gameState.last() == GameState.WHITE_MOVE) {
            whiteMillisRemaining -= 100
            delay(100)
            updateClockValue(millisecondsToString(whiteMillisRemaining))
        }
    }

    private suspend fun blackClockTick(updateClockValue: (String) -> Unit) {
        while (blackMillisRemaining > 0 && gameState.last() == GameState.BLACK_MOVE) {
            blackMillisRemaining -= 100
            delay(100)
            updateClockValue(millisecondsToString(blackMillisRemaining))
        }
    }

    override fun onCleared() {
        super.onCleared()
        clockJob.cancel()
    }

    fun onClockBlackPressed() {
        setPlayerWhiteClock()
    }

    fun onClockWhitePressed() {
        setPlayerBlackClock()
    }

    //TODO utworzyc zmienna, ktora przechowuje ostatnio ustawiony czas
    //TODO popup - czy aby na pewno?
    fun onRestartClicked() {
        updateGameState(GameState.PAUSE)
    }

    //TODO trzeba utworzyc nowa liste!
    fun onRestartConfirmedClicked() {
        updateGameState(GameState.NEW_GAME)
        whiteMillisRemaining = 360000L
        blackMillisRemaining = 360000L
        updateClocks()
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

    private fun updateGameState(state: GameState) {
        gameState.add(state)
        _gameState.postValue(state)

        if (gameState.size == 2 && (state == GameState.WHITE_MOVE || state == GameState.BLACK_MOVE)) {
            if (!clockJob.isActive) {
                clockJob.start()
            }
        }
    }

    private fun updateClocks() {
        _clockBlack.postValue(millisecondsToString(blackMillisRemaining))
        _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
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

    private fun setPlayerWhiteClock() {
        updateGameState(GameState.WHITE_MOVE)
    }

    private fun setPlayerBlackClock() {
        updateGameState(GameState.BLACK_MOVE)
    }

    private fun pauseClocks() {
        updateGameState(GameState.PAUSE)
    }

    private fun millisecondsToString(millisecond: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisecond)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond) % 60
        val millis = millisecond % 1000
        return "%01d:%02d:%02d.%03d".format(hours, minutes, seconds, millis)
    }
}



