package de.mow2.towerdefense.controller.gameobjects


import android.graphics.Bitmap
import android.graphics.Canvas
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * GameObject is the foundation of any moving object in the game
 * TODO: Tower could be an GameObject, too. Would be easier to make creeps attack towers
 */

abstract class GameObject(coordX: Float, coordY: Float) {
    protected var coordX: Float
    protected var coordY: Float
    protected var velocityX: Float = 0.0f
    protected var velocityY: Float = 0.0f
    init{
        this.coordX = coordX
        this.coordY = coordY
    }

    fun getPositionX(): Float{ return coordX }
    fun getPositionY(): Float{ return coordY }

    abstract fun draw(canvas: Canvas, bitmap: Bitmap)
    abstract fun update()

    companion object{
        fun getDistanceBetweenObjects (obj1: GameObject, obj2: GameObject): Float {
            return sqrt(
                (obj2.coordX - obj1.coordX).pow(2) + (obj2.coordY - obj1.coordY).pow(2)
            )
        }
    }
}