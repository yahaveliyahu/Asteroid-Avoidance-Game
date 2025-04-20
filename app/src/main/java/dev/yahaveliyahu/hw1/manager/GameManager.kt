package dev.yahaveliyahu.hw1.manager

import android.content.Context
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import dev.yahaveliyahu.hw1.R
import dev.yahaveliyahu.hw1.logic.RocketPlayer
import dev.yahaveliyahu.hw1.utillities.Constants

class GameManager(private val hearts: Array<AppCompatImageView>) {
    private var lives = Constants.INITIAL_LIVES

    fun loseLife(context: Context) {
        if (lives > 0) {
            lives--
            vibrateAndToast(context)
            updateHearts()
        }
    }

    private fun vibrateAndToast(context: Context) {
        Toast.makeText(context, "Crash!", Toast.LENGTH_SHORT).show()
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (_: Exception) {
        }
    }

    private fun updateHearts() {
        for (i in hearts.indices) {
            hearts[i].visibility = if (i < lives) AppCompatImageView.VISIBLE else AppCompatImageView.INVISIBLE
        }
    }

    fun isGameOver(): Boolean = lives == 0

    fun reset() {
        lives = Constants.INITIAL_LIVES
        updateHearts()
    }

    fun checkCollision(player: RocketPlayer) {
        val rocketRect = Rect().apply { player.getView().getHitRect(this) }
        val parent = player.getView().parent as? ViewGroup ?: return

        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            if (view is AppCompatImageView && view.tag == "obstacle") {
                val obstacleRect = Rect().apply { view.getHitRect(this) }
                if (Rect.intersects(rocketRect, obstacleRect)) {
                    playCrashSound(player.getView().context)
                    loseLife(player.getView().context)
                    parent.removeView(view)
                    break
                }
            }
        }
    }

    private fun playCrashSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.crash_sound)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
}

