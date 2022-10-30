package de.mow2.towerdefense

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import de.mow2.towerdefense.controller.PopupFragment
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.controller.SoundManager.soundPool
import de.mow2.towerdefense.controller.SoundManager.soundSetting
import de.mow2.towerdefense.controller.Sounds

/**
 * This class is the main entry point
 */
class MainActivity : AppCompatActivity() {
    private val TAG: String = javaClass.name
    private val fm = supportFragmentManager
    private var dialogPopup = PopupFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume(){
        super.onResume()
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
        startActivity(Intent(this, GameActivity::class.java))
    }

    /**
     * Loads Dialog Fragment popUpWindow on button Click based on button id
     */
    fun popUpButton(view: View) {
        when (view.id) {
            R.id.info_button -> {
                dialogPopup.show(fm, "infoDialog")
                soundPool.play(Sounds.HITSOUND.id, 1F, 1F, 1, 0, 1F)
            }
            R.id.about_button -> {
                dialogPopup.show(fm, "aboutDialog")
            }
            R.id.preference_button -> {
                dialogPopup.show(fm, "settingsDialog")
            }
        }
    }
}