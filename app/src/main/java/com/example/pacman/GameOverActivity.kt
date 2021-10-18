package com.example.pacman

import com.example.pacman.Globals.Companion.instance
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pacman.R
import android.widget.TextView
import com.example.pacman.Globals
import android.content.Intent
import android.view.View
import com.example.pacman.PlayActivity
import com.example.pacman.MainActivity

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        val status = findViewById<TextView>(R.id.status)
        val highScore = findViewById<TextView>(R.id.highScore)
        val score = findViewById<TextView>(R.id.score)
        if (instance!!.score==1680)
        {
            status.text="You win!"
        }else{
            status.text="Game Over!"
        }
        highScore.text = "High Score: " + instance!!.highScore
        score.text = "Your Score: " + instance!!.score
    }

    fun restart(view: View?) {
        startActivity(Intent(this, PlayActivity::class.java))
        finish()
    }

    fun mainMenu(view: View?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}