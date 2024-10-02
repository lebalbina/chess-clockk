package com.example.chessclockk

import com.example.chessclockk.usecase.TempoRepository
import com.example.chessclockk.vm.GameState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO event-driven game state
class NewClock @Inject constructor(
    private val tempoRepository: TempoRepository,
    private val timeProvider: ITimeProvider,
    private val dispatcher: CoroutineDispatcher
) : IClockk {
    var whiteMillisRemaining: Long = 3000L
        private set

    var blackMillisRemaining: Long = 3000L
        private set

    var bonus: Long = 2000L
        private set

    private var gameState: GameState = GameState.NEW_GAME

    private lateinit var job: Job

    init {
        val (tempo, bonus) = tempoRepository.retrieveTempo()
        blackMillisRemaining = tempo.convertHHMMSSToMillis()
        whiteMillisRemaining = tempo.convertHHMMSSToMillis()
        this.bonus = bonus.convertHHMMSSToMillis()
    }

    override fun startClock(
        updateWhite: (Long) -> Unit,
        updateBlack: (Long) -> Unit,
        onGameEnd: (GameState) -> Unit
    ) {
        job = CoroutineScope(dispatcher).launch {
            while (isActive) {
                runClocks(updateWhite, updateBlack, onGameEnd)
                delay(100L)
            }
        }
    }

    override fun setTempo(tempo: String, bonus: String) {
        tempoRepository.saveTempo(tempo, bonus)
        blackMillisRemaining = tempo.convertHHMMSSToMillis()
        whiteMillisRemaining = tempo.convertHHMMSSToMillis()
        this.bonus = bonus.convertHHMMSSToMillis()
    }

    override fun updateGameState(gameState: GameState) {
        this.gameState = gameState
    }

    override fun stopClock() {
        job.cancel()
    }

    private suspend fun runClocks(
        updateWhite: (Long) -> Unit,
        updateBlack: (Long) -> Unit,
        onGameEnd: (GameState) -> Unit
    ) {
        var counter = 0
        when (gameState) {
            GameState.WHITE_MOVE -> {
                clockTick(
                    updateUi = { updateWhite(it) },
                    updatePlayerMillis = {
                        counter += 1
                        println("updatePlayerMillis white lambda counter = $counter")
                        whiteMillisRemaining = it
                    },
                    gameStateToCheck = GameState.WHITE_MOVE,
                    playerMillis = whiteMillisRemaining,
                    onGameEnd = { onGameEnd(it) }
                )
            }

            GameState.BLACK_MOVE -> {
                clockTick(
                    updateUi = { updateBlack(it) },
                    updatePlayerMillis = {
                        blackMillisRemaining = it
                        println("updatePlayerMillis black lambda counter = $counter")
                    },
                    gameStateToCheck = GameState.BLACK_MOVE,
                    playerMillis = blackMillisRemaining,
                    onGameEnd = { onGameEnd(it) }
                )
            }

            else -> {

            }
        }
    }

    private suspend fun clockTick(
        updateUi: (Long) -> Unit,
        onGameEnd: (GameState) -> Unit,
        updatePlayerMillis: (Long) -> Unit,
        playerMillis: Long,
        gameStateToCheck: GameState
    ) {
        val startTime = timeProvider.currentTimeMillis()
        var lastUpdateTime = startTime
        var remainingPlayerMillis = playerMillis
        var lastBeepSound = startTime

        while (remainingPlayerMillis > 0 && gameState == gameStateToCheck) {
            val currentTime = timeProvider.currentTimeMillis()
            val elapsedTime = currentTime - lastUpdateTime
            remainingPlayerMillis -= elapsedTime
            println("current time = $currentTime, elapsed time = $elapsedTime, remaining millis= $remainingPlayerMillis")
            lastUpdateTime = currentTime
            //extract method
            updatePlayerMillis(remainingPlayerMillis)
            updateUi(remainingPlayerMillis)

            if (remainingPlayerMillis < 1000L) {
                onGameEnd(
                    if (gameStateToCheck == GameState.WHITE_MOVE) GameState.END_GAME_WHITE
                    else GameState.END_GAME_BLACK
                )
                break
            }

            if (remainingPlayerMillis < 6000L && currentTime - lastBeepSound >= 1000L) {
//                soundManager.playBeep()
                lastBeepSound = currentTime
            }
            delay(100)
        }
    }

    private fun addBonus(updateMillis: (Long) -> Unit) {

    }


}