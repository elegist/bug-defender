package de.mow2.towerdefense.model.gameobjects



import de.mow2.towerdefense.model.core.SquareField
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * GameObject is the foundation of any moving object in the game
 */
abstract class GameObject(var squareField: SquareField) {
    protected var coordX = squareField.coordX
    protected var coordY = squareField.coordY
    protected var velocityX: Float = 0.0f
    protected var velocityY: Float = 0.0f


    fun positionX(): Float{ return coordX }
    fun positionY(): Float{ return coordY }

    abstract fun update()

    companion object{
        fun findDistance (obj1: GameObject, obj2: GameObject): Float {
            return sqrt(
                (obj2.coordX - obj1.coordX).pow(2) + (obj2.coordY - obj1.coordY).pow(2)
            )
        }
        fun findDistance (fromX: Float, fromY: Float,  toX: Float, toY: Float): Float {
            return sqrt(
                (toX - fromX).pow(2) + (toY - fromY).pow(2)
            )
        }
    }
}