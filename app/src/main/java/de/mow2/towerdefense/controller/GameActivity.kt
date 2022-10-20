package de.mow2.towerdefense.controller

import android.content.Context
import android.os.Bundle
import android.widget.Chronometer
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    lateinit var chrono: Chronometer
    lateinit var coinsTxt: TextView
    lateinit var scrollView: ScrollView
    var buildMenuExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //initialize in-game gui
        //time measurement
        chrono = timeView
        chrono.start()
        //coins display
        coinsTxt = coinsText
        coinsTxt.text = GameManager.coins.toString()

        SoundManager.loadPreferences(this)

        //get vertical scroll offset for build menu
        scrollView = gameContainer
        gameContainer.viewTreeObserver.addOnScrollChangedListener {
            scrollOffset = scrollView.scrollY
        }
        //immersive mode
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        //define display of build menu, if it hasn't already been done
        if(!buildMenuExists) {
            defineBuildUpgradeMenu()
        }
    }

    override fun onResume(){
        super.onResume()
        // re-initialize MediaPlayer with correct settings
        SoundManager.loadPreferences(this)
        SoundManager.initMediaPlayer(this, R.raw.song3)
        if(!musicSetting) {
            SoundManager.pauseMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        // 4. stops MediaPlayer while not being in activity
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

    /**
     * immersive mode (hide system bars)
     */
    private fun hideSystemBars() {
        // Hide both the status bar and the navigation bar
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    companion object {
        //scroll offset for build menu
        var scrollOffset = 0
    }
}

