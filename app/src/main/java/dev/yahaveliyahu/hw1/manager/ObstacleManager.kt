package dev.yahaveliyahu.hw1.manager

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import dev.yahaveliyahu.hw1.R
import dev.yahaveliyahu.hw1.logic.EffectSound
import dev.yahaveliyahu.hw1.logic.RocketPlayer
import kotlin.random.Random
import dev.yahaveliyahu.hw1.utillities.Constants

object ObstacleManager {
    private val stones = mutableListOf<AppCompatImageView>()
    private const val FALL_SPEED = 100f // מספר פיקסלים שכל אבן יורדת בכל טיק

    fun updateObstacles(context: Context, game: GameManager, player: RocketPlayer) {
        val iterator = stones.iterator()
        while (iterator.hasNext()) {
            val stone = iterator.next()
            stone.translationY += FALL_SPEED

            val rocketRect = Rect().apply { player.getView().getHitRect(this) }
            val stoneRect = Rect().apply { stone.getHitRect(this) }

            val hit = Rect.intersects(rocketRect, stoneRect)
            val outOfScreen = stone.y > 2000f

            if (hit) {
                game.loseLife(context)
                EffectSound().playCrash(context)
                (stone.parent as? ViewGroup)?.removeView(stone)
                iterator.remove()

                // אם האבן עברה את המסך – ניפטר ממנה
            } else if (outOfScreen) {
                (stone.parent as? ViewGroup)?.removeView(stone)
                iterator.remove()
            }
        }

        // איפוס המשחק אם נגמרו החיים
        if (game.isGameOver()) {
            game.reset()
            clearObstacles(context)
        }
    }

    fun spawnObstacle(context: Context) {
        val parent = (context as? Activity)?.findViewById<ViewGroup>(R.id.gameLayout) ?: return

        val layoutWidth = parent.width.toFloat()
        val laneWidth = layoutWidth / Constants.NUM_LANES
        val numStones = Random.nextInt(1, 3) // בין 1 ל-2 אבנים
        val lanesToUse = (0 until Constants.NUM_LANES).shuffled().take(numStones)

        for (lane in lanesToUse) {
            val obstacle = AppCompatImageView(context).apply {
                setImageResource(R.drawable.block)
                layoutParams = ViewGroup.LayoutParams(150, 150)

                val laneCenterX = (lane + 0.5f) * laneWidth
                val x = laneCenterX - 75f // חצי מהרוחב של האבן (150/2)
                translationX = x
                translationY = 0f
                tag = "obstacle"
            }

            parent.addView(obstacle)
            stones.add(obstacle)
        }
    }


    private fun clearObstacles(context: Context) {
        val parent = (context as? Activity)?.findViewById<ViewGroup>(R.id.gameLayout) ?: return
        for (stone in stones) {
            parent.removeView(stone)
        }
        stones.clear()
    }
}