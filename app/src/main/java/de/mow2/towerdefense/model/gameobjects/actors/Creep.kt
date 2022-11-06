package de.mow2.towerdefense.model.gameobjects.actors

import android.util.Log
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
//TODO: ist squareField als parameter wirklich sinnvoll? vielleicht eher node verwenden
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
    private var sortedPath= path?.reversed()
    private var currentPath = sortedPath
    private var target = currentPath!!.first()

    //health points
    var healthPoints = if(GameManager.gameLevel != 0) 5 * GameManager.gameLevel else 5

    //basedamage
    var baseDamage = 1

    //coordinates of first target
    private var targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
    private var targetY = GameManager.playGround.squareArray[target.x][target.y].coordY

    init{
        //spawnpoint of the creep
        coordX = GameManager.playGround.squareArray[spawnPoint.x][spawnPoint.y].coordX
        coordY = GameManager.playGround.squareArray[spawnPoint.x][spawnPoint.y].coordY
        //speed of a creep
        speed = 0.03f
    }

    override fun update(){
        /**
         * math for the movement calculation:
         * https://www.codeproject.com/articles/990452/interception-of-two-moving-objects-in-d-space
         */
        cooldown()
        moveTo(targetX, targetY)
        orientation = if(distanceToTargetX < -5) {
            3 //left
        } else if(distanceToTargetX > 5) {
            2 //right
        } else if(distanceToTargetY < 0) {
            1 //up
        } else {
            0 //down (default)
        }

        //TODO: distance check might break on different screen sizes
        //check if creep distance is close enough to the target so it can create a new path
        if(distanceToTargetAbs <= GameManager.playGround.squareSize*0.10){
            targetNode = Astar.Node(target.x, GameManager.squaresY-1)
            path = Astar().findPath(target, targetNode, GameManager.squaresX, GameManager.squaresY)
            sortedPath = path?.reversed()

            if(currentPath != sortedPath){
                currentPath = sortedPath
            }

            findNextTarget()
        }
    }

    /**
     * Finds the device coordinates of the next node inside currentPath List of this creep and
     * updates targetX and targetY.
     * @see targetX
     * @see targetY
     */
    private fun findNextTarget() {
        target = currentPath!![targetIndex]
        if (target != currentPath!!.last()) {
            targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
            targetY = GameManager.playGround.squareArray[target.x][target.y].coordY
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
        //TODO(): Put spawn logic inside GameManager
        /**
         * maximum amount of creeps that are spawned per minute
         */
        var spawnsPerMinute: Float = 30f
        var spawnsPerSecond: Float = spawnsPerMinute / 60
        //link with target updates per second to convert to updates per spawn
        private val updateCycle: Float = GameLoop.targetUPS / spawnsPerSecond
        var waitUpdates: Float = 0f

        /**
         * Cycles between true and false.
         * Supposed to be used with a creep spawner.
         * Frequency can be controlled by the spawnsPerMinute inside Creep class
         * @see spawnsPerMinute
         */
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