package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.pathfinding.Astar
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * An instance of this class represents one specific enemy.
 * @param type One value of CreepTypes (e.g. leafbug, firebug...)
 * @param spawnPoint A-star node at which the enemy will spawn
 */
class Enemy(val type: EnemyType, spawnPoint: Astar.Node = Astar.Node(Random.nextInt(0 until GameManager.squaresX), 0)
): GameObject(), Comparable<Enemy>, java.io.Serializable {
    /**
     * Pixels per update for movement.
     * Will be multiplied with direction to get a velocity.
     * @see moveTo()
     */
    override var speed: Float = 0f
        set(value) {
            val rawPixels = (GameView.gameWidth + GameView.gameHeight) * value
            field = rawPixels / GameLoop.targetUPS
        }
    //position
    override var position = GameManager.playGround.squareArray[spawnPoint.x][spawnPoint.y].position
    //size
    override var height = GameManager.playGround.squareSize
    override var width = height
    //walking direction
    var orientation: Int = 0 //TODO: Change value based on walking direction! (0 = down, 1 = up, 2 = left/right) maybe develop a better solution??
    //path finding
    private val alg = Astar()
    private var targetIndex: Int = 0
    private var targetNode = Astar.Node(spawnPoint.x, GameManager.squaresY-1)
    private var path = alg.findPath(spawnPoint, targetNode, GameManager.squaresX, GameManager.squaresY)
    private var sortedPath= path?.reversed()
    private var currentPath = sortedPath
    private var currentTargetNode = currentPath!!.first()
    private var currentTargetPosition = GameManager.playGround.squareArray[currentTargetNode.x][currentTargetNode.y].position

    //queue sorting
    override fun compareTo(other: Enemy): Int = this.position.y.compareTo(other.position.y)

    //game variables
    var healthPoints = 0
    var baseDamage = 0
    var isDead = false
    var killValue = 0
    var coinValue = 0

    init{
        //spawn frequency
        actionsPerMinute = 120f

        /**
         * set speed, health and baseDamage depending on the type of the creep instance
         */
        when(type){
            EnemyType.LEAFBUG -> {
                speed = 0.03f
                healthPoints = if(GameManager.gameLevel != 0) 5 * GameManager.gameLevel else 5
                baseDamage = 1
                killValue = 1
                coinValue = 10
            }
            EnemyType.FIREBUG -> {
                //TODO()
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 8 * GameManager.gameLevel else 8
                baseDamage = 3
                killValue = 1
                coinValue = 10
            }
            EnemyType.MAGMACRAB -> {
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 8 * GameManager.gameLevel else 8
                baseDamage = 3
                killValue = 2
                coinValue = 20
            }
            EnemyType.SKELETONKNIGHT -> {
                //TODO()
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 8 * GameManager.gameLevel else 8
                baseDamage = 3
                killValue = 3
                coinValue = 30
            }
            EnemyType.SKELETONKING -> {
                speed = 0.005f
                healthPoints = if(GameManager.gameLevel != 0) 20 * GameManager.gameLevel else 20
                baseDamage = 10
                killValue = 5
                coinValue = 50
            }
        }

    }

    override fun update(){
        /**
         * math for the movement calculation:
         * https://www.codeproject.com/articles/990452/interception-of-two-moving-objects-in-d-space
         */
        moveTo(currentTargetPosition)
        orientation = if(distance.x < -5) {
            3 //left
        } else if(distance.x > 5) {
            2 //right
        } else if(distance.y < 0) {
            1 //up
        } else {
            0 //down (default)
        }

        //TODO: distance check might break on different screen sizes
        //check if creep distance is close enough to the target so it can create a new path
        if(distanceToTargetAbs <= GameManager.playGround.squareSize*0.10){
            targetNode = Astar.Node(currentTargetNode.x, GameManager.squaresY-1)
            path = Astar().findPath(currentTargetNode, targetNode, GameManager.squaresX, GameManager.squaresY)
            sortedPath = path?.reversed()

            if(currentPath != sortedPath){
                currentPath = sortedPath
            }

            findNextTarget()
        }
    }

    /**
     * Finds the device coordinates of the next node inside currentPath List of this creep and
     * updates target position
     */
    private fun findNextTarget() {
        currentTargetNode = currentPath!![targetIndex]
        if (currentTargetNode != currentPath!!.last()) {
            targetIndex++
        }
        currentTargetPosition = GameManager.playGround.squareArray[currentTargetNode.x][currentTargetNode.y].position
    }

    //TODO: Damagetype (slow, poison, burn) as parameter?
    fun takeDamage(damageAmount: Int){
        healthPoints -= damageAmount
    }

    /**
     * list of all enemies
     */
    enum class EnemyType {
        LEAFBUG, FIREBUG, MAGMACRAB, SKELETONKNIGHT, SKELETONKING
    }

}

