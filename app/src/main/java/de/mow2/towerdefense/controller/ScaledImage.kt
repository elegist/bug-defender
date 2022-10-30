package de.mow2.towerdefense.controller

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.gameobjects.actors.CreepTypes
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

/**
 * Takes a Bitmap and resizes its dimensions
 * !Important: call with at least 1 optional argument!
 * could be expanded to perform various action such as change color, alpha etc.
 * @param resources resources that hold a reference to the drawable
 * @param drawableR the resource identifier of the specific image
 * @param width desired width
 * @param height desired height
 * @param towerType (optional) type of tower (from TowerTypes)
 * @param creepType (optional) type of creep (from CreepTypes)
 */
data class ScaledImage(val resources: Resources, val width: Int, val height: Int, val towerType: TowerTypes? = null, val creepType: CreepTypes? = null) {
    private val options = BitmapFactory.Options()

    init {
        options.inScaled = false //prevent "rescaling"
        options.inPreferredConfig = Bitmap.Config.RGB_565 //low quality bitmaps
    }

    fun getImage(): Bitmap? {
        val bitmap = if(towerType != null) {
            BitmapFactory.decodeResource(resources, getTowerImageResource(), options)
        } else if(creepType != null) {
            BitmapFactory.decodeResource(resources, getCreepImageResource(), options)
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

    private fun getCreepImageResource(): Int {
        return R.drawable.leafbug_down
    }
}

