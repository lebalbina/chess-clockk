package com.example.chessclockk

import android.content.SharedPreferences
import android.util.EventLogTags
import com.example.chessclockk.usecase.TempoRepository
import com.example.chessclockk.vm.GameState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.invoke
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher

@ExtendWith(CoroutineTestRule::class)
class ClockkTest {

    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true)

    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)

    private val tempoRepository = mockk<TempoRepository>(relaxed = true)

    private val timeProvider = mockk<TimeProvider>(relaxed = true)

    private val coroutineTestExtension = CoroutineTestRule()

    private lateinit var chessClock: NewClock

    //Arrange
    //Act
    //Assert

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { tempoRepository.retrieveTempo() } returns Pair("00:03:00", "00:02")

        chessClock = NewClock(tempoRepository, timeProvider, coroutineTestExtension.testDispatcher)
    }

    @Test
    fun testInit() {
        assertEquals(180000, chessClock.blackMillisRemaining)
        assertEquals(180000, chessClock.whiteMillisRemaining)
        assertEquals(2000L, chessClock.bonus)
    }

    @Test
    fun testSetTempo() {
        chessClock.setTempo("00:07:00", "00:03")

        verify { tempoRepository.saveTempo("00:07:00", "00:03") }
        assertEquals(420000L, chessClock.blackMillisRemaining)
        assertEquals(420000L, chessClock.whiteMillisRemaining)
        assertEquals(3000L, chessClock.bonus)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWhitePlayerTimeUpdate() = runTest(coroutineTestExtension.testDispatcher) {
        // Arrange
        chessClock.setTempo("00:03:00", "00:00")
        val updateWhiteMillis = mockk<(Long) -> Unit>(relaxed = true)
        chessClock.updateGameState(GameState.WHITE_MOVE)
        every { timeProvider.currentTimeMillis() } returnsMany listOf(0L, 500L, 1000L, 1500L)

        // Act
        launch {
            chessClock.startClock(
                updateBlack = {},
                onGameEnd = {},
                updateWhite = updateWhiteMillis
            )
        }
        advanceTimeBy(200L)
        chessClock.stopClock()

        //Assert
        coVerify { updateWhiteMillis.invoke(179500) }
        coVerify { updateWhiteMillis.invoke(179000) }
        assertEquals(179000, chessClock.whiteMillisRemaining)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWhitePlayerHasLost() = runTest(coroutineTestExtension.testDispatcher) {
        // Arrange
        chessClock.setTempo("00:00:03", "00:00")
        val onGameEnd = mockk<(GameState) -> Unit>(relaxed = true)
        chessClock.updateGameState(GameState.WHITE_MOVE)
        every { timeProvider.currentTimeMillis() } returnsMany listOf(
            0L, 1000L,
            2000L, 3000L,
            4000L, 5000L
        )

        // Act
        launch {
            chessClock.startClock(
                updateBlack = {},
                onGameEnd = onGameEnd,
                updateWhite = {}
            )
        }
        advanceTimeBy(300L)
        chessClock.stopClock()

        //Assert
        coVerify { onGameEnd.invoke(GameState.END_GAME_WHITE) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSwitchPlayerTimeUpdate() = runTest(coroutineTestExtension.testDispatcher) {
        // Arrange
        chessClock.setTempo("00:03:00", "00:00")
        val updateWhiteMillis = mockk<(Long) -> Unit>(relaxed = true)
        chessClock.updateGameState(GameState.WHITE_MOVE)
        every { timeProvider.currentTimeMillis() } returnsMany listOf(
            0L, 1000L,
            2000L, 3000L,
            4000L, 5000L,
            6000L, 7000L,
            8000L, 9000L
        )

        // Act
        launch {
            chessClock.startClock(
                updateBlack = {},
                onGameEnd = {},
                updateWhite = updateWhiteMillis
            )
        }
        advanceTimeBy(200L)
        chessClock.updateGameState(GameState.BLACK_MOVE)
        advanceTimeBy(400L)
        chessClock.stopClock()

        //Assert
        coVerify { updateWhiteMillis.invoke(any()) }
        assertEquals(178000, chessClock.whiteMillisRemaining)
        assertEquals(177000, chessClock.blackMillisRemaining)
    }

    //Test beeps

}

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}


