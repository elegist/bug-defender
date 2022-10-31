package de.mow2.towerdefense.controller


import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import de.mow2.towerdefense.R

class SpriteSheet(var resources: Resources, var image: Bitmap) {
    var bitmapOptions = BitmapFactory.Options()
    init {
        bitmapOptions.inScaled = false
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
        image = BitmapFactory.decodeResource(resources, R.drawable.leafbug, bitmapOptions)
    }

/*    fun cutSprite(): Sprite{
        return Sprite(this, Rect(0,0, 64, 64))
    }*/
}