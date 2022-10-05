package de.mow2.towerdefense.controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mow2.towerdefense.R

/**
 * Remove comment before Release!!!
 * This class joins game view and model
 * TODO: Create and include GameView? Could be an instance of GLSurfaceView or similar...
 */
class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
    }
}