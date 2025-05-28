package dev.yahaveliyahu.hw1

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dev.yahaveliyahu.hw1.logic.BGMusic
import dev.yahaveliyahu.hw1.logic.EffectSound
import dev.yahaveliyahu.hw1.logic.RocketPlayer
import dev.yahaveliyahu.hw1.manager.GameManager
import dev.yahaveliyahu.hw1.manager.ObstacleManager
import dev.yahaveliyahu.hw1.utillities.Constants.GAME_SPEED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.view.MotionEvent
import dev.yahaveliyahu.hw1.utillities.ToastUtil.safeToast
import kotlinx.coroutines.Job


class MainActivity : AppCompatActivity() {

    private lateinit var leftButton: MaterialButton

    private lateinit var rightButton: MaterialButton

    private lateinit var boostButton: MaterialButton

    private lateinit var rocketImage: AppCompatImageView

    private lateinit var hearts: Array<AppCompatImageView>

    private lateinit var scoreText: MaterialTextView

    private lateinit var distanceText: MaterialTextView

    private lateinit var player: RocketPlayer

    private lateinit var game: GameManager

    private lateinit var bgMusic: BGMusic

    private lateinit var sensorManager: SensorManager

    private var accelerometer: Sensor? = null

    private lateinit var sensorEventListener: SensorEventListener

    private var sensorMode = false // Default: Screen tilt

    private var lastX = 0f

    private var currentSpeed = GAME_SPEED

    private var gameRunning = true

    private var lastFinalScore = 0

    private var difficultyLevel = 1

    private var baseSpeed = GAME_SPEED

    private var isBoosting = false

    private var isBraking = false

    private var gameLoopJob: Job? = null

    internal var isActive = true // To make it available under all files located under app

    companion object {
        var activeToast: Toast? = null
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Retrieving game state from SharedPreferences
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        sensorMode = prefs.getBoolean("sensor_mode", false)

        findViews()
        bgMusic = BGMusic()

        // If using a sensor – Sensor settings
        if (sensorMode) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            setupSensorListener()
            hideButtons()
        }

        // rocketImage must be ready before creating player
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
        scoreText = findViewById(R.id.main_LBL_score)
        distanceText = findViewById(R.id.main_LBL_distance)
        boostButton = findViewById(R.id.main_btn_boost)
        hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
    }

    private fun hideButtons() {
        leftButton.visibility = View.GONE
        rightButton.visibility = View.GONE
        boostButton.visibility = View.GONE
    }

    private fun initViews() {
        leftButton.setOnClickListener {
            EffectSound.playStep(this)
            player.moveLeft()
        }

        rightButton.setOnClickListener {
            EffectSound.playStep(this)
            player.moveRight()
        }
        // Listens to touch events on the Boost button, to detect both long press and release
        boostButton.setOnTouchListener { view, event ->
            when (event.action) {
                // The user started pressing the button
                MotionEvent.ACTION_DOWN -> {
                    view.performClick() // Necessary to support accessibility and button animations
                    if (!isBoosting) {
                        isBoosting = true
                        EffectSound.playBoost(this)
                        currentSpeed = (baseSpeed * 0.3).toLong()
                    }
                }
                // The user released the click or it was canceled
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isBoosting = false
                    EffectSound.stopBoost()
                    currentSpeed = baseSpeed
                }
            }
            true // Indicates that we have handled the event and do not need to pass it on
        }
    }

    private fun startGameLoop() {
        // Stop any existing loop first, if any
        gameLoopJob?.cancel()
        // Start a new loop
        gameLoopJob = lifecycleScope.launch {
            var spawnCooldown = 0
            gameRunning = true

            while (gameRunning) {
                val currentScore = game.getScore() // Saving score before resetting

                val currentDistance = game.getDistance()
                // Difficulty level increases every time Distance is a multiple of 25
                if (currentDistance > 0 && currentDistance % 20 == 0
                    && currentDistance / 20 >= difficultyLevel) {
                    difficultyLevel++
                    baseSpeed = (baseSpeed * 0.9).toLong().coerceAtLeast(150L)
                    currentSpeed = baseSpeed
                    safeToast(this@MainActivity, "Difficulty increased!")
                }
                val didReset = ObstacleManager.updateObstacles(this@MainActivity, game, player) {
                    refreshUI()
                }
                if (didReset) {
                    lastFinalScore = currentScore
                    refreshUI()
                    gameRunning = false  // Stopping the loop here

                    if (MyHighScores.isNewHighScore(this@MainActivity, lastFinalScore)) {
                        showHighScoreDialog(lastFinalScore)
                    } else {
                        val intent = Intent(this@MainActivity, GameOverActivity::class.java)
                        intent.putExtra("final_score", lastFinalScore)
                        startActivity(intent)
                        finish()
                    }
                }
                // Continue only if the game is running.
                if (gameRunning) {
                    if (spawnCooldown <= 0) {
                        ObstacleManager.spawnObstacle(this@MainActivity, game)
                        spawnCooldown = 3
                    } else {
                        spawnCooldown--
                    }
                    game.addDistance(1)
                    refreshUI()

                    if (!sensorMode && !isBoosting) {
                        currentSpeed = baseSpeed
                    }

                    delay(currentSpeed)
                }
            }
        }
    }

    private fun refreshUI() {
        updateScoreDisplay()
        updateDistanceDisplay()
    }

    private fun showHighScoreDialog(score: Int = lastFinalScore) {
        val input = EditText(this)
        input.hint = "Enter your name"

        val dialog = AlertDialog.Builder(this)
            .setTitle("New High Score!")
            .setMessage("Your score: $score\nEnter your name:")
            .setView(input)
            .setPositiveButton("Next", null)
            .setCancelable(false)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isEmpty()) {
                input.error = "Name is required!"
            } else {
                // Location permissions
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                } else {
                    openLocationPicker(name, score)
                    dialog.dismiss()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun openLocationPicker(name: String, score: Int) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val highScore = HighScore(name, score, location.latitude, location.longitude)
                MyHighScores.saveScore(this, highScore)
                safeToast(this, "High score saved!")
                val intent = Intent(this, HighScoresActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                safeToast(this, "Could not get location")
            }
        }
    }

    private fun setupSensorListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0] // Tilt left/right
                    val y = it.values[1] // Tilt forward/backward

                    val threshold  = 2.5f // Sensitivity to moving left/right
                    val thresholdY = 3.0f // Acceleration/deceleration sensitivity

                    // Left/right control – like arrow buttons
                    if (x - lastX > threshold) {
                        EffectSound.playStep(this@MainActivity)
                        player.moveLeft()
                        lastX = x
                    } else if (lastX - x > threshold) {
                        EffectSound.playStep(this@MainActivity)
                        player.moveRight()
                        lastX = x
                    }

                    // Tilt forward/backward
                    if (y < -thresholdY) {
                        if (!isBoosting) {
                            isBoosting = true
                            isBraking = false
                            EffectSound.playBoost(this@MainActivity)
                            EffectSound.stopBrake()
                            // Tilt forward to accelerate
                            currentSpeed = (baseSpeed * 0.3).toLong()
                        }
                    } else if (y > thresholdY) {
                        if (!isBraking) {
                            isBraking = true
                            isBoosting = false
                            EffectSound.playBrake(this@MainActivity)
                            EffectSound.stopBoost()
                            // Tilt back to slow down
                            currentSpeed = (baseSpeed * 1.9).toLong()
                        }
                    } else {
                        if (isBoosting || isBraking) {
                            isBoosting = false
                            isBraking = false
                            // Not tilted — return to normal speed
                            EffectSound.stopBoost()
                            EffectSound.stopBrake()
                            currentSpeed = baseSpeed
                        }
                    }
                    // ️ Speed limit to reasonable values
                    currentSpeed = currentSpeed.coerceIn(150L, 1000L)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            // Don't need it in the game
        }
    }

        private fun updateScoreDisplay() {
            scoreText.text = getString(R.string.score_display, game.getScore())
        }

        private fun updateDistanceDisplay() {
            distanceText.text = getString(R.string.distance_display, game.getDistance())
        }

        // To save battery and memory, you should start/stop a sensor at the onResume/onPause times
        override fun onResume() {
            super.onResume()
            isActive = true
            if (sensorMode) {
                accelerometer?.also { acc ->
                    sensorManager.registerListener(sensorEventListener, acc, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
            bgMusic.play(this)
            if (!gameRunning) {
                startGameLoop()
            }
        }

        override fun onPause() {
            super.onPause()
            isActive = false
            activeToast?.cancel()
            activeToast = null
            if (sensorMode) {
                sensorManager.unregisterListener(sensorEventListener)
            }
            bgMusic.stop()
            EffectSound.stopBoost()
            EffectSound.stopBrake()
            gameRunning = false
            gameLoopJob?.cancel()
        }
    }