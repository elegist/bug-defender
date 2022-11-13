package de.mow2.towerdefense.controller.helper

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Takes a Bitmap and resizes its dimensions
 * @param resources resources that hold a reference to the drawable
 * @param width desired width
 * @param height desired height
 * @param bitmapR bitmap resource identifier
 */
data class ScaledImage(val resources: Resources, val width: Int, val height: Int, val bitmapR: Int) {
    private val options = BitmapFactory.Options()
    private val bitmap: Bitmap = BitmapFactory.decodeResource(resources, bitmapR)
    val scaledImage: Bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

    init {
        options.inScaled = false //prevent "rescaling"
        options.inPreferredConfig = Bitmap.Config.RGB_565 //low quality bitmaps
    }
}

