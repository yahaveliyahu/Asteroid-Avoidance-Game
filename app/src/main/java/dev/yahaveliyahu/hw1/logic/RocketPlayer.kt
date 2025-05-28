package dev.yahaveliyahu.hw1.logic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import dev.yahaveliyahu.hw1.utillities.Constants.NUM_LANES
import dev.yahaveliyahu.hw1.utillities.Constants.NUM_ROWS
import dev.yahaveliyahu.hw1.utillities.Constants.START_LANE
import dev.yahaveliyahu.hw1.utillities.ToastUtil.safeToast

class RocketPlayer(private val rocketView: AppCompatImageView) {

    private var currentLane = START_LANE.coerceIn(0, NUM_LANES - 1)

    fun moveLeft() {
        if (currentLane <= 0) {
            giveFeedback()
            return
        }
        currentLane--
        updateRocketPosition()
    }

    fun moveRight() {
        if (currentLane >= NUM_LANES - 1) {
            giveFeedback()
            return
        }
        currentLane++
        updateRocketPosition()
    }

    fun setToStartLane() {
        currentLane = START_LANE.coerceIn(0, NUM_LANES - 1)
        updateRocketPosition()
    }

    private fun updateRocketPosition() {
        rocketView.post {
            val parent = rocketView.parent as? ViewGroup ?: return@post
            val layoutWidth = parent.width.toFloat()
            val laneWidth = layoutWidth / NUM_LANES
            val centerX = (currentLane + 0.5f) * laneWidth
            rocketView.translationX = centerX - rocketView.width / 2f

        }
    }

    private fun giveFeedback() {
        val context = rocketView.context
        safeToast(context, "There are no more paths in this direction")
        try {
            // Pull VibratorManager (API>=31) or regular Vibrator (API<31)
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                    ?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            // Vibrate for 100ms if vibrator is present
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Placing the rocket on the bottom row
    fun getPosition(): Pair<Int, Int> = Pair(NUM_ROWS - 3, currentLane)
}
