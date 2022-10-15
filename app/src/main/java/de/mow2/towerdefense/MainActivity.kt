package de.mow2.towerdefense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.mow2.towerdefense.controller.PopupFragment
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.controller.SoundManager

/**
 * Remove comment before Release!!!
 * This class is the main entry point
 */

class MainActivity : AppCompatActivity() {
    private val TAG: String = javaClass.name
    private val fm = supportFragmentManager
    var dialogPopup = PopupFragment()
    //var soundPool: SoundPool? = null
    //val soundId = 1*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadPreferences()
        /* // tryout sound pool
         soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
         soundPool!!.load(baseContext, R.raw.hit_04, 1)*/
    }

  /*  // tryout sound pool
    fun playSound (view: View){
        soundPool?.play(soundId, 1F, 1F, 0,0, 1F)
    }*/

    fun startGame(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
    }

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

    // background music in main activity
    // initialize MediaPlayer
    override fun onResume(){
        super.onResume()
        SoundManager.initMediaPlayer(this, R.raw.sound1)
    }

    // 4. stops MediaPlayer while not being in activity
    override fun onPause() {
        super.onPause()
        SoundManager.mediaPlayer.release()
    }

    private fun loadPreferences() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val musicSetting = preferences.getBoolean("music_pref", true)
        Log.i(TAG, musicSetting.toString())
    }
}