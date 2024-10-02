package com.example.chessclockk

import android.os.SystemClock

interface ITimeProvider {
    fun currentTimeMillis(): Long
}

class TimeProvider : ITimeProvider {
    override fun currentTimeMillis(): Long {
        return SystemClock.elapsedRealtime()
    }
}