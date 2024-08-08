package com.example.chessclockk

import android.util.Log
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

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlayingLiveData: LiveData<Boolean>
        get() = _isPlaying

    private val _clockBlack = MutableLiveData<String>()
    val clockBlackLiveData: LiveData<String>
        get() = _clockBlack

    private val _clockWhite = MutableLiveData<String>()
    val clockWhiteLiveData: LiveData<String>
        get() = _clockWhite

    private var isClockWhiteRunning = false
    private var whiteMillisRemaining: Long = 0L

    private var isClockBlackRunning = false
    private var blackMillisRemaining: Long = 0L

    private var clockValue: String = "00:00:00"
    private var isPlaying: Boolean = false

    private var job: Job? = null

    private var gameState: GameState = GameState.NEW_GAME

    enum class GameState {
        WHITE_MOVE, BLACK_MOVE, PAUSE, NEW_GAME
    }

    fun clockBlackPressed() {
        gameState = GameState.WHITE_MOVE
        isClockBlackRunning = false
        isClockWhiteRunning = true
        runClockWhite()
    }

    fun clockWhitePressed() {
        gameState = GameState.BLACK_MOVE
        isClockWhiteRunning = false
        isClockBlackRunning = true
        runClockBlack()
    }

    fun startIncrement(isLongPress: Boolean) {
        if (!isLongPress) {
            whiteMillisRemaining += 1000
            blackMillisRemaining += 1000
            updateClocks()
        } else {
            job = viewModelScope.launch {
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
        job?.cancel()
    }

    //TODO pause pauses both clocks, but play starts only the previously ran clock!!
    fun onPlayPauseBtnClicked(pause: Boolean) {
        if (pause) {
            isClockBlackRunning = false
            isClockWhiteRunning = false
        }
        else {

        }
    }

    private fun decreaseTime(isLongPress: Boolean) {
        whiteMillisRemaining -= 1000
        blackMillisRemaining -= 1000
        updateClocks()
    }

    private fun updateClocks() {
        _clockBlack.postValue(millisecondsToString(blackMillisRemaining))
        _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
    }

    private fun resetClocks(clockValue: String) {
        val regex = """(\d{2}):(\d{2}):(\d{2}).(\d{3})""".toRegex()
        val matchResult = regex.matchEntire(clockValue)
        if (matchResult != null) {
            val (hours, minutes, seconds, millis) = matchResult.destructured
            val hoursInt = hours.toInt()
            val minutesInt = minutes.toInt()
            val secondsInd = seconds.toInt()
            val milliseconds = millis.toInt()
            val clockMilliseconds =
                (hoursInt * 3600 + minutesInt * 60 + secondsInd) * 1000L + milliseconds
            whiteMillisRemaining = clockMilliseconds
            blackMillisRemaining = clockMilliseconds
        } else {
            throw IllegalArgumentException("Invalid time format. Expectes HH:MM:SS.MMM")
        }
    }

    private fun runClockWhite() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (whiteMillisRemaining > 0 && isClockWhiteRunning) {
                    whiteMillisRemaining -= 100
                    delay(100)
                    withContext(Dispatchers.Main) {
                        _clockWhite.postValue(millisecondsToString(whiteMillisRemaining))
                    }
                }
            }
        }
    }

    private fun runClockBlack() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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

    private fun millisecondsToString(milisecond: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milisecond)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milisecond) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milisecond) % 60
        val millis = milisecond % 1000
        return "%02d:%02d:%02d.%03d".format(hours, minutes, seconds, millis)
    }

}
