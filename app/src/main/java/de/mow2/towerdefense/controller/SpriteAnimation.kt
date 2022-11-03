package de.mow2.towerdefense.controller

import android.graphics.Bitmap

//TODO: get movement direction, frame count etc.
/**
 * Takes a Bitmap containing all frames of an animation.
 * Call nextFrame() each time to get the current frame to be drawn onto a canvas.
 * @param bitmap A bitmap containing all frames
 * @param width The desired outcome width
 * @param height The desired outcome height
 * @param frameCount Number of Frames contained in given bitmap
 * @param frameDuration Time duration of one frame in milliseconds
 */
class SpriteAnimation(private val bitmap: Bitmap, val width: Int, private val height: Int, private val frameCount: Int = 7, private val frameDuration: Int = 30) {
    private lateinit var animation: Array<Bitmap>
    private var animationMap = HashMap<Int, Array<Bitmap>>() //holds all different animations for this type
    private var startFrameTime = System.currentTimeMillis()

    var frameCounter = 0

    init {
        cutSpriteSheet()
    }

    /**
     * Returns the next frame of the animation, based on elapsed time since the last frame was called
     */
    fun nextFrame(orientation: Int): Bitmap {
        animation = animationMap[orientation]!!
        //TODO: different orientations (e.g. walking direction of creep)
        if(System.currentTimeMillis() - startFrameTime >= frameDuration) {
            update()
            startFrameTime = System.currentTimeMillis()
        }
        return animation[frameCounter]
    }

    /**
     * Cuts the given Bitmap into single frames, based on parameter frameCount
     */
    private fun cutSpriteSheet() {
        val rows = 3 //TODO: make dynamic
        val cutW = bitmap.width / frameCount
        val cutH = bitmap.height / rows
        for(i in 0 until rows) {
            var addAnimation = arrayOf<Bitmap>()
            for(j in 0 until frameCount) {
                val cutImg = Bitmap.createBitmap(bitmap, cutW * j, cutH * i, cutW, cutH)
                val scaled = Bitmap.createScaledBitmap(cutImg, width, height, true)
                addAnimation = addAnimation.plus(scaled)
            }
            animationMap[i] = addAnimation
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