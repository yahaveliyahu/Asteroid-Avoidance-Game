package dev.yahaveliyahu.hw1.logic

import android.content.Context
import android.media.MediaPlayer
import dev.yahaveliyahu.hw1.R

object EffectSound {
    private val players = mutableMapOf<Int, MediaPlayer?>()

    private fun playSound(context: Context, resId: Int) {
        stopSound(resId)

        val mediaPlayer = MediaPlayer.create(context, resId)
        if (mediaPlayer == null) {
            // Failed to create player - do not continue
            return
        }

        players[resId] = mediaPlayer
        mediaPlayer.setOnCompletionListener {
            it.release()
            players.remove(resId)
        }
        mediaPlayer.start()
    }

    private fun stopSound(resId: Int) {
        players[resId]?.stop()
        players[resId]?.release()
        players.remove(resId)
    }

    fun playStep(context: Context) = playSound(context, R.raw.step_sound)
    fun playCrash(context: Context) = playSound(context, R.raw.crash_sound)
    fun playCoin(context: Context) = playSound(context, R.raw.coin)
    fun playHeart(context: Context) = playSound(context, R.raw.heart_up)
    fun playBoost(context: Context) = playSound(context, R.raw.boost)
    fun playBrake(context: Context) = playSound(context, R.raw.brake)

    fun stopBoost() = stopSound(R.raw.boost)
    fun stopBrake() = stopSound(R.raw.brake)
}
