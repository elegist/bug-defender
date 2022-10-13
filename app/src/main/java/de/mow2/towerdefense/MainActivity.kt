package de.mow2.towerdefense

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import de.mow2.towerdefense.controller.GameActivity
//import de.mow2.towerdefense.databinding.ActivityMainBinding

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.mow2.towerdefense.controller.GameActivity

import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import de.mow2.towerdefense.controller.SoundManager

/**
 * Remove comment before Release!!!
 * This class is the main entry point
 * TODO: Add Preferences and Nav
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startGame(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
    }

    fun popUpButton(view: View) {
        val menuInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = menuInflater.inflate(R.layout.popup_view, null)
        val width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        val height = ConstraintLayout.LayoutParams.WRAP_CONTENT

        val popupWindow = PopupWindow(popupView, width, height, true)
        val buttonPopup = popupView.findViewById<Button>(R.id.buttonPopup)
        val popupText = popupView.findViewById<TextView>(R.id.popup_textView)

        when (view.id) {
            R.id.about_button -> popupText.setText(R.string.about_text)
            R.id.preference_button -> popupText.setText(R.string.preferences_text)
            R.id.info_button -> popupText.setText(R.string.info_text)
        }

        buttonPopup.setOnClickListener{
            popupWindow.dismiss()
        }

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    // 2. pause || resumes playback
    fun pauseResumeMusic(view: View) {
        val pauseResumeButton = R.id.pause_resume_Button
        if (SoundManager.mediaPlayer.isPlaying) {
            SoundManager.mediaPlayer.pause()
        } else {
            SoundManager.mediaPlayer.start()
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
}