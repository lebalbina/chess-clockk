package com.example.chessclockk.vm

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
import java.util.concurrent.TimeUnit

//TODO UseCase dla poszczegolnych funkcjonalnosci
class MainActivityVM : ViewModel(), IMainActivityVM {

    private val _clockBlack = MutableLiveData<String>()
    val clockBlackLiveData: LiveData<String>
        get() = _clockBlack

    private val _clockWhite = MutableLiveData<String>()
    val clockWhiteLiveData: LiveData<String>
        get() = _clockWhite

    private val state: MainScreenState
    private val _state = MutableLiveData<MainScreenState>()
    val stateLiveData: LiveData<MainScreenState> get() = _state

    private val clockJob: Job by lazy {
        initializeClockJob()
    }

    //TODO z sharedPrefs wyciagnac
    private var whiteMillisRemaining: Long = 360000L
    private var blackMillisRemaining: Long = 360000L

    private var clockIncreaseJob: Job? = null
    private var clockDecreaseJob: Job? = null

    private var gameState = mutableListOf<GameState>()
    private var bonusTime: Long = 0L

    val MAX_HOURS_MILLIS = TimeUnit.HOURS.toMillis(9)
    val MAX_MINUTES_MILLIS = TimeUnit.MINUTES.toMillis(59)
    val MAX_SECONDS_MILLIS = TimeUnit.SECONDS.toMillis(59)

    private val maxGameTime by lazy {
        val hoursInMillis = TimeUnit.HOURS.toMillis(9)
        val minutesInMillis = TimeUnit.MINUTES.toMillis(59)
        val secondsInMillis = TimeUnit.SECONDS.toMillis(59)
        hoursInMillis + minutesInMillis + secondsInMillis
    }

    init {
        updateGameState(GameState.NEW_GAME)
        updateClocks()
        state = MainScreenState(
            timeFormat = "timeFormat",
            gameState = gameState.last(),
            whiteMovesCount = 0,
            blackMovesCount = 0
        )
    }

    override fun onClockBlackPressed() {
        setPlayerWhiteClock()
        if (gameState.size > 2) {
            blackMillisRemaining += bonusTime
            updateBlackCounterMoves(state.blackMovesCount + 1)
        }
    }

    override fun onClockWhitePressed() {
        setPlayerBlackClock()
        if (gameState.size > 2) {
            whiteMillisRemaining += bonusTime
            updateWhiteCounterMoves(state.whiteMovesCount + 1)
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

            GameState.NEW_GAME -> {
                /* do nothing */
            }
        }
    }

    //TODO sharedprefs do zapisu czasu
    override fun onCustomTimeSet(customTime: String, bonus: String) {
        val splitTime = customTime.split(":")
        val splitBonus = bonus.split(":")

        blackMillisRemaining = extractHHMMSSformat(splitTime)
        whiteMillisRemaining = extractHHMMSSformat(splitTime)
        updateClocks()

        val timeLong = extractMMSSformat(splitBonus[0].toLong(), splitBonus[1].toLong())
        val timeFormat = millisecondsToString(timeLong)
        updateTimeFormat(timeFormat)
    }

    override fun onCleared() {
        super.onCleared()
        clockJob.cancel()
    }

    private fun updateWhiteCounterMoves(count: Int) {
        _state.postValue(state.copy(whiteMovesCount = count))
    }

    private fun updateBlackCounterMoves(count: Int) {
        _state.postValue(state.copy(blackMovesCount = count))
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
        _clockBlack.postValue(millisecondsToString(blackMillisRemaining))
        _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
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

    //TODO jak nie ma godzin, to ich nie wyswietlac
    private fun millisecondsToString(millisecond: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisecond)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond) % 60
        val millis = millisecond % 1000

        var format = ""
        if (hours == 0L) {
            format = "%02d:%02d"
        }
        return "%01d:%02d:%02d.%03d".format(hours, minutes, seconds, millis)
    }

    private fun updateTimeFormat(timeFormat: String) {
        _state.postValue(state.copy(timeFormat = timeFormat))
    }

    private fun extractHHMMSSformat(list: List<String>): Long {
        val hours = list[0].toLong()
        val minutes = list[1].toLong()
        val seconds = list[2].toLong()

        return TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) +
                TimeUnit.SECONDS.toMillis(seconds)
    }

    private fun extractMMSSformat(minutes: Long, seconds: Long): Long {
        return TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds)
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
}






