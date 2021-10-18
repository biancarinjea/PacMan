package com.example.pacman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pacman.R
import com.example.pacman.MainActivity

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        MainActivity.player.start()
    }

    override fun onPause() {
        super.onPause()
        MainActivity.player.pause()
    }
}