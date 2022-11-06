package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.controller.GameManager
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.pathfinding.Astar
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * An instance of this class represents one specific enemy.
 * @param type One value of CreepTypes (e.g. leafbug, firebug...)
 * @param squareField The squareField on which this creep will spawn
 */
class Creep(type: CreepTypes, spawnPoint: Astar.Node = Astar.Node(Random.nextInt(0 until GameManager.squaresX), 0)
): GameObject() {
    // set width and height of the bitmap
    var w: Int = GameManager.playGround.squareSize
    var h: Int = w

    //walking direction
    var orientation: Int = 0 //TODO: Change value based on walking direction! (0 = down, 1 = up, 2 = left/right) maybe develop a better solution??
    //path finding
    private val alg = Astar()
    private var targetIndex: Int = 0
    private var targetNode = Astar.Node(spawnPoint.x, GameManager.squaresY-1)
    private var path = alg.findPath(spawnPoint, targetNode, GameManager.squaresX, GameManager.squaresY)
    private var sortedPath = path?.reversed()
    private var currentPath = sortedPath
    private var target = currentPath!!.first()

    init{
        coordX = GameManager.playGround.squareArray[spawnPoint.x][spawnPoint.y].coordX
        coordY = GameManager.playGround.squareArray[spawnPoint.x][spawnPoint.y].coordY
    }

    //init coordinates of first target
    private var targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
    private var targetY = GameManager.playGround.squareArray[target.x][target.y].coordY

    var healthPoints = 2
    var baseDamage = 1

    /**
     * calc pixels per update and init speed
     */
    private var speedPixelsPerSecond: Float = (GameView.gameWidth + GameView.gameHeight)*0.03f
        set(value){
            field = (GameView.gameWidth + GameView.gameHeight)*value
        }
    //init speed
    private val speed = speedPixelsPerSecond / GameLoop.targetUPS

    override fun update(){
        /**
         * math for the movement calculation:
         * https://www.codeproject.com/articles/990452/interception-of-two-moving-objects-in-d-space
         */
        //vector between enemy and target
        val distanceToTargetX: Float = targetX - positionX()
        val distanceToTargetY: Float = targetY - positionY()
        //update direction variable for animation purposes
        orientation = if(distanceToTargetX < -5) {
            3 //left
        } else if(distanceToTargetX > 5) {
            2 //right
        } else if(distanceToTargetY < 0) {
            1 //up
        } else {
            0 //down (default)
        }
        //absolute distance
        val distanceToTargetAbs: Float = findDistance(this.positionX(), this.positionY(), targetX, targetY)
        if(distanceToTargetAbs.toInt() <= GameManager.playGround.squareSize*0.10) findNextTarget()
        //direction
        val directionX: Float = distanceToTargetX/distanceToTargetAbs
        val directionY: Float = distanceToTargetY/distanceToTargetAbs
        //check if target has been reached
        if(distanceToTargetAbs > 0){
            velocityX = directionX*speed
            velocityY = directionY*speed
        }else{
            velocityX = 0f
            velocityY = 0f
        }
        //update coordinates
        coordX += velocityX
        coordY += velocityY

        //TODO: distance check might break on different screen sizes
        //check if creep distance is close enough to the target so it can create a new path
        if(distanceToTargetAbs.toInt() <= GameManager.playGround.squareSize*0.10){
            targetNode = Astar.Node(target.x, GameManager.squaresY-1)
            path = alg.findPath(target, targetNode, GameManager.squaresX, GameManager.squaresY)
            sortedPath = path?.reversed()
            currentPath = sortedPath
            findNextTarget()
        }
    }

    /**
     * Finds the device coordinates of the next node inside current path array of this creep and
     * updates targetX and targetY
     */
    private fun findNextTarget() {
        target = currentPath!![targetIndex]
        if (target != currentPath!!.last()) {
            targetY = GameManager.playGround.squareArray[target.x][target.y].coordY
            targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
            targetIndex++
        } else {
            targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
            targetY = GameManager.playGround.squareArray[target.x][target.y].coordY
        }
    }

    fun takeDamage(damageAmount: Int){
        healthPoints -= damageAmount
    }

    companion object{
        //set spawn rate
        var spawnsPerMinute: Float = 30f
        private var spawnsPerSecond: Float = spawnsPerMinute / 60
        //link with target updates per second to convert to updates per spawn
        private val updateCycle: Float = GameLoop.targetUPS / spawnsPerSecond
        private var waitUpdates: Float = 0f

        fun canSpawn() :Boolean{
            return if(waitUpdates <= 0f) {
                waitUpdates += updateCycle
                true
            }else{
                waitUpdates--
                false
            }
        }
    }
}