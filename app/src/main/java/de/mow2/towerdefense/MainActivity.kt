package de.mow2.towerdefense

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import de.mow2.towerdefense.controller.GameActivity

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
}