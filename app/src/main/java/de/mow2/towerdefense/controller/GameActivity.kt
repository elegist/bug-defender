package de.mow2.towerdefense.controller

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


/**
 * Remove comment before Release!!!
 * This class joins game view and model
 * TODO: Create and include GameView? Could be an instance of GLSurfaceView or similar...
 */
class GameActivity : AppCompatActivity() {
    private lateinit var weaponAnimation: AnimationDrawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(de.mow2.towerdefense.R.layout.activity_game)

        val slingshotImage = findViewById<ImageView>(de.mow2.towerdefense.R.id.imageView).apply {
            setBackgroundResource(de.mow2.towerdefense.R.drawable.tower_slingshot_weapon_anim)
            weaponAnimation = background as AnimationDrawable
        }

        slingshotImage.setOnClickListener({weaponAnimation.start()})

    }
}