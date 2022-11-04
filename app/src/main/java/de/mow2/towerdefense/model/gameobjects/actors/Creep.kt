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
 * An instance of this class represents one specific enemy.
 * @param type One value of CreepTypes (e.g. leafbug, firebug...)
 * @param squareField The squareField on which this creep will spawn
 */
class Creep(type: CreepTypes, squareField: SquareField = GameManager.playGround.squareArray[(Random.nextInt(0 until GameManager.squaresX))][0]
): GameObject(squareField) {
    val alg = Astar()
    var w: Int = squareField.width
    var h: Int = squareField.height
    //walking direction
   var orientation: Int = 0 //TODO: Change value based on walking direction! (0 = down, 1 = up, 2 = left/right) maybe develop a better solution??
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