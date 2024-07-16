package com.example.chessclockk

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityVM : ViewModel() {

    val timerLiveData: MutableLiveData<String> = MutableLiveData()
    var timer: Int = 0
    var timeJob: Job? = null

    fun startTimer() {
        timeJob?.cancel()
        timeJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                timer++
                timerLiveData.postValue(timer.toString())
            }
        }
    }

    fun stopTimer() {
        timeJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timeJob?.cancel()
    }
}