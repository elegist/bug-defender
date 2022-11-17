package de.mow2.towerdefense.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.shashank.sony.fancytoastlib.FancyToast
import de.mow2.towerdefense.MainActivity
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.controller.SoundManager.soundPool
import de.mow2.towerdefense.controller.helper.BuildButton
import de.mow2.towerdefense.controller.helper.GameState
import de.mow2.towerdefense.databinding.ActivityGameBinding
import de.mow2.towerdefense.model.core.BuildUpgradeMenu
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes


/**
 * This Activity starts the game
 */
class GameActivity : AppCompatActivity() {
    private val gameState = GameState()

     //game content and gui
    private val gameManager = GameManager(this)
    private lateinit var gameLayout: LinearLayout
    private lateinit var gameView: GameView
    private lateinit var chrono: Chronometer
    lateinit var coinsTxt: TextView
    lateinit var healthBar: ProgressBar
    lateinit var healthText: TextView
    lateinit var waveBar: ProgressBar
    lateinit var waveText: TextView
    private var menuPopup = PopupFragment()
    private val fm = supportFragmentManager

    //buildmenu
    private lateinit var buildMenuScrollView: HorizontalScrollView
    private lateinit var buildMenuLayout: LinearLayout
    private lateinit var buildButton: ImageButton
    private var buildMenuExists = false

    // View Binding
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        //create new game view
        gameLayout = binding.gameViewContainer
        gameView = GameView(this, this, gameManager)
        gameLayout.addView(gameView)
        setContentView(binding.root)
        //load settings and GUI
        loadPrefs()
        initGUI()
        hideSystemBars()
        //init game manager
        gameManager.initLevel(GameManager.gameLevel) //TODO: Load saved game
        //start level timer
        chrono.start()
    }
    /**
     * pauses Game and goes back to main menu
     */
    fun pauseGame(view: View) {
        //TODO: save game state and return to main menu
        gameState.saveGameState(this)
        startActivity(Intent(this, MainActivity::class.java))
    }

    /**
     * Triggered if liveAmt = 0, sets game over screen
     */
    fun onGameOver() {
        setContentView(R.layout.gameover_view)
        SoundManager.mediaPlayer.release()
        soundPool.play(Sounds.GAMEOVER.id, 1F, 1F, 1, 0, 1F)
        GameManager.reset()
    }

    /**
     * Button-triggered reset (return to main menu)
     */
    fun leaveGame(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        GameManager.reset()
    }

    /**
     * Load all saved user preferences
     */
    private fun loadPrefs() {
        //load preferences
        SoundManager.loadPreferences(this)
    }

    /**
     * opens menu as pop up window if menu button is clicked
     */
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
        healthText = binding.healthText
        waveBar = binding.waveProgressBar
        waveText = binding.waveText
        //reference build menu container
        buildMenuScrollView = binding.buildMenuWrapper
        buildMenuLayout = binding.buildMenuContainer

        // detect which button is currently selected
        buildButton = binding.buildButton

        binding.bottomGUI.children.forEach { view ->
            view.setOnClickListener {
                if(GameManager.selectedTool != null){
                    if (GameManager.selectedTool == it.id){
                        GameManager.selectedTool = null
                    } else {
                        GameManager.selectedTool = it.id
                    }
                } else {
                    GameManager.selectedTool = it.id
                }

                //set onclick for the build menu
//                if (it == binding.buildButton) {
//                    toggleBuildMenu(it)
//                }
            }
            if(view == buildButton) {
                view.setOnLongClickListener {
                    toggleBuildMenu(it)
                    return@setOnLongClickListener true
                }
            }
        }

        TowerTypes.values().forEachIndexed { i, type ->
            val towerBtn = BuildButton(this, null, R.style.MenuButton_Button, type)
            towerBtn.id = i
            towerBtn.setOnClickListener {
                GameManager.selectedTool = buildButton.id
                GameManager.selectedTower = type
                toggleBuildMenu(it)
            }
            buildMenuLayout.addView(towerBtn)
        }
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

    private fun toggleBuildMenu(view: View) {
        if (!buildMenuExists){
            buildMenuScrollView.visibility = View.VISIBLE
        } else {
            buildMenuScrollView.visibility = View.GONE
        }
        buildMenuExists = !buildMenuExists
    }
}

