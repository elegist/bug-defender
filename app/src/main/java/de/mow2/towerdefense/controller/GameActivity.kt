package de.mow2.towerdefense.controller

import android.os.Bundle
import android.widget.Chronometer
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting


/**
 * Remove comment before Release!!!
 * This class joins game view and model
 * TODO: Create and include GameView? Could be an instance of GLSurfaceView or similar...
 */
class GameActivity : AppCompatActivity() {
    lateinit var chrono: Chronometer
    lateinit var coinsTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //initializing in-game gui
        chrono = findViewById(R.id.timeView)
        chrono.start()

        coinsTxt = findViewById(R.id.coinsText)
        SoundManager.loadPreferences(this)
    }

    // background music in main activity
    // initialize MediaPlayer
    override fun onResume(){
        super.onResume()
        SoundManager.loadPreferences(this)
        SoundManager.initMediaPlayer(this, R.raw.song3)
        if(!musicSetting) {
            SoundManager.pauseMusic()
        }
    }

    // 4. stops MediaPlayer while not being in activity
    override fun onPause() {
        super.onPause()
        SoundManager.mediaPlayer.release()
    }
}

