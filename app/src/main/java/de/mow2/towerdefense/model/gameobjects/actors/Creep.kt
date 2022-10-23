package de.mow2.towerdefense.model.gameobjects.actors

import android.util.Log
import de.mow2.towerdefense.controller.GameLoop
import de.mow2.towerdefense.controller.GameManager
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.pathfinding.Astar
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * 
 */
//TODO: ist squareField als parameter wirklich sinnvoll? vielleicht eher node verwenden
class Creep(type: CreepTypes, squareField: SquareField = GameView.playGround.squareArray[(Random.nextInt(0 until GameManager.squaresX))][0]
): GameObject(squareField) {
    var w: Int = squareField.width
    var h: Int = squareField.height
    //path finding
    var path: List<Astar.Node> = emptyList()
    set(value) {
        field = value
        findNextTarget()
        GameManager.comparePathCoords(value)
    }
    var target: Astar.Node = Astar.Node(this.squareField.mapPos["x"]!!, this.squareField.mapPos["y"]!!)
    var targetIndex: Int = 0
    var targetX = GameView.playGround.squareArray[target.x][target.y].coordX
    var targetY = GameView.playGround.squareArray[target.x][target.y].coordY

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
        var distanceToTargetX: Float = targetX - positionX()
        var distanceToTargetY: Float = targetY - positionY()
        //absolute distance
        var distanceToTargetAbs: Float = findDistance(this.positionX(), this.positionY(), targetX, targetY)
        if(distanceToTargetAbs.toInt() <= 1) findNextTarget()
        //direction
        var directionX: Float = distanceToTargetX/distanceToTargetAbs
        var directionY: Float = distanceToTargetY/distanceToTargetAbs
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

    private fun findNextTarget() {
        target = path[targetIndex]
        if(target != path.last()) {
            targetX = GameView.playGround.squareArray[target.x][target.y].coordX
            targetY = GameView.playGround.squareArray[target.x][target.y].coordY
            targetIndex++
        } else {
            targetX = GameView.playGround.squareArray[target.x][target.y].coordX
            targetY = GameView.playGround.squareArray[target.x][target.y].coordY
        }
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