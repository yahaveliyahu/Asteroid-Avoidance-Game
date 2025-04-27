package dev.yahaveliyahu.hw1.manager

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import dev.yahaveliyahu.hw1.R
import dev.yahaveliyahu.hw1.logic.EffectSound
import dev.yahaveliyahu.hw1.logic.RocketPlayer
import dev.yahaveliyahu.hw1.utillities.Constants.NUM_COLS
import dev.yahaveliyahu.hw1.utillities.Constants.NUM_LANES
import dev.yahaveliyahu.hw1.utillities.Constants.NUM_ROWS
import kotlin.random.Random

object ObstacleManager {
    private val grid = Array(NUM_ROWS) { arrayOfNulls<AppCompatImageView>(NUM_COLS) }

    fun spawnObstacle(context: Context) {
        val parent = (context as? Activity)?.findViewById<ViewGroup>(R.id.gameLayout) ?: return
        val laneWidth = parent.width.toFloat() / NUM_COLS
        val numStones = Random.nextInt(1, 3) // We will create 1 or 2 stones
        val lanesToUse = (0 until NUM_COLS).shuffled().take(numStones)

        for (lane in lanesToUse) {
            // If the cell is empty – create a stone, otherwise we automatically skip it
            if (grid[0][lane] == null) {
                val stone = AppCompatImageView(context).apply {
                    setImageResource(R.drawable.stone)
                    layoutParams = ViewGroup.LayoutParams(150, 150)
                    tag = "obstacle"
                }

                val x = lane * laneWidth + (laneWidth - 150f) / 2f
                stone.translationX = x
                stone.translationY = 0f //Set translationY to 0 so the stone starts in the top row (no vertical translation)

                parent.addView(stone)
                grid[0][lane] = stone
            }
        }
    }

    fun updateObstacles(context: Context, game: GameManager, player: RocketPlayer) {
        val parent = (context as? Activity)?.findViewById<ViewGroup>(R.id.gameLayout) ?: return
        val cellHeight = parent.height.toFloat() / NUM_ROWS

        // We will start from the bottom rows so as not to delete a stone and run over it in the next row
        for (row in NUM_ROWS - 1 downTo 0) {
            for (col in 0 until NUM_LANES) {
                val stone = grid[row][col]  // The location of the stone in the matrix
                if (stone != null) {
                    // Collision test
                    val (playerRow, playerCol) = player.getPosition() // The location of the rocket in the matrix
                    if (playerRow == row && playerCol == col) { // The positions are equal = a collision has occurred
                        parent.removeView(stone)
                        grid[row][col] = null
                        game.loseLife(context)
                        EffectSound().playCrash(context)
                    } else {
                        // Moving the stone downwards
                        if (row + 1 < NUM_ROWS && grid[row + 1][col] == null) {
                            grid[row + 1][col] = stone
                            grid[row][col] = null
                            stone.translationY = (row + 1) * cellHeight
                            // If a stone reaches the bottom row without colliding, it will be deleted
                        } else if (row == NUM_ROWS - 1) {
                            parent.removeView(stone)
                            grid[row][col] = null
                        }
                    }
                }
            }
        }
        if (game.isGameOver()) {
            game.reset()
            clearObstacles(context)
        }
    }

    private fun clearObstacles(context: Context) {
        val parent = (context as? Activity)?.findViewById<ViewGroup>(R.id.gameLayout) ?: return
        for (row in 0 until NUM_ROWS) {
            for (col in 0 until NUM_COLS) {
                grid[row][col]?.let {
                    parent.removeView(it)
                    grid[row][col] = null
                }
            }
        }
    }
}
