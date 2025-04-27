package dev.yahaveliyahu.hw1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import dev.yahaveliyahu.hw1.logic.BGMusic
import dev.yahaveliyahu.hw1.logic.EffectSound
import dev.yahaveliyahu.hw1.logic.RocketPlayer
import dev.yahaveliyahu.hw1.manager.GameManager
import dev.yahaveliyahu.hw1.manager.ObstacleManager
import dev.yahaveliyahu.hw1.utillities.Constants.GAME_SPEED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var leftButton: MaterialButton

    private lateinit var rightButton: MaterialButton

    private lateinit var rocketImage: AppCompatImageView

    private lateinit var hearts: Array<AppCompatImageView>

    private lateinit var player: RocketPlayer

    private lateinit var game: GameManager

    private lateinit var bgMusic: BGMusic

    private lateinit var soundFX: EffectSound

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViews()
        bgMusic = BGMusic()
        soundFX = EffectSound()

        rocketImage.post {
            player = RocketPlayer(rocketImage)
            player.setToStartLane() // Positions the rocket exactly in the middle path

            game = GameManager(hearts)

            initViews()
            bgMusic.play(this)
            startGameLoop()
        }
    }

    private fun findViews() {
        leftButton = findViewById(R.id.main_btn_left)
        rightButton = findViewById(R.id.main_btn_right)
        rocketImage = findViewById(R.id.main_IMG_rocket)
        hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
    }

    private fun initViews() {
        leftButton.setOnClickListener {
            soundFX.playStep(this)
            player.moveLeft()
        }

        rightButton.setOnClickListener {
            soundFX.playStep(this)
            player.moveRight()
        }
    }

    private fun startGameLoop() {
        lifecycleScope.launch {
            var spawnCooldown = 0

            while (true) {
                ObstacleManager.updateObstacles(this@MainActivity, game, player)
                if (spawnCooldown <= 0) {
                    ObstacleManager.spawnObstacle(this@MainActivity)
                    spawnCooldown = 5
                } else {
                    spawnCooldown--
                }
                delay(GAME_SPEED)
            }
        }
    }
}