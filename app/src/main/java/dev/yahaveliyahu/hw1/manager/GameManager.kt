package dev.yahaveliyahu.hw1.manager

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import dev.yahaveliyahu.hw1.utillities.Constants.INITIAL_LIVES

class GameManager(private val hearts: Array<AppCompatImageView>) {
    private var lives = INITIAL_LIVES

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
            // Retrieve vibrator according to SDK version
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Using VibratorManager in API >= 31
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                        as? VibratorManager
                vm?.defaultVibrator
            } else {
                // The Classic Vibrator in API < 31
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            // Short vibration activation
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    300,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun updateHearts() {
        for (i in hearts.indices) {
            hearts[i].visibility = if (i < lives) AppCompatImageView.VISIBLE else AppCompatImageView.INVISIBLE
        }
    }

    fun isGameOver(): Boolean = lives == 0

    fun reset() {
        lives = INITIAL_LIVES
        updateHearts()
    }
}

