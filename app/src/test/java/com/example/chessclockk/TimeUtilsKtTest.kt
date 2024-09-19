package com.example.chessclockk

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class TimeUtilsKtTest {

    @Test
    fun testConvertMMTempoSSBonusToChessNotation() {
        val input = Pair("00:03:00", "00:02")
        val result = input.convertGameAndBonusTimeToTempo()
        assertEquals("3' + 2\"", result)
    }

    @Test
    fun testConvertHHMMTempoAndMMSSBonusToChessNotation() {
        val input = Pair("01:30:00", "01:30")
        val result = input.convertGameAndBonusTimeToTempo()
        assertEquals("90' + 90\"", result)
    }

    @Test
    fun testConvertHHMMTempoAndNoBonusToChessNotation() {
        val input = Pair("01:30:00", "00:00")
        val result = input.convertGameAndBonusTimeToTempo()
        assertEquals("90'", result)
    }

    @Test
    fun testConvertHHMMSSTempoAndMMSSBonusToChessNotation() {
        val input = Pair("01:30:30", "01:30")
        val result = input.convertGameAndBonusTimeToTempo()
        assertEquals("90' 30\" + 90\"", result)
    }

    @Test
    fun convertHHMMSSMillisToHHMMSS() {
        val input = TimeUnit.HOURS.toMillis(12) +
                TimeUnit.MINUTES.toMillis(35) +
                TimeUnit.SECONDS.toMillis(20)
        val result = input.millisToHHMMSS()
        assertEquals("12:35:20", result)
    }

    @Test
    fun convertHMMSSMillisToHHMMSS() {
        val input = TimeUnit.HOURS.toMillis(2) +
                TimeUnit.MINUTES.toMillis(35) +
                TimeUnit.SECONDS.toMillis(20)
        val result = input.millisToHHMMSS()
        assertEquals("2:35:20", result)
    }

    @Test
    fun convertMMSSMillisToHHMMSS() {
        val input =  TimeUnit.MINUTES.toMillis(35) + TimeUnit.SECONDS.toMillis(20)
        val result = input.millisToHHMMSS()
        assertEquals("35:20", result)
    }

    @Test
    fun convertMSMillisToHHMMSS() {
        val input =  TimeUnit.MINUTES.toMillis(5) + TimeUnit.SECONDS.toMillis(7)
        val result = input.millisToHHMMSS()
        assertEquals("5:07", result)
    }
}



