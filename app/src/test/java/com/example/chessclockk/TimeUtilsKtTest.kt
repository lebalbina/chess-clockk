package com.example.chessclockk

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TimeUtilsKtTest {

    @Test
    fun testConvertMMSSTempoSSBonusToChessNotation() {
        val input: Pair<String, String> = Pair("00:03:00", "00:02")
        val result = input.extractHHMMSSMMSStoTempo()
        assertEquals("3' + 2\"", result)
    }

    @Test
    fun testConvertHHMMSSTempoAndMMSSBonusToChessNotation() {
        val input: Pair<String, String> = Pair("01:30:00", "01:30")
        val result = input.extractHHMMSSMMSStoTempo()
        assertEquals("90' + 90\"", result)
    }

    @Test
    fun testConvertHHMMSSTempoAndNoBonusToChessNotation() {
        val input: Pair<String, String> = Pair("01:30:00", "00:00")
        val result = input.extractHHMMSSMMSStoTempo()
        assertEquals("90'", result)
    }

}

