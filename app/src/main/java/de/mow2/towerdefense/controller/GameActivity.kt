package de.mow2.towerdefense.controller

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import kotlinx.android.synthetic.main.activity_game.*


/**
 * Remove comment before Release!!!
 * This class joins game view and model
 * TODO: Create and include GameView? Could be an instance of GLSurfaceView or similar...
 */
class GameActivity : AppCompatActivity() {
    lateinit var chrono: Chronometer
    lateinit var coinsTxt: TextView
    var buildMenuExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //initializing in-game gui
        chrono = findViewById(R.id.timeView)
        chrono.start()

        coinsTxt = findViewById(R.id.coinsText)
        SoundManager.loadPreferences(this)
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if(!buildMenuExists) {
            defineBuildUpgradeMenu()
        }
    }

    // stops MediaPlayer while not being in activity
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

    /**
     * gets needed screen dimension and location of bottom gui to allow correct placement of the menu
     */
    private fun defineBuildUpgradeMenu() {
        //initializing build and upgrade menu positioning
        val offsetY = gameView.height - gameContainer.height
        GameView.bottomEnd = gameView.bottom.toFloat() - offsetY

        //initialize bitmaps for each tower type
        GameManager.initBuildMenu(resources)
        buildMenuExists = true
    }

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}

