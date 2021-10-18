package com.example.pacman

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.pacman.HelpActivity
import com.example.pacman.PlayActivity
import android.os.Bundle
import com.example.pacman.R
import com.example.pacman.MainActivity
import android.media.MediaPlayer
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    // Method to start activity for Help button
    fun showHelpScreen(view: View?) {
        val helpIntent = Intent(this, HelpActivity::class.java)
        startActivity(helpIntent)
    }

    // Method to start activity for Play button
    fun showPlayScreen(view: View?) {
        val playIntent = Intent(this, PlayActivity::class.java)
        startActivity(playIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        player = MediaPlayer.create(this, R.raw.pacman_song)
        with(player) {
            this?.setVolume(100f, 100f)
            this?.setLooping(true)
            this?.start()
        }
    }

    public override fun onPause() {
        super.onPause()
        player!!.pause()
    }

    public override fun onResume() {
        Log.i("info", "MainActivity onResume")
        super.onResume()
        player!!.start()
    }

    companion object {
        lateinit var player: MediaPlayer

        //    @Override
        //    public boolean onCreateOptionsMenu(Menu menu) {
        //        // Inflate the menu; this adds items to the action bar if it is present.
        //        getMenuInflater().inflate(R.menu.menu_main, menu);
        //        return true;
        //    }
        //    @Override
        //    public boolean onOptionsItemSelected(MenuItem item) {
        //        // Handle action bar item clicks here. The action bar will
        //        // automatically handle clicks on the Home/Up button, so long
        //        // as you specify a parent activity in AndroidManifest.xml.
        //        int id = item.getItemId();
        //
        //        //noinspection SimplifiableIfStatement
        //        if (id == R.id.action_settings) {
        //            return true;
        //        }
        //
        //        return super.onOptionsItemSelected(item);
        //    }
    }

    fun resumeGame(view: android.view.View) {}
}