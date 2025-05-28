package dev.yahaveliyahu.hw1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val buttonModeBtn = findViewById<MaterialButton>(R.id.menu_btn_button_mode)
        val sensorModeBtn = findViewById<MaterialButton>(R.id.menu_btn_sensor_mode)
        val highScoresBtn = findViewById<MaterialButton>(R.id.menu_btn_high_scores)

        buttonModeBtn.setOnClickListener {startGame(false)}
        sensorModeBtn.setOnClickListener {startGame(true)}
        highScoresBtn.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)}
    }

    private fun startGame(sensorMode: Boolean) {
        // Saving the last game state
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.edit().putBoolean("sensor_mode", sensorMode).apply()

        // Starting the game
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
