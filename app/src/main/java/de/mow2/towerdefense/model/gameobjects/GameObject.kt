package de.mow2.towerdefense.model.gameobjects



import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.GameLoop
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * GameObject is the foundation of any moving or static actor in the game
 */
abstract class GameObject() {
    //device coordinates for drawing and moving
    // TODO(): replace with vector2D utils
    protected var coordX: Float = 0f
    protected var coordY: Float = 0f
    /**
     * Pixels per update for movement.
     * Will be multiplied with direction to get a velocity.
     * @see moveTo(target: GameObject)
     */
    var speed: Float = 0f
        set(value){
            val rawPixels = (GameView.gameWidth + GameView.gameHeight)*value
            field = rawPixels / GameLoop.targetUPS
        }
    private var velocityX: Float = 0f
    private var velocityY: Float = 0f

    //variables for movement calculation
    //vector to a target
    protected var distanceToTargetX = 0f
    protected var distanceToTargetY = 0f
    //absolute distance to a target
    protected var distanceToTargetAbs: Float = 0f
    //vector direction
    private var directionX: Float = distanceToTargetX/distanceToTargetAbs
    private var directionY: Float = distanceToTargetY/distanceToTargetAbs

    //set spawn rate
    var actionsPerMinute: Float = 0f
    var spawnsPerSecond: Float = actionsPerMinute / 60
    //link with target updates per second to convert to updates per spawn
    private val updateCycle: Float = GameLoop.targetUPS / spawnsPerSecond
    var waitUpdates: Float = 0f

    open fun update() {}

    /**
     * Moves a GameObject to another. Has to be called from a subclass of GameObject.
     * Subclasses have to init movement speed.
     * Default movement speed is 0.
     * @see speedPixelsPerSecond
     * @see speed
     */
    fun moveTo(targetX: Float, targetY: Float){
        //vector to the target
        distanceToTargetX = targetX - coordX
        distanceToTargetY = targetY - coordY
        //absolute distance
        distanceToTargetAbs = findDistance(coordX, coordY, targetX, targetY)
        //direction
        directionX= distanceToTargetX/distanceToTargetAbs
        directionY= distanceToTargetY/distanceToTargetAbs
        //check if target has been reached
        if(distanceToTargetAbs > 0f){
            velocityX = directionX*speed
            velocityY = directionY*speed
        }else{
            velocityX = 0f
            velocityY = 0f
        }
        //update coordinates
        coordX += velocityX
        coordY += velocityY
    }
    /**
     * @return current x-coordinates of a GameObject
     */
    fun positionX(): Float{ return coordX }
    /**
     * current Y-coordinates of a GameObject
     * @return
     */
    fun positionY(): Float{ return coordY }

    /**
     * gets the absolute distance between two objects
     * @param obj1 GameObject that will provide x- and y-coordinates as a starting point
     * @param obj2 GameObject that will provide x- and y-coordinates as a destination
     * @return Float
     */
    fun findDistance (obj1: GameObject, obj2: GameObject): Float {
        return sqrt(
            (obj2.coordX - obj1.coordX).pow(2) + (obj2.coordY - obj1.coordY).pow(2)
        )
    }

    /**
     * gets the absolute distance between two points
     * @param fromX x-coordinate of the starting point
     * @param fromY y-coordinate of the starting point
     * @param toX x-coordinate of the destination
     * @param toY y-coordinate of the destination
     * @return Float
     */
    fun findDistance (fromX: Float, fromY: Float,  toX: Float, toY: Float): Float {
        return sqrt(
            (toX - fromX).pow(2) + (toY - fromY).pow(2)
        )
    }

    /**
     * Cycles between true and false.
     * Enables GameObjects to have a set amount of actions per minute.
     * @see spawnsPerMinute
     * @return true || false
     */
    fun cooldown() :Boolean{
        return if(waitUpdates <= 0f) {
            waitUpdates += updateCycle
            true
        }else{
            waitUpdates--
            false
        }
    }
}
