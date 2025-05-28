package dev.yahaveliyahu.hw1.manager

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.widget.AppCompatImageView
import dev.yahaveliyahu.hw1.utillities.Constants.INITIAL_LIVES
import dev.yahaveliyahu.hw1.utillities.ToastUtil.safeToast

class GameManager(private val hearts: Array<AppCompatImageView>) {
    private var lives = INITIAL_LIVES
    private var score = 0
    private var distance = 0
    private var lastFinalScore = 0



    fun loseLife(context: Context) {
        if (lives > 0) {
            lives--
            vibrateAndToast(context)
            updateHearts()
        }
    }

    fun addScore(points: Int) {
        score += points
    }

    fun getScore(): Int = score

    fun getLives(): Int = lives

    fun gainLife() {
        if (lives < INITIAL_LIVES) {
            lives++
            updateHearts()
        }
    }

    fun addDistance(units: Int) {
        distance += units
    }

    fun getDistance(): Int = distance


    private fun vibrateAndToast(context: Context) {
        safeToast(context, "Crash!")
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
        lastFinalScore = score // Saving the score before resetting
        lives = INITIAL_LIVES
        score = 0
        distance = 0
        updateHearts()
    }
}

