package de.mow2.towerdefense

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.databinding.ActivityMainBinding
import android.media.MediaPlayer
import android.widget.ImageButton

/**
 * Remove comment before Release!!!
 * This class is the main entry point
 * TODO: Add Preferences and Nav
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playBackgroundMusic()

        setContentView(R.layout.activity_main)
    }

    fun startGame(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
    }

    // tryout PopUp Window for Navigation




    // background music in main activity
    var backgroundMusic: MediaPlayer? = null

    // 1. starts background music with app start
    fun playBackgroundMusic() {
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(this, R.raw.sound1)
            backgroundMusic!!.isLooping = true
            backgroundMusic!!.start()
        } else backgroundMusic!!.start()
    }

    // 2. pause || resumes playback
    fun pauseResumeMusic(view: View) {
        var pauseResumeButton = findViewById<ImageButton>(R.id.pause_resume_Button)
        if (backgroundMusic?.isPlaying == true) {
            backgroundMusic?.pause()
            pauseResumeButton.setImageResource(androidx.appcompat.R.drawable.abc_ic_voice_search_api_material)
        } else {
            backgroundMusic?.start()
            pauseResumeButton.setImageResource(androidx.appcompat.R.drawable.abc_ic_go_search_api_material)
        }
    }

    // 3. restart playback when reentering main activity
    override fun onRestart() {
        super.onRestart()
        playBackgroundMusic()
    }

    // 4. destroys MediaPlayer instance when the app is closed
    override fun onStop() {
        super.onStop()
        if (backgroundMusic != null) {
            backgroundMusic!!.release()
            backgroundMusic = null
        }
    }

}