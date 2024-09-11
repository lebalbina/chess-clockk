package com.example.chessclockk.vm

interface IMainActivityVM {

    fun onClockBlackPressed()
    fun onClockWhitePressed()
    fun onRestartClicked()
    fun onRestartConfirmedClicked()
    fun onPlayPauseBtnClicked()
    fun onCustomTimeSet(customTime: String, bonus: String)
}
