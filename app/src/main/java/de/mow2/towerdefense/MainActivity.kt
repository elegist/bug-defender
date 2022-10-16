package de.mow2.towerdefense

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import de.mow2.towerdefense.controller.PopupFragment
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.controller.SoundManager.soundPool

/**
 * Remove comment before Release!!!
 * This class is the main entry point
 */

class MainActivity : AppCompatActivity() {
    private val TAG: String = javaClass.name
    private val fm = supportFragmentManager
    var dialogPopup = PopupFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SoundManager.playSounds()
    }

    fun startGame(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
    }

    fun popUpButton(view: View) {
        // how to play sound with onClick
        soundPool.load(this, R.raw.hit_04, 1)
        when (view.id) {
            R.id.info_button -> {
                dialogPopup.show(fm, "infoDialog")
                soundPool.play(1, 1F, 1F, 1, 0, 1F)
            }
            R.id.about_button -> {
                dialogPopup.show(fm, "aboutDialog")
            }
            R.id.preference_button -> {
                dialogPopup.show(fm, "settingsDialog")
            }
        }
    }

    // background music in main activity
    // initialize MediaPlayer
    override fun onResume(){
        super.onResume()
        SoundManager.loadPreferences(this)
        SoundManager.initMediaPlayer(this, R.raw.sound1)
        if(!musicSetting) {
            SoundManager.pauseMusic()
        }
    }

    // 4. stops MediaPlayer while not being in activity
    override fun onPause() {
        super.onPause()
        SoundManager.mediaPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        SoundManager.mediaPlayer.release()
    }
}