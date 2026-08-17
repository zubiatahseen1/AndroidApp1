package com.example.androidapp1

import android.os.Bundle
import android.os.CountDownTimer   // for the 20-second countdown
import android.widget.Button       // so we can work with the Button interface elements
import android.widget.TextView     // so we can work with the TextView interface elements
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Variables that will be LINKED to the interface elements in activity_main.xml.
    // "lateinit" means we promise to initialize them before use (we do, in onCreate).
    private lateinit var timerText: TextView   // shows the seconds remaining
    private lateinit var countText: TextView   // shows the number of taps
    private lateinit var tapButton: Button     // the button the user taps
    private lateinit var resetButton: Button   // resets the game

    // Game state
    private var tapCount = 0                          // how many times the user has tapped
    private var gameRunning = false                   // is a game currently in progress?
    private var countDownTimer: CountDownTimer? = null // the 20-second countdown (null until a game starts)

    // Constants for the game — 20 seconds total, ticking once every second.
    companion object {
        const val GAME_TIME_MS = 20000L      // total game time in milliseconds
        const val TIMER_INTERVAL_MS = 1000L  // how often onTick fires (every 1 second)
    }

    // onCreate is the ENTRY POINT of an Android activity — like main() in other programs.
    // It runs when the app loads.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // lets the app background extend to the edges of the screen

        // Load activity_main.xml as this screen's interface.
        // R = the auto-generated Resources class that bridges Kotlin code and XML.
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Link the Kotlin variables to the XML interface elements by their IDs.
        timerText = findViewById(R.id.timerText)
        countText = findViewById(R.id.countText)
        tapButton = findViewById(R.id.tapButton)
        resetButton = findViewById(R.id.resetButton)

        // Event listeners: run the code in the braces whenever each button is clicked.
        tapButton.setOnClickListener { tapButtonPressed() }
        resetButton.setOnClickListener { resetGame() }
    }

    // Called every time the tap button is pressed.
    private fun tapButtonPressed() {
        // If a finished game is on screen (not running, but taps were recorded),
        // ignore further taps until the user presses Reset.
        if (!gameRunning && tapCount > 0) return

        // The FIRST tap starts the game (and also counts as a tap).
        if (!gameRunning) startGame()

        // Count the tap and update the display.
        tapCount++
        countText.text = tapCount.toString()
    }

    // Starts a new 20-second game.
    private fun startGame() {
        gameRunning = true
        tapButton.isEnabled = true

        // Create the countdown: GAME_TIME_MS total, ticking every TIMER_INTERVAL_MS.
        countDownTimer = object : CountDownTimer(GAME_TIME_MS, TIMER_INTERVAL_MS) {

            // onTick runs once per interval (every second) while time remains.
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerText.text = secondsRemaining.toString()
            }

            // onFinish runs when the 20 seconds are up — stop the game
            // and disable the tap button so the final score stays displayed.
            override fun onFinish() {
                timerText.text = "0"
                gameRunning = false
                tapButton.isEnabled = false
            }
        }.start() // .start() actually begins the countdown
    }

    // Puts everything back to the starting state so the user can play again.
    private fun resetGame() {
        countDownTimer?.cancel()  // stop any countdown that is still running
        tapCount = 0
        gameRunning = false
        timerText.text = "20"
        countText.text = "0"
        tapButton.isEnabled = true
    }

    // onDestroy runs when the app is fully closed (NOT when it just goes to the
    // background). We cancel the timer here so its memory is freed properly.
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
