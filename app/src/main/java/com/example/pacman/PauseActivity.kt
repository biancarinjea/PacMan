package com.example.pacman

import com.example.pacman.PlayActivity.Companion.instance
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pacman.R
import com.example.pacman.MainActivity
import com.example.pacman.PlayActivity
import android.content.Intent
import android.view.View
import com.example.pacman.HelpActivity

class PauseActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.paused_layout)
        MainActivity.player.start()
        instance!!.finish()
    }

    // Method to start activity for Help button
    fun showHelpScreen(view: View?) {
        val helpIntent = Intent(this, HelpActivity::class.java)
        startActivity(helpIntent)
    }

    // Method to start activity for Play button
    fun showPlayScreen(view: View?) {
        val playIntent = Intent(this, PlayActivity::class.java)
        startActivity(playIntent)
        finish()
    }

    // Method to resume the game
    fun resumeGame(view: View?) {
        val resumeIntent = Intent(this, PlayActivity::class.java)
        resumeIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(resumeIntent)
        finish()
    }
}