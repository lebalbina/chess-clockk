package com.example.chessclockk

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.chessclockk.clock.SoundManager
import com.example.chessclockk.usecase.TempoRepository
import com.example.chessclockk.vm.GameState
import com.example.chessclockk.vm.IMainActivityVM
import com.example.chessclockk.vm.IMainActivityVM.*
import com.example.chessclockk.vm.MainActivityVM
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor

@ExtendWith(InstantExecutorExtension::class)
class MainActivityVMTest {

    private val tempoRepository = mockk<TempoRepository>(relaxed = true)
    private val soundManager = mockk<SoundManager>(relaxed = true)
    private val clockk = mockk<IClockk>(relaxed = true)

    private lateinit var viewModel: IMainActivityVM

    private val stateObserver = mockk<Observer<MainScreenState>>(relaxed = true)
    private val restartDialogObserver = mockk<Observer<Boolean>>(relaxed = true)

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
