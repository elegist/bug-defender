package de.mow2.towerdefense

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import de.mow2.towerdefense.controller.GameActivity
import android.view.*
import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.DialogFragment
import kotlinx.android.synthetic.main.popup_view.*

/**
 * Remove comment before Release!!!
 * This class is the main entry point
 * TODO: Add Preferences and Nav
 */
//@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    /*var soundPool: SoundPool? = null
    val soundId = 1*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        var dialogPopup = DialogFragment()
        when (view.id) {
            R.id.info_button -> {
                dialogPopup.show(supportFragmentManager, "customDialog", )
            }
            R.id.about_button -> {
                dialogPopup.show(supportFragmentManager, "customDialog")
            }
            R.id.preference_button -> {
                dialogPopup.show(supportFragmentManager, "customDialog")
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

    /*fun loadPreferences() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val token = preferences.getString("music_pref", "")
        if (!token.equals("", ignoreCase = true)) {
            Log.w(TAG, token!!)
        } else {
            Log.w(TAG, "kein token verfügbar")
        }
    }*/
}