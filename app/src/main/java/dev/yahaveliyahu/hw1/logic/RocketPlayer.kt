package dev.yahaveliyahu.hw1.logic

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import dev.yahaveliyahu.hw1.utillities.Constants

class RocketPlayer(private val rocketView: AppCompatImageView) {

    private var currentLane = Constants.START_LANE.coerceIn(0, Constants.NUM_LANES - 1)
    fun moveLeft() {
        if (currentLane <= -1) {
            giveFeedback()
            return
        }
        currentLane = (currentLane - 1).coerceIn(-1, Constants.NUM_LANES - 1)
        updateRocketPosition()
    }

    fun moveRight() {
        if (currentLane >= Constants.NUM_LANES - 2) {
            giveFeedback()
            return
        }
        currentLane = (currentLane + 1).coerceIn(0, Constants.NUM_LANES - 1)
        updateRocketPosition()
    }

    fun setToStartLane() {
        currentLane = Constants.START_LANE.coerceIn(-1, Constants.NUM_LANES)
        updateRocketPosition()
    }

    private fun updateRocketPosition() {
        rocketView.post {
            val parent = rocketView.parent as? ViewGroup ?: return@post
            val layoutWidth = parent.width.toFloat()
            val laneWidth = layoutWidth / Constants.NUM_LANES
            val rocketWidth = rocketView.width
            val x = currentLane * laneWidth + (laneWidth - rocketWidth) / 2f
            rocketView.translationX = x
        }
    }

    private fun giveFeedback() {
        val context = rocketView.context
        Toast.makeText(context, "There are no more paths in this direction", Toast.LENGTH_SHORT).show()
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun getView(): View = rocketView
}
