package de.mow2.towerdefense.controller

import android.os.Bundle
import android.widget.Chronometer
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    private val levelGenerator: LevelGenerator by viewModels()
    lateinit var chrono: Chronometer
    lateinit var coinsTxt: TextView
    lateinit var scrollView: ScrollView
    private var buildMenuExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //create view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //load preferences
        SoundManager.loadPreferences(this)
        //initialize game gui
        chrono = timeView
        coinsTxt = coinsText
        //get vertical scroll offset for build menu
        scrollView = gameContainer
        gameContainer.viewTreeObserver.addOnScrollChangedListener {
            scrollOffset = scrollView.scrollY
        }
        //immersive mode
        hideSystemBars()
        //all built up: finally initialize level
        //define observers for value changes
        val coinObserver = Observer<Int> { newCoinVal ->
            coinsTxt.text = newCoinVal.toString()
        }
        //TODO: dynamically decide which level to build
        levelGenerator.initLevel(0)
        //bind observers to views
        levelGenerator.coinAmnt.observe(this, coinObserver)
        //pass levelGenerator to gameView
        gameView.levelGenerator = levelGenerator
        //start level timer
        chrono.start()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        //define display of build menu, if it hasn't already been done
        if(!buildMenuExists) {
            defineBuildUpgradeMenu()
        }
    }


    // background music in main activity
    // initialize MediaPlayer, load settings
    override fun onResume(){
        super.onResume()
        // re-initialize MediaPlayer with correct settings
        SoundManager.loadPreferences(this)
        SoundManager.initMediaPlayer(this, R.raw.song3)
        if(!musicSetting) {
            SoundManager.pauseMusic()
        }
    }

    // stops MediaPlayer while not being in activity
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
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }

    companion object {
        //scroll offset for build menu
        var scrollOffset = 0
    }
}

