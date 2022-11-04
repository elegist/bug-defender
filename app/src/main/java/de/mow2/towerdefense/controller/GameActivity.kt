package de.mow2.towerdefense.controller

import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes.Margins
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Chronometer
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar.LayoutParams
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import com.shashank.sony.fancytoastlib.FancyToast
import de.mow2.towerdefense.MainActivity
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.model.core.BuildUpgradeMenu
import de.mow2.towerdefense.model.core.GUICallBack
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes
import kotlinx.android.synthetic.main.activity_game.*

/**
 * This Activity starts the game
 */
class GameActivity : AppCompatActivity(), GUICallBack {
    //game content and gui
    private val levelGenerator: LevelGenerator by viewModels()
    private lateinit var gameLayout: LinearLayout
    private lateinit var gameView: GameView
    private lateinit var chrono: Chronometer
    private lateinit var coinsTxt: TextView
    private var menuPopup = PopupFragment()
    private val fm = supportFragmentManager
    //buildmenu
    private lateinit var buildMenuScrollView: HorizontalScrollView
    private lateinit var buildMenuLayout: LinearLayout
    private val buildMenu = BuildUpgradeMenu()
    var buildMenuExists = false
    //observers
    private lateinit var coinObserver: Observer<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        //create view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        GameManager.resources = resources //GameManager needs to know resources for drawing
        GameManager.initImages()
        loadPrefs()

        //create new game view
        gameLayout = gameViewContainer
        gameView = GameView(this, this)
        gameLayout.addView(gameView)

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

    /**
     * Button-triggered reset (return to main menu)
     */
    fun leaveGame(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        GameManager.resetManager()
    }

    /**
     * Load all saved user preferences
     */
    private fun loadPrefs() {
        //load preferences
        SoundManager.loadPreferences(this)
    }

    fun popUpMenu(view: View) {
        menuPopup.show(fm, "menuDialog")
    }

    /**
     * Initialize all game GUI references (findViewById)
     */
    private fun initGUI() {
        //reference game gui
        chrono = timeView
        coinsTxt = coinsText
        //reference build menu container
        buildMenuScrollView = buildMenuWrapper
        buildMenuLayout = buildMenuContainer
    }

    /**
     * Define value observers for coins, lives etc.
     */
    private fun defineObservers() {
        coinObserver = Observer<Int> { newCoinVal ->
            coinsTxt.text = newCoinVal.toString()
        }
    }

    override fun onResume(){
        super.onResume()
        // (re-)initialize MediaPlayer with correct settings
        SoundManager.loadPreferences(this)
        SoundManager.initMediaPlayer(this, R.raw.song3)
        if(!musicSetting) {
            SoundManager.pauseMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        // stops MediaPlayer while not being in activity
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

    /**
     * BuildMenu buttons trigger this function to build a tower
     * @param type type of the tower
     * @param level the towers level (base = 0, upgraded = 1-2)
     */
    override fun buildTower(type: TowerTypes, level: Int) {
        val cost = buildMenu.getTowerCost(type, level)
        if(levelGenerator.decreaseCoins(cost)) {
            //build tower
            if(level != 0) {
                buildMenu.upgradeTower(selectedField)
            } else {
                buildMenu.buildTower(selectedField, type)
            }
        } else {
            //not enough money! message player
            FancyToast.makeText(this, "not enough money", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false ).show()
        }
    }

    private lateinit var selectedField: SquareField
    /**
     * Generates a individual build tower menu depending on the touched SquareField
     * If a tower has already been built, it displays a delete tower / upgrade menu
     * @param squareField chosen field on the playground (by touch event)
     */
    override fun toggleBuildMenu(squareField: SquareField) {
        buildMenuLayout.removeAllViews()
        selectedField = squareField
        //create build menu or make it disappear
        if(!buildMenuExists) {
            //if field already contains tower, display delete tower button
            val level = if(selectedField.hasTower != null) {
                val tower = selectedField.hasTower!! //reference tower
                //add delete tower button
                val deleteBtn = ImageButton(this, null, R.style.MenuButton_Button)
                deleteBtn.setImageResource(R.drawable.tower_destroy)
                deleteBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.green_overlay
                    )
                )
                deleteBtn.setPadding(0,0, 0, 30)
                buildMenuLayout.addView(deleteBtn)
                deleteBtn.setOnClickListener {
                    buildMenu.destroyTower(tower)
                    levelGenerator.increaseCoins(buildMenu.getTowerCost(tower.type, tower.level) / 2) //get half of the tower value back
                    toggleBuildMenu(selectedField)
                }
                tower.level + 1
            } else {
                0
            }
            TowerTypes.values().forEachIndexed { i, type ->
                val towerBtn = BuildButton(this, null, R.style.MenuButton_Button, type, level)
                towerBtn.id = i
                towerBtn.setOnClickListener {
                    buildTower(type, level)
                    toggleBuildMenu(selectedField)
                }
                buildMenuLayout.addView(towerBtn)
            }

            buildMenuScrollView.visibility = View.VISIBLE
        } else {
            buildMenuScrollView.visibility = View.GONE
        }
        //swap boolean flag
        buildMenuExists = !buildMenuExists
    }
}

