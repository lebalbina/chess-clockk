package com.example.chessclockk.vm

import androidx.lifecycle.LiveData

//TODO dodac LiveDate'y i stan
interface IMainActivityVM {

    val clockBlackLiveData: LiveData<String>
    val clockWhiteLiveData: LiveData<String>

    val stateLiveData: LiveData<MainScreenState>

    fun onClockBlackPressed()
    fun onClockWhitePressed()
    fun onRestartClicked()
    fun onRestartConfirmedClicked()
    fun onPlayPauseBtnClicked()
    fun onCustomTimeSet(customTime: String, bonus: String)
    fun onCustomTimeSetClick()

    data class MainScreenState(
        val timeFormat: String,
        val gameState: GameState,
        val blackMovesCount: Int,
        val whiteMovesCount: Int
    )
}
