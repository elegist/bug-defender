package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.GameLoop
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
class Creep(type: CreepTypes, squareField: SquareField = GameManager.playGround.squareArray[(Random.nextInt(0 until GameManager.squaresX))][0]
): GameObject(squareField) {
    val alg = Astar()
    var w: Int = squareField.width
    var h: Int = squareField.height
    //path finding
    var path: List<Astar.Node> = emptyList()
    set(value) {
        field = value
        if(targetIndex == path.size){
            targetIndex = 0
        }
    }
    var target: Astar.Node = Astar.Node(this.squareField.mapPos["x"]!!, this.squareField.mapPos["y"]!!)
    var targetIndex: Int = 0
    var targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
    var targetY = GameManager.playGround.squareArray[target.x][target.y].coordY

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
        val posX: Int = this.squareField.mapPos["x"]!!
        val posY: Int = this.squareField.mapPos["y"]!!
        val creepNode = Astar.Node(posX, posY)
        val targetNode = Astar.Node(posX, 17)
        val path = alg.findPath(creepNode, targetNode, GameManager.squaresX, GameManager.squaresY)
        if(path != null) {
            val sortedPath = path.reversed()
            this.path = sortedPath
        }
        /**
         * math for the movement calculation:
         * https://www.codeproject.com/articles/990452/interception-of-two-moving-objects-in-d-space
         */
        //vector between enemy and target
        var distanceToTargetX: Float = targetX - positionX()
        var distanceToTargetY: Float = targetY - positionY()
        //absolute distance
        var distanceToTargetAbs: Float = findDistance(this.positionX(), this.positionY(), targetX, targetY)
        if(distanceToTargetAbs.toInt() <= GameManager.playGround.squareSize*0.10) findNextTarget()
        //direction
        var directionX: Float = distanceToTargetX/distanceToTargetAbs
        var directionY: Float = distanceToTargetY/distanceToTargetAbs
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
    }

    private fun findNextTarget() {
        target = path[targetIndex]
        if(target != path.last()) {
                targetY = GameManager.playGround.squareArray[target.x][target.y].coordY
                targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
                targetIndex++
        } else {
            targetX = GameManager.playGround.squareArray[target.x][target.y].coordX
            targetY = GameManager.playGround.squareArray[target.x][target.y].coordY
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