package de.mow2.towerdefense

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import de.mow2.towerdefense.controller.GameActivity
//import de.mow2.towerdefense.databinding.ActivityMainBinding
import android.media.MediaPlayer
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import android.widget.*

/**
 * Remove comment before Release!!!
 * This class is the main entry point
 * TODO: Add Preferences and Nav
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playBackgroundMusic()

        setContentView(R.layout.activity_main)

        // tryout PopUp Window Menu
        val menuLayout = findViewById<LinearLayout>(R.id.menu_layout)
        val infoButton = findViewById<Button>(R.id.info_button)
        infoButton.setOnClickListener {
            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val infoView = inflater.inflate(R.layout.popup_view, null)
            val popupWindow = PopupWindow(infoView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            popupWindow.elevation = 10.0F
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            val tv = infoView.findViewById<TextView>(R.id.textView)
            val buttonPopup = infoView.findViewById<Button>(R.id.buttonPopup)

            buttonPopup.setOnClickListener{
                popupWindow.dismiss()
            }

            TransitionManager.beginDelayedTransition(menuLayout)
            popupWindow.showAtLocation(
                menuLayout, // Location to display popup window
                Gravity.CENTER, // Layout position to display popup
                0, // X offset
                0 // Y offset
            )
        }
    }

    fun startGame(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
    }

    // background music in main activity
    var backgroundMusic: MediaPlayer? = null

    // 1. starts background music with app start
    fun playBackgroundMusic() {
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(this, R.raw.sound1)
            backgroundMusic!!.isLooping = true
            backgroundMusic!!.start()
        } else backgroundMusic!!.start()
    }

    // 2. pause || resumes playback
    fun pauseResumeMusic(view: View) {
        var pauseResumeButton = findViewById<ImageButton>(R.id.pause_resume_Button)
        if (backgroundMusic?.isPlaying == true) {
            backgroundMusic?.pause()
            pauseResumeButton.setImageResource(androidx.appcompat.R.drawable.abc_ic_voice_search_api_material)
        } else {
            backgroundMusic?.start()
            pauseResumeButton.setImageResource(androidx.appcompat.R.drawable.abc_ic_go_search_api_material)
        }
    }

    // 3. restart playback when reentering main activity
    override fun onRestart() {
        super.onRestart()
        playBackgroundMusic()
    }

    // 4. destroys MediaPlayer instance when the app is closed
    override fun onStop() {
        super.onStop()
        if (backgroundMusic != null) {
            backgroundMusic!!.release()
            backgroundMusic = null
        }
    }

}