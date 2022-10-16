package de.mow2.towerdefense.controller

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import de.mow2.towerdefense.R
import kotlinx.android.synthetic.main.activity_game.*


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
        
        SoundManager.initMediaPlayer(this, R.raw.song3)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        defineBuildUpgradeMenu()
    }

    // stops MediaPlayer while not being in activity
    override fun onPause() {
        super.onPause()
        SoundManager.mediaPlayer.release()
    }

    /**
     * gets needed screen dimension and location of bottom gui to allow correct placement of the menu
     */
    private fun defineBuildUpgradeMenu() {
        //initializing build and upgrade menu positioning
        var menuPosArray = IntArray(2)
        findViewById<LinearLayout>(R.id.bottomGUI).getLocationOnScreen(menuPosArray)
        GameView.bottomEnd = menuPosArray[1].toFloat()
        GameView.bottomGuiHeight = bottomGUI.height.toFloat()

        //initialize bitmaps for each tower type
        GameManager.initBuildMenu(resources)
    }
}

