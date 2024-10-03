package com.example.chessclockk

import com.example.chessclockk.clock.SoundManager
import com.example.chessclockk.usecase.TempoRepository
import com.example.chessclockk.vm.GameState
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

@ExtendWith(CoroutineTestRule::class)
class ClockkTest {

    private val tempoRepository = mockk<TempoRepository>(relaxed = true)

    private val timeProvider = mockk<ITimeProvider>(relaxed = true)

    private val coroutineTestExtension = CoroutineTestRule()

    private val soundManager = mockk<SoundManager>(relaxed = true)

    private lateinit var chessClock: Clockk

    //Arrange
    //Act
    //Assert

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { tempoRepository.retrieveTempo() } returns Pair("00:03:00", "00:02")
        every { timeProvider.currentTimeMillis() } returnsMany listOf(
            0L, 1000L,
            2000L, 3000L,
            4000L, 5000L,
            6000L, 7000L,
            8000L, 9000L,
            10000, 11000L,
            12000L, 13000L,
            14000L, 15000L,
            16000L, 17000L,
            18000, 19000L
        )

        chessClock = Clockk(
            tempoRepository,
            timeProvider,
            coroutineTestExtension.testDispatcher,
            soundManager
        )

        chessClock.updateGameState(GameState.WHITE_MOVE)
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

        // Act
        chessClock.startClock(
            updateBlack = {},
            onGameEnd = {},
            updateWhite = updateWhiteMillis
        )
        advanceTimeBy(200L)
        chessClock.stopClock()

        //Assert
        coVerify { updateWhiteMillis.invoke(179000) }
        coVerify { updateWhiteMillis.invoke(178000) }
        assertEquals(178000, chessClock.whiteMillisRemaining)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWhitePlayerHasLost() = runTest(coroutineTestExtension.testDispatcher) {
        // Arrange
        chessClock.setTempo("00:00:03", "00:00")
        val onGameEnd = mockk<(GameState) -> Unit>(relaxed = true)

        // Act
        chessClock.startClock(
            updateBlack = {},
            onGameEnd = onGameEnd,
            updateWhite = {}
        )
        advanceTimeBy(300L)
        chessClock.stopClock()

        //Assert
        coVerify { onGameEnd.invoke(GameState.END_GAME_WHITE) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWhitePlayerBonus() = runTest(coroutineTestExtension.testDispatcher) {
        // Arrange
        chessClock.setTempo("00:00:05", "00:02")
        val onGameEnd = mockk<(GameState) -> Unit>(relaxed = true)

        // Act
        chessClock.startClock(
            updateBlack = {},
            onGameEnd = onGameEnd,
            updateWhite = {}
        )
        advanceTimeBy(100L)
        chessClock.addBonusWhite()
        chessClock.stopClock()

        //Assert
        assertEquals(6000L, chessClock.whiteMillisRemaining)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSwitchPlayerTimeUpdate() = runTest(coroutineTestExtension.testDispatcher) {
        // Arrange
        chessClock.setTempo("00:03:00", "00:00")
        val updateWhiteMillis = mockk<(Long) -> Unit>(relaxed = true)

        // Act
        chessClock.startClock(
            updateBlack = {},
            onGameEnd = {},
            updateWhite = updateWhiteMillis
        )
        advanceTimeBy(200L)
        chessClock.updateGameState(GameState.BLACK_MOVE)
        advanceTimeBy(400L)
        chessClock.stopClock()

        //Assert
        coVerify { updateWhiteMillis.invoke(any()) }
        assertEquals(178000, chessClock.whiteMillisRemaining)
        assertEquals(177000, chessClock.blackMillisRemaining)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testBeep() = runTest(coroutineTestExtension.testDispatcher) {
        // Arrange
        chessClock.setTempo("00:00:10", "00:00")
        val updateWhiteMillis = mockk<(Long) -> Unit>(relaxed = true)

        // Act
        chessClock.startClock(
            updateBlack = {},
            onGameEnd = {},
            updateWhite = updateWhiteMillis
        )
        advanceTimeBy(1000L)
        chessClock.stopClock()

        //Assert
        coVerify(exactly = 5) { soundManager.playBeep() }
    }
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


