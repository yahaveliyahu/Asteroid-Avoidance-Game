package dev.yahaveliyahu.hw1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        val finalScore = intent.getIntExtra("final_score", 0)
        val scoreText = findViewById<TextView>(R.id.score_LBL_score)
        scoreText.text = getString(R.string.game_over_score, finalScore)

        findViewById<Button>(R.id.score_BTN_retry).apply {
            text = context.getString(R.string.play_again_in_the_same_mode_button)
            setOnClickListener {
                val intent = Intent(this@GameOverActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        findViewById<Button>(R.id.score_BTN_menu).apply {
            text = context.getString(R.string.back_to_menu)
            setOnClickListener {
                val intent = Intent(this@GameOverActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}