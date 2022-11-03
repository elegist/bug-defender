package de.mow2.towerdefense.controller

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

/**
 * Takes a Bitmap and resizes its dimensions
 * !! Important: call with at least 1 optional argument !!
 * could be expanded to perform various action such as change color, alpha etc.
 * @param resources resources that hold a reference to the drawable
 * @param width desired width
 * @param height desired height
 * @param towerType (optional) type of tower (from TowerTypes)
 * @param bitmapR (optional) needed if theres no tower -> bitmap resource identifier
 */
data class ScaledImage(val resources: Resources, val width: Int, val height: Int, val towerType: TowerTypes? = null, val bitmapR: Int? = null) {
    private val options = BitmapFactory.Options()

    init {
        options.inScaled = false //prevent "rescaling"
        options.inPreferredConfig = Bitmap.Config.RGB_565 //low quality bitmaps
    }

    fun getImage(): Bitmap? {
        val bitmap = if(towerType != null) {
            BitmapFactory.decodeResource(resources, getTowerImageResource(), options)
        } else if(bitmapR != null) {
            BitmapFactory.decodeResource(resources, bitmapR)
        } else {
            return null
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    private fun getTowerImageResource(): Int {
        return when(towerType) {
            TowerTypes.BLOCK -> R.drawable.tower_block
            TowerTypes.SLOW -> R.drawable.tower_slow
            TowerTypes.AOE -> R.drawable.tower_aoe
            null -> R.drawable.tower_destroy
        }
    }
}

