package de.mow2.towerdefense.controller

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.core.view.*
import androidx.preference.PreferenceManager
import de.mow2.towerdefense.MainActivity
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.controller.SoundManager.soundPool
import de.mow2.towerdefense.controller.helper.*
import de.mow2.towerdefense.databinding.ActivityGameBinding
import de.mow2.towerdefense.model.core.BuildUpgradeMenu
import de.mow2.towerdefense.model.core.GameController
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

/**
 * This Activity starts the game
 */
class GameActivity : AppCompatActivity(), GameController {
    override var gameState = GameState(this)
    private val gameManager = GameManager(this)

    //GUI
    private lateinit var binding: ActivityGameBinding
    private lateinit var bottomGuiContainer: ConstraintLayout
    private lateinit var topGuiBg: View
    private lateinit var bottomGuiSpacer: View
    private lateinit var gameLayout: LinearLayout
    private lateinit var gameView: GameView
    private lateinit var waveDisplay: TextView
    private lateinit var waveDisplayText: String
    private lateinit var coinsTxt: TextView
    private lateinit var healthBar: ProgressBar
    private lateinit var healthText: TextView
    private lateinit var waveBar: ProgressBar
    private lateinit var wavePopupText: TextView
    private val menuPopup = PopupFragment()
    private val tutPopup = TutorialFragment()
    private val fm = supportFragmentManager
    private lateinit var prefManager: SharedPreferences

    //build menu
    private lateinit var buildMenuScrollView: HorizontalScrollView
    private lateinit var buildMenuLayout: LinearLayout
    private lateinit var buildButton: ImageButton
    private var buildMenuExists = false

    /**
     * Load all saved user preferences
     */
    private fun loadPrefs() {
        prefManager = PreferenceManager.getDefaultSharedPreferences(this)
        GameManager.tutorialsActive = prefManager.getBoolean("tutorial_pref", true)
        SoundManager.loadPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        Log.i("Binding: ", "$binding.")
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
        // shows tutorial
        if (GameManager.tutorialsActive) {
            displayTutorial(true)
        }
    }

    /**
     * Initialize all game GUI references and contents
     */
    private fun initGUI() {
        //reference game gui containers
        bottomGuiContainer = binding.bottomGuiContainer
        bottomGuiSpacer = binding.bottomGuiSpacer
        topGuiBg = binding.topGuiBg
        topGuiBg.background = BitmapPreloader.topDrawable
        //reference game gui elements
        waveDisplay = binding.waveValue
        coinsTxt = binding.coinsText
        healthBar = binding.healthProgressBar
        healthText = binding.healthText
        waveBar = binding.waveProgressBar
        wavePopupText = binding.waveText
        //reference build menu container
        buildMenuScrollView = binding.buildMenuWrapper
        buildMenuLayout = binding.buildMenuContainer

        // detect which button is currently selected
        buildButton = binding.buildButton

        //create bottom gui contents
        bottomGuiSpacer.background = BitmapPreloader.bottomDrawable
        bottomGuiContainer.children.forEach { view ->
            view.setOnClickListener { button ->
                if (GameManager.selectedTool == button.id) {
                    GameManager.selectedTool = null
                    bottomGuiContainer.children.forEach { it.setBackgroundResource(R.drawable.defaultbtn_states) }
                } else {
                    GameManager.selectedTool = button.id
                    bottomGuiContainer.children.forEach { it.setBackgroundResource(R.drawable.defaultbtn_states) }
                    button.setBackgroundResource(R.drawable.button_border_active)
                }
            }
            if (view == buildButton) {
                view.setOnLongClickListener {
                    toggleBuildMenu()
                    return@setOnLongClickListener true
                }
            }
        }

        //"choose a tower" menu displayed on long click
        val buildMenu = BuildUpgradeMenu(gameManager, this)
        TowerTypes.values().forEachIndexed { i, type ->
            val towerBtn = BuildButton(this, null, R.style.MenuButton_Button, type)
            val towerBtnText = TextView(this)
            towerBtnText.text = "${buildMenu.getTowerCost(type)}"
            towerBtnText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            towerBtnText.setTextColor(Color.YELLOW)
            towerBtn.id = i
            towerBtn.setOnClickListener {
                GameManager.selectedTool = buildButton.id
                GameManager.selectedTower = type
                bottomGuiContainer.children.forEach { it.setBackgroundResource(R.drawable.defaultbtn_states) }
                binding.buildButton.setBackgroundResource(R.drawable.button_border_active)
                toggleBuildMenu()
                when (type) {
                    TowerTypes.SINGLE -> buildButton.setImageResource(R.drawable.tower_single_imagebtn)
                    TowerTypes.SLOW -> buildButton.setImageResource(R.drawable.tower_slow_imagebtn)
                    TowerTypes.AOE -> buildButton.setImageResource(R.drawable.tower_aoe_imagebtn)
                    TowerTypes.MAGIC -> buildButton.setImageResource(R.drawable.tower_magic_imagebtn)
                }
            }
            val buttonContainer = LinearLayout(this)
            buttonContainer.orientation = LinearLayout.VERTICAL
            buttonContainer.addView(towerBtnText)
            buttonContainer.addView(towerBtn)
            buildMenuLayout.addView(buttonContainer)
        }
        GameManager.selectedTool = null //deselect any tool at beginning
    }

    /**
     * Method to write all GUI-related data into their respective layout element
     */
    override fun updateGUI() {
        runOnUiThread {
            coinsTxt.text = GameManager.coinAmnt.toString()
            healthBar.progress = GameManager.livesAmnt
            waveBar.progress = GameManager.killCounter
            val livesText = "${GameManager.livesAmnt} / ${healthBar.max}"
            healthText.text = livesText
            val waveText = "${GameManager.killCounter} / ${waveBar.max}"
            wavePopupText.text = waveText
            waveDisplayText = "${GameManager.gameLevel + 1}"
            waveDisplay.text = waveDisplayText
        }
    }

    override fun updateHealthBarMax(newMax: Int) {
        healthBar.max = newMax
    }

    override fun updateProgressBarMax(newMax: Int) {
        waveBar.max = newMax
        waveBar.progress = 0
    }


    /**
     * opens menu as pop up window if menu button is clicked
     */
    fun popUpMenu(view: View) {
        menuPopup.show(fm, "menuDialog")
    }

    /**
     * show custom toast message in the middle of the screen
     * @param type decides which snackbar should be shown
     */
    override fun showToastMessage(type: String) {
        runOnUiThread {
            val parent = binding.wrapAll
            val toast = CustomToast(this, parent)
            toast.setUpSnackbar(type)
        }
    }

    /**
     * pauses Game and goes back to main menu
     */
    fun leaveGame(view: View) {
        SoundManager.mediaPlayer.release()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
        startActivity(intent)
    }

    /**
     * Triggered if liveAmt = 0, sets game over screen
     */
    override fun onGameOver() {
        runOnUiThread {
            setContentView(R.layout.gameover_view)
            SoundManager.mediaPlayer.release()
            soundPool.play(Sounds.GAMEOVER.id, 1F, 1F, 1, 0, 1F)
            val timeValue = findViewById<TextView>(R.id.timeValue)
            val levelValue = findViewById<TextView>(R.id.levelValue)
            val enemyValue = findViewById<TextView>(R.id.enemyValue)
            timeValue.text = "${waveDisplay.text}"
            val levelText = "${GameManager.gameLevel + 1}"
            levelValue.text = levelText
            enemyValue.text = "${GameManager.enemiesKilled}"
            GameManager.reset()
            gameState.deleteSaveGame()
        }
    }

    override fun onResume() {
        super.onResume()
        // (re-)initialize MediaPlayer with correct settings
        SoundManager.initMediaPlayer(this, R.raw.exploration)
        SoundManager.playSounds()
        SoundManager.loadSounds(this)
        if (!musicSetting) {
            SoundManager.pauseMusic()
        }
        if (!SoundManager.soundSetting) {
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
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    /**
     * hide or show build menu
     */
    private fun toggleBuildMenu() {
        if (!buildMenuExists) {
            buildMenuScrollView.visibility = View.VISIBLE
        } else {
            buildMenuScrollView.visibility = View.GONE
        }
        buildMenuExists = !buildMenuExists
    }

    /**
     * hide or show Tutorial
     * @param active Boolean Value shows if Tutorial should be shown or not
     */
    fun displayTutorial(active: Boolean) {
        if (active) {
            tutPopup.show(fm, "tutorialDialog")
            gameView.toggleGameLoop(false)
        } else {
            prefManager.edit {
                putBoolean("tutorial_pref", false)
            }
            gameView.toggleGameLoop(true)
        }
    }

    /**
     * sets a highlight for the in the tutorial mentioned element
     * @param item the element which should be highlighted
     */
    fun highlight(item: String) {
        val healthBar = binding.healthBarContainer
        val progressBar = binding.progressBarContainer
        val time = binding.leftElementsWrapper
        val coins = binding.rightElementsWrapper
        val menuBtn = binding.menuBtn
        val deleteBtn = binding.deleteButton
        val upgradeBtn = binding.upgradeButton
        val buildBtn = binding.buildButton
        val bottomGui = binding.bottomGuiContainer
        val topGui = binding.topGUI
        val gameContainer = binding.gameContainer
        val tutorial = TutorialHighlighter(
            healthBar,
            progressBar,
            time,
            coins,
            bottomGui,
            topGui,
            gameContainer,
            menuBtn,
            deleteBtn,
            upgradeBtn,
            buildBtn
        )
        tutorial.showElements(item, this)
    }
}
