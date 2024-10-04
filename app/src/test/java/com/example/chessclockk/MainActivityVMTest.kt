package com.example.chessclockk

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.Observer
import com.example.chessclockk.clock.SoundManager
import com.example.chessclockk.usecase.TempoRepository
import com.example.chessclockk.vm.GameState
import com.example.chessclockk.vm.IMainActivityVM
import com.example.chessclockk.vm.IMainActivityVM.MainScreenState
import com.example.chessclockk.vm.MainActivityVM
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

@ExtendWith(InstantExecutorExtension::class)
class MainActivityVMTest {

    private val tempoRepository = mockk<TempoRepository>(relaxed = true)
    private val soundManager = mockk<SoundManager>(relaxed = true)
    private val clockk = mockk<IClockk>(relaxed = true)

    private lateinit var viewModel: IMainActivityVM

    private val stateObserver = mockk<Observer<MainScreenState>>(relaxed = true)
    private val restartDialogObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val whiteClockObserver = mockk<Observer<String>>(relaxed = true)
    private val blackClockObserver = mockk<Observer<String>>(relaxed = true)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { soundManager.playClick() } returns Unit
        every { tempoRepository.retrieveTempo() } returns Pair("00:03:00", "00:02")
        every { tempoRepository.saveTempo(any(), any()) } returns Unit
        viewModel = MainActivityVM(tempoRepository, soundManager, clockk)
    }

    //Arrange
    //Act
    //Assert

    @Test
    fun clockBlackPressedFirstTime_shouldUpdateState() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.WHITE_MOVE,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )
        //Act
        viewModel.onClockBlackPressed()

        //Assert
        verify { stateObserver.onChanged(expectedState) }
    }

    @Test
    fun restartBtnPressed_shouldShowDialog() {
        //Arrange
        viewModel.showRestartDialog.observeForever(restartDialogObserver)

        //Act
        viewModel.onRestartClicked()

        //Assert
        verify { restartDialogObserver.onChanged(true) }
    }

    @Test
    fun restartDismissedBtnPressed_shouldHideDialog() {
        //Arrange
        viewModel.showRestartDialog.observeForever(restartDialogObserver)

        //Act
        viewModel.onRestartDismissedClicked()

        //Assert
        verify { restartDialogObserver.onChanged(false) }
    }

    @Test
    fun restartConfirmedBtnPressed_shouldHideDialog() {
        //Arrange
        viewModel.showRestartDialog.observeForever(restartDialogObserver)
        viewModel.stateLiveData.observeForever(stateObserver)
        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.NEW_GAME,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )

        //Act
        viewModel.onRestartConfirmedClicked()
        viewModel.onRestartDismissedClicked()

        //Assert
        verify { restartDialogObserver.onChanged(false) }
        verify { stateObserver.onChanged(expectedState) }
    }

    @Test
    fun ppBtnPressed_gameRunning_shouldPause() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.PAUSE,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )

        //Act
        viewModel.onClockBlackPressed()
        viewModel.onPlayPauseBtnClicked()

        //Assert
        verify { stateObserver.onChanged(expectedState) }
    }

    @Test
    fun ppBtnPressed_gamePausedOnBlackTurn_shouldRunBlack() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.BLACK_MOVE,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )

        //Act
        viewModel.onClockWhitePressed()
        viewModel.onPlayPauseBtnClicked()
        viewModel.onPlayPauseBtnClicked()

        //Assert
        verify { stateObserver.onChanged(expectedState) }
    }

    @Test
    fun ppBtnPressed_gamePausedOnWhiteTurn_shouldRunWhite() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.WHITE_MOVE,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )

        //Act
        viewModel.onClockBlackPressed()
        viewModel.onPlayPauseBtnClicked()
        viewModel.onPlayPauseBtnClicked()

        //Assert
        verify { stateObserver.onChanged(expectedState) }
    }

    @Test
    fun ppBtnPressed_newGame_shouldDoNothing() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.NEW_GAME,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )

        //Act
        viewModel.onPlayPauseBtnClicked()

        //Assert
        verify(exactly = 1) { stateObserver.onChanged(expectedState) }
    }

//    @Test
//    fun ppBtnPressed_endGameWhite_shouldDoNothing() {
//        //Arrange
//        viewModel.stateLiveData.observeForever(stateObserver)
//        val onGameEndSlot = slot<(GameState) -> Unit>()
//        every { clockk.startClock(any(), any(), capture(onGameEndSlot)) } answers {
//            onGameEndSlot.captured(GameState.END_GAME_WHITE)
//        }
//        val expectedState = MainScreenState(
//            timeFormat = "3' + 2\"",
//            gameState = GameState.END_GAME_WHITE,
//            blackMovesCount = 0,
//            whiteMovesCount = 0
//        )
//
//        //Act
//        clockk.startClock(
//            onGameEnd = onGameEndSlot.captured,
//            updateWhite = {},
//            updateBlack = {}
//        )
//        viewModel.onPlayPauseBtnClicked()
//
//        //Assert
//        verifySequence {
//            stateObserver.onChanged(any())
//            stateObserver.onChanged(expectedState)
//        }
//    }

    @Test
    fun clockBlackPressedInitGame_gameStateShouldUpdate() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        viewModel.clockBlackLiveData.observeForever(blackClockObserver)

        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.WHITE_MOVE,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )

        //Act
        viewModel.onClockBlackPressed()

        //Assert
        verify { soundManager.playClick() }
        verifyOrder {
            stateObserver.onChanged(any())
            stateObserver.onChanged(expectedState)
        }
        verify { blackClockObserver.onChanged(any()) }
    }

    @Test
    fun clockWhitePressedFirstMove_gameStateShouldUpdate() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        viewModel.clockBlackLiveData.observeForever(blackClockObserver)

        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.BLACK_MOVE,
            blackMovesCount = 0,
            whiteMovesCount = 1
        )

        //Act
        viewModel.onClockBlackPressed()
        viewModel.onClockWhitePressed()

        //Assert
        verify(exactly = 2) { soundManager.playClick() }
        verifyOrder {
            stateObserver.onChanged(any())
            stateObserver.onChanged(expectedState)
        }
        verify { blackClockObserver.onChanged(any()) }
    }

    @Test
    fun clockBlackPressedFirstMove_gameStateShouldUpdate() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        viewModel.clockWhiteLiveData.observeForever(whiteClockObserver)

        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.WHITE_MOVE,
            blackMovesCount = 1,
            whiteMovesCount = 1
        )

        //Act
        viewModel.onClockBlackPressed()
        viewModel.onClockWhitePressed()
        viewModel.onClockBlackPressed()

        //Assert
        verify(exactly = 3) { soundManager.playClick() }
        verifyOrder {
            stateObserver.onChanged(any())
            stateObserver.onChanged(expectedState)
        }
        verify(exactly = 2) { whiteClockObserver.onChanged(any()) }
    }

    @Test
    fun customTimeSetClick_gameRunning_shouldPauseGame() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        val expectedState = MainScreenState(
            timeFormat = "3' + 2\"",
            gameState = GameState.PAUSE,
            blackMovesCount = 0,
            whiteMovesCount = 0
        )

        //Act
        viewModel.onClockBlackPressed()
        viewModel.onCustomTimeSetClick()

        //Assert
        verify { stateObserver.onChanged(expectedState) }
    }

    @Test
    fun customTimeSetClick_newGame_shouldDoNothing() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)

        //Act
        viewModel.onCustomTimeSetClick()

        //Assert
        verify(exactly = 1) { stateObserver.onChanged(any()) }
    }

    @Test
    fun customTimeSet_newGame_shouldUpdateTempo() {
        //Arrange
        viewModel.stateLiveData.observeForever(stateObserver)
        viewModel.clockBlackLiveData.observeForever(blackClockObserver)
        viewModel.clockWhiteLiveData.observeForever(whiteClockObserver)

        val newTime = "00:07"
        val newBonus = "00:03"

        //Act
        viewModel.onCustomTimeSet(newTime, newBonus)

        //Assert
        verify { tempoRepository.saveTempo(newTime, newBonus) }
        verify { tempoRepository.retrieveTempo() }
        verify { whiteClockObserver.onChanged(any()) }
        verify { blackClockObserver.onChanged(any()) }
    }
}

class InstantExecutorExtension : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance()
            .setDelegate(object : TaskExecutor() {
                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                override fun postToMainThread(runnable: Runnable) = runnable.run()

                override fun isMainThread(): Boolean = true
            })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}

