package de.mow2.towerdefense.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import de.mow2.towerdefense.R
import java.nio.Buffer

class SpriteSheet(context: Context,  var image: Bitmap) {
    var bitmapOptions = BitmapFactory.Options()
    init {
        bitmapOptions.inScaled = false
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
        image = BitmapFactory.decodeResource(context.resources, R.drawable.leafbug, bitmapOptions)
    }

    fun getSprite(): Sprite{
        return Sprite(this, Rect(0,0, 64, 64))
    }
}