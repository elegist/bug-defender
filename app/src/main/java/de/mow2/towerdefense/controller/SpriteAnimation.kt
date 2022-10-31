package de.mow2.towerdefense.controller

import android.graphics.Bitmap

//TODO: get movement direction, frame count etc.
class SpriteAnimation(private val bitmap: Bitmap, val width: Int, val height: Int) {
    private var animation = arrayOf<Bitmap>()

    private val frameCount = 7
    var frameCounter = 0

    init {
        cutSpriteSheet()
    }

    fun nextFrame(): Bitmap {
        update()
        return animation[frameCounter]
    }

    private fun cutSpriteSheet() {
        var cutW = bitmap.width / frameCount
        var cutH = bitmap.height
        var i = 0
        while (i < frameCount) {
            val cutImg = Bitmap.createBitmap(bitmap, cutW * i, 0, cutW, cutH)
            val scaled = Bitmap.createScaledBitmap(cutImg, width, height, true)
            animation = animation.plus(scaled)
            i++
        }
    }

    private fun update() {
        if(frameCounter < animation.size - 1) {
            frameCounter++
        } else {
            frameCounter = 0
        }
    }
}