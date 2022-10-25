package de.mow2.towerdefense.controller

import android.os.Bundle
import android.view.View
import android.widget.Chronometer
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.model.core.BuildUpgradeMenu
import de.mow2.towerdefense.model.core.GUICallBack
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity(), GUICallBack {
    //game content and gui
    private val levelGenerator: LevelGenerator by viewModels()
    private lateinit var gameLayout: LinearLayout
    private lateinit var gameView: GameView
    private lateinit var chrono: Chronometer
    private lateinit var coinsTxt: TextView
    //buildmenu
    private lateinit var buildMenuScrollView: HorizontalScrollView
    private lateinit var buildMenuLayout: LinearLayout
    private val buildMenu = BuildUpgradeMenu()
    private var buildMenuExists = false
    //observers
    private lateinit var coinObserver: Observer<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        //create view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //loading preferences
        loadPrefs()
        //create new game view
        gameLayout = gameViewContainer
        gameView = GameView(this, this)
        gameLayout.addView(gameView)
        //init display and gui
        initGUI()
        hideSystemBars()
        defineObservers()
        //build level
        //TODO: dynamically decide which level to build
        levelGenerator.initLevel(0)
        //bind observers to views
        levelGenerator.coinAmnt.observe(this, coinObserver)
        //pass levelGenerator to gameView
        gameView.levelGenerator = levelGenerator
        //start level timer
        chrono.start()
    }

    private fun loadPrefs() {
        //load preferences
        SoundManager.loadPreferences(this)
    }

    private fun initGUI() {
        //reference game gui
        chrono = timeView
        coinsTxt = coinsText
        //reference build menu container
        buildMenuScrollView = buildMenuWrapper
        buildMenuLayout = buildMenuContainer
        initializeBuildMenu()
    }

    private fun defineObservers() {
        coinObserver = Observer<Int> { newCoinVal ->
            coinsTxt.text = newCoinVal.toString()
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
     * immersive mode (hide system bars)
     */
    private fun hideSystemBars() {
        // Hide both the status bar and the navigation bar
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun buildTower(type: TowerTypes) {
        val cost = buildMenu.getTowerCost(type)
        if(levelGenerator.decreaseCoins(cost)) {
            //build tower
            buildMenu.buildTower(selectedField, type)
        } else {
            //not enough money! message player
        }
        toggleBuildMenu(selectedField)
    }

    private lateinit var selectedField: SquareField
    override fun toggleBuildMenu(squareField: SquareField) {
        selectedField = squareField
        //toggle visibility based on boolean flag
        if(!buildMenuExists) {
            buildMenuScrollView.visibility = View.VISIBLE
        } else {
            buildMenuScrollView.visibility = View.GONE
        }
        //swap boolean flag
        buildMenuExists = !buildMenuExists
    }

    override fun initializeBuildMenu() {
        TowerTypes.values().forEachIndexed { i, type ->
            var towerBtn = ImageButton(this, null, R.style.ImageButton_Main)
            when(type) {
                TowerTypes.BLOCK -> towerBtn.setImageResource(R.drawable.tower_block)
                TowerTypes.SLOW -> towerBtn.setImageResource(R.drawable.tower_slow)
                TowerTypes.AOE -> towerBtn.setImageResource(R.drawable.tower_aoe)
            }
            towerBtn.id = i
            towerBtn.setOnClickListener {buildTower(type)}
            buildMenuLayout.addView(towerBtn)
        }
    }
}

