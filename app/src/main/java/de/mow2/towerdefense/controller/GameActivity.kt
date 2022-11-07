package de.mow2.towerdefense.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Chronometer
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.shashank.sony.fancytoastlib.FancyToast
import de.mow2.towerdefense.MainActivity
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.controller.SoundManager.soundPool
import de.mow2.towerdefense.databinding.ActivityGameBinding
import de.mow2.towerdefense.model.core.BuildUpgradeMenu
import de.mow2.towerdefense.model.core.GUICallBack
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes


/**
 * This Activity starts the game
 */
class GameActivity : AppCompatActivity(), GUICallBack {
    //game content and gui
    private val gameManager = GameManager(this)
    private lateinit var gameLayout: LinearLayout
    private lateinit var gameView: GameView
    private lateinit var chrono: Chronometer
    lateinit var coinsTxt: TextView
    lateinit var healthBar: ProgressBar
    lateinit var waveBar: ProgressBar
    private var menuPopup = PopupFragment()
    private val fm = supportFragmentManager
    //buildmenu
    private lateinit var buildMenuScrollView: HorizontalScrollView
    private lateinit var buildMenuLayout: LinearLayout
    private val buildMenu = BuildUpgradeMenu()
    var buildMenuExists = false
    // View Binding
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        //create new game view
        gameLayout = binding.gameViewContainer
        gameView = GameView(this,this, gameManager)
        gameLayout.addView(gameView)
        setContentView(binding.root)
        //load settings and GUI
        loadPrefs()
        initGUI()
        hideSystemBars()
        //init resources and game manager
        gameManager.resources = resources //GameManager needs to know resources for drawing
        gameManager.initImages()
        gameManager.initLevel(0) //TODO: Load saved game
        //start level timer
        chrono.start()
    }

    fun onGameOver() {
        setContentView(R.layout.gameover_view)
        soundPool.play(Sounds.GAMEOVER.id, 1F, 1F, 1, 0, 1F)
    }

    /**
     * Button-triggered reset (return to main menu)
     */
    fun leaveGame(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
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
        chrono = binding.timeView
        coinsTxt = binding.coinsText
        healthBar = binding.healthProgressBar
        waveBar = binding.waveProgressBar
        //reference build menu container
        buildMenuScrollView = binding.buildMenuWrapper
        buildMenuLayout = binding.buildMenuContainer
    }

    override fun onResume(){
        super.onResume()
        // (re-)initialize MediaPlayer with correct settings
        SoundManager.initMediaPlayer(this, R.raw.song3)
        SoundManager.playSounds()
        SoundManager.loadSounds(this)
        if(!musicSetting) {
            SoundManager.pauseMusic()
        }
        if(!SoundManager.soundSetting){
            soundPool.release()
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
        if(gameManager.decreaseCoins(cost)) {
            //build tower
            if(level != 0) {
                buildMenu.upgradeTower(selectedField)
            } else {
                buildMenu.buildTower(selectedField, type)
                soundPool.play(Sounds.PUNCHSOUND.id, 1F, 1F, 1, 0, 1F)
            }
        } else {
            //not enough money!
            /*val snackbar = Snackbar
                .make(gameView, R.string.moneyWarning, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.dark_brown))
                .show()*/
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
                deleteBtn.setBackgroundColor(getColor(R.color.green_overlay))
                deleteBtn.setPadding(0,0, 0, 30)
                buildMenuLayout.addView(deleteBtn)
                deleteBtn.setOnClickListener {
                    buildMenu.destroyTower(tower)
                    gameManager.increaseCoins(buildMenu.getTowerCost(tower.type, tower.level) / 2) //get half of the tower value back
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

