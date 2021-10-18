package com.example.pacman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log

class PlayActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var drawingView: GameView? = null
    private var globals: Globals? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawingView = GameView(this)
        setContentView(drawingView)
        instance = this
        globals = Globals.instance
        sharedPreferences = this.getSharedPreferences("info", MODE_PRIVATE)
        val temp = this.sharedPreferences.getInt("high_score", 0)
        globals!!.highScore = temp
    }
    fun gameOver()
    {
        val changePage = Intent(this, GameOverActivity::class.java)
        // Error: "Please specify constructor invocation;
        // classifier 'Page2' does not have a companion object"

        startActivity(changePage)
        finish()
    }
    override fun onPause() {
        Log.i("info", "onPause")
        super.onPause()
        drawingView!!.pause()
        val editor = sharedPreferences!!.edit()
        editor.putInt("high_score", globals!!.highScore)
        editor.apply()
        MainActivity.player.pause()
    }

    override fun onResume() {
        Log.i("info", "onResume")
        super.onResume()
        drawingView!!.resume()
        MainActivity.player.start()
    }

    companion object {
        var instance: PlayActivity? = null
    }
}