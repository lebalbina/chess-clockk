package com.example.chessclockk

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

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
}


