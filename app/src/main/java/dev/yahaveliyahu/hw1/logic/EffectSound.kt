package dev.yahaveliyahu.hw1.logic

import android.content.Context
import android.media.MediaPlayer
import dev.yahaveliyahu.hw1.R

class EffectSound {
    fun playStep(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.step_sound)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
    fun playCrash(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.crash_sound)
        mediaPlayer.start()

        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
}