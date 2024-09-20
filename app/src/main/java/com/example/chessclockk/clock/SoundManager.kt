package com.example.chessclockk.clock

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.chessclockk.R
import javax.inject.Inject

class SoundManager @Inject constructor(context: Context) {
    private val soundPool: SoundPool
    private val clickSoundId: Int
    private val beepSoundId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        clickSoundId = soundPool.load(context, R.raw.press, 1)
        beepSoundId = soundPool.load(context, R.raw.beep, 1)
    }

    fun playClick() {
        soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playBeep() {
        soundPool.play(beepSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}

