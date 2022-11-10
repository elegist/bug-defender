package de.mow2.towerdefense

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import de.mow2.towerdefense.controller.*
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.controller.SoundManager.soundPool
import de.mow2.towerdefense.controller.SoundManager.soundSetting
import de.mow2.towerdefense.databinding.ActivityMainBinding
import de.mow2.towerdefense.model.core.GameManager

/**
 * This class is the main entry point
 */
class MainActivity : AppCompatActivity() {
    private val TAG: String = javaClass.name
    private val gameState = GameState()
    private val fm = supportFragmentManager
    private var dialogPopup = PopupFragment()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameState.testGameState(this)
    }

    override fun onResume(){
        super.onResume()
        // decides if resume game button should be shown
        /*if(spielstand vorhanden){
            binding.resumeGameBtn.visibility = View.GONE
        }*/
        // loads music and sounds and sets them based on saved preferences
        SoundManager.loadPreferences(this)
        SoundManager.initMediaPlayer(this, R.raw.sound1)
        SoundManager.playSounds()
        SoundManager.loadSounds(this)
        if(!soundSetting){
            soundPool.release()
        }
        if(!musicSetting) {
            SoundManager.pauseMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        // releases MediaPlayer to make changing music possible
        SoundManager.mediaPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        // releases MediaPlayer and SoundPool onDestroy
        soundPool.release()
        SoundManager.mediaPlayer.release()
    }

    /**
     * Starts GameActivity called by a Button
     */
    fun startGame(view: View) {
        GameManager.reset()
        startActivity(Intent(this, GameActivity::class.java))
    }

    /**
     * Loads Dialog Fragment popUpWindow on button Click based on button id
     */
    fun popUpButton(view: View) {
        when (view.id) {
            R.id.info_button -> {
                dialogPopup.show(fm, "infoDialog")
            }
            R.id.about_button -> {
                dialogPopup.show(fm, "aboutDialog")
            }
            R.id.preference_button -> {
                dialogPopup.show(fm, "settingsDialog")
            }
        }
    }

    /**
     * resumes game with on click
     */
    fun resumeGame(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
    }
}