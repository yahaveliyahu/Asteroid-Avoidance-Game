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
import dev.yahaveliyahu.hw1.utillities.ToastUtil.safeToast
import kotlin.random.Random

object ObstacleManager {
    private val grid = Array(NUM_ROWS) { arrayOfNulls<AppCompatImageView>(NUM_COLS) }

    fun spawnObstacle(context: Context, game: GameManager) {
        val parent = (context as? Activity)?.findViewById<ViewGroup>(R.id.gameLayout) ?: return
        val laneWidth = parent.width.toFloat() / NUM_COLS
        val numItems = Random.nextInt(1, 5)
        val lanesToUse = (0 until NUM_COLS).shuffled().take(numItems)

        for (lane in lanesToUse) {
            // If the cell is empty – create a stone, otherwise we automatically skip it
            if (grid[0][lane] == null) {
                val item = createRandomItem(context, game)
                val x = lane * laneWidth + (laneWidth - item.layoutParams.width) / 2f
                item.translationX = x
                item.translationY = 0f
                parent.addView(item)
                grid[0][lane] = item
            }
        }
    }

    private fun isHeartOnScreen(): Boolean {
        for (row in grid) {
            for (item in row) {
                if (item?.tag == "heart") return true
            }
        }
        return false
    }

    private fun createRandomItem(context: Context, game: GameManager): AppCompatImageView {
        val coinTypes = listOf(
            Pair(R.drawable.coin_1, 1),
            Pair(R.drawable.coin_5, 5),
            Pair(R.drawable.coin_10, 10)
        )
        // Try to create a heart – only if no heart exists and life is less than 3
        if (!isHeartOnScreen() && game.getLives() < 3 && Random.nextFloat() < 0.1f) { // 10% heart if heart is missing
            return AppCompatImageView(context).apply {
                setImageResource(R.drawable.heart)
                layoutParams = ViewGroup.LayoutParams(120, 120)
                tag = "heart"
            }
        }
        if (Random.nextFloat() < 0.7f) { // 70% stone, 30% coin
            return AppCompatImageView(context).apply {
                setImageResource(R.drawable.stone)
                layoutParams = ViewGroup.LayoutParams(150, 150)
                tag = "obstacle"
            }
        }
        val (image, value) = coinTypes.random()
        return AppCompatImageView(context).apply {
            setImageResource(image)
            layoutParams = ViewGroup.LayoutParams(130, 130)
            tag = "coin:$value"
        }
    }




    fun updateObstacles(context: Context, game: GameManager, player: RocketPlayer,
        updateScoreUI: () -> Unit): Boolean {
        val parent = (context as? Activity)?.findViewById<ViewGroup>(R.id.gameLayout) ?: return false
        val cellHeight = parent.height.toFloat() / NUM_ROWS

        // We will start from the bottom rows so as not to delete a stone and run over it in the next row
        for (row in NUM_ROWS - 1 downTo 0) {
            for (col in 0 until NUM_LANES) {
                val item = grid[row][col]
                if (item != null) {
                    // Collision test
                    val (playerRow, playerCol) = player.getPosition() // The location of the rocket in the matrix
                    // The positions are equal = a collision has occurred
                    if ((row == playerRow || row == playerRow - 1) && playerCol == col) {
                        val tag = item.tag as? String ?: ""
                        if (tag.startsWith("coin:")) {
                            val value = tag.removePrefix("coin:").toIntOrNull() ?: 0
                            game.addScore(value)
                            EffectSound.playCoin(context)
                            updateScoreUI()
                        } else if (tag == "heart") {
                            game.gainLife()
                            safeToast(context, "❤️ Life +1!")
                            EffectSound.playHeart(context)
                        } else {
                            game.loseLife(context)
                            EffectSound.playCrash(context)
                        }
                        parent.removeView(item)
                        grid[row][col] = null
                    } else {
                        // Moving the item downwards
                        if (row + 1 < NUM_ROWS && grid[row + 1][col] == null) {
                            grid[row + 1][col] = item
                            grid[row][col] = null
                            item.translationY = (row + 1) * cellHeight
                            // If a item reaches the bottom row without colliding, it will be deleted
                        } else if (row == NUM_ROWS - 1) {
                            parent.removeView(item)
                            grid[row][col] = null
                        }
                    }
                }
            }
        }
        if (game.isGameOver()) {
            game.reset()
            clearObstacles(context)
            return true
        }
        return false
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

