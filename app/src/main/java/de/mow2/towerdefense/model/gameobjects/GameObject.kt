package de.mow2.towerdefense.model.gameobjects

import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.helper.Vector2D
import kotlin.math.abs

/**
 * GameObject is the foundation of any moving or static actor in the game
 */
abstract class GameObject() {
    abstract val speed: Float
    //size
    abstract var width: Int
    abstract var height: Int
    //position
    abstract var position: Vector2D
    val positionCenter: Vector2D
        get() {
            return Vector2D(position.x + width / 2, position.y + height / 2)
    }

    //variables for movement calculation
    protected lateinit var distance: Vector2D
    protected var distanceToTargetAbs: Float = 0f
    private lateinit var direction: Vector2D
    private lateinit var velocity: Vector2D

    private var updateCycle: Float = 0f
    //set spawn rate
    var actionsPerMinute: Float = 0f
        set(value){
            field = value
            if(field != 0f){
                val actionsPerSecond: Float = field / 60
                //link with target updates per second to convert to updates per spawn
                updateCycle = GameLoop.targetUPS / actionsPerSecond
            }
        }
    var waitUpdates: Float = 0f

    abstract fun update()

    /**
     * Moves a GameObject to another. Has to be called from a subclass of GameObject.
     * Subclasses have to init movement speed.
     * Default movement speed is 0.
     * @see speedPixelsPerSecond
     * @see speed
     */
    fun moveTo(target: Vector2D){
        //vector to the target
        distance = target - position
        //absolute distance
        distanceToTargetAbs = findDistance(position, target)
        //direction
        direction = distance / distanceToTargetAbs
        //check if target has been reached
        velocity = if(distanceToTargetAbs > 0f){
            direction * speed
        }else{
            Vector2D(0, 0)
        }
        //update coordinates
        position += velocity
    }

    /**
     * gets the absolute distance between two objects
     * @param obj1 GameObject that will provide x- and y-coordinates as a starting point
     * @param obj2 GameObject that will provide x- and y-coordinates as a destination
     * @return Float
     */
    open fun findDistance (obj1: GameObject, obj2: GameObject): Float {
        return abs(obj2.position.x - obj1.position.x) + abs(obj2.position.y - obj1.position.y)
    }
    open fun findDistance (from: Vector2D, to: Vector2D): Float {
        return abs(to.x - from.x) + abs(to.y - from.y)
    }
    /**
     * Cycles between true and false.
     * Enables GameObjects to have a set amount of actions per minute.
     * @see spawnsPerMinute
     * @return true || false
     */
    open fun cooldown() :Boolean{
        return if(waitUpdates <= 0f) {
            waitUpdates += updateCycle
            true
        }else{
            waitUpdates--
            false
        }
    }
}
