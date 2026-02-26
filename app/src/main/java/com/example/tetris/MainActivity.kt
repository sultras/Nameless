package com.example.tetris

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tetrisView: TetrisView
    private lateinit var scoreView: TextView
    private lateinit var statusView: TextView

    private val gameLoop = object : Runnable {
        override fun run() {
            tetrisView.tick()
            handler.postDelayed(this, 450L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tetrisView = findViewById(R.id.tetrisView)
        scoreView = findViewById(R.id.scoreText)
        statusView = findViewById(R.id.statusText)

        findViewById<Button>(R.id.leftButton).setOnClickListener { tetrisView.moveLeft() }
        findViewById<Button>(R.id.rightButton).setOnClickListener { tetrisView.moveRight() }
        findViewById<Button>(R.id.rotateButton).setOnClickListener { tetrisView.rotate() }
        findViewById<Button>(R.id.downButton).setOnClickListener { tetrisView.softDrop() }
        findViewById<Button>(R.id.dropButton).setOnClickListener { tetrisView.hardDrop() }
        findViewById<Button>(R.id.restartButton).setOnClickListener {
            tetrisView.reset()
            statusView.text = getString(R.string.ready)
        }

        tetrisView.onStateChanged = { score, gameOver ->
            scoreView.text = getString(R.string.score, score)
            statusView.text = if (gameOver) getString(R.string.game_over) else getString(R.string.playing)
        }
        tetrisView.reset()
    }

    override fun onResume() {
        super.onResume()
        handler.post(gameLoop)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(gameLoop)
    }
}
