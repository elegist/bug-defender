package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D
import de.mow2.towerdefense.model.pathfinding.Astar
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * An instance of this class represents one specific enemy.
 * @param type One value of CreepTypes (e.g. leafbug, firebug...)
 * @param spawnPoint A-star node at which the enemy will spawn
 */
class Enemy(val type: EnemyType, private val spawnPoint: Astar.Node = Astar.Node(Random.nextInt(0 until GameManager.squaresX), 0)
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

    //path finding
    private val alg = Astar()
    private var targetIndex: Int = 0
    private var finalTarget = Astar.Node(spawnPoint.x, GameManager.squaresY-1) //initial destination
    private lateinit var sortedPath: List<Astar.Node>
    private var currentTargetNode: Astar.Node
    private var currentTargetPosition: Vector2D
    private var nextDistance = GameManager.playGround.squareSize*0.1f //min distance an enemy has to be from the currentTargetPosition to update it's path
    private var currentState: ActorState = ActorState.IDLE

    //queue sorting
    override fun compareTo(other: Enemy): Int = this.position.y.compareTo(other.position.y)

    //game variables
    var healthPoints = 0
    var baseDamage = 0
    var isDead = false
    var killValue = 0
    var coinValue = 0

    init{
       if(pathToEnd(spawnPoint)){
            currentTargetNode = sortedPath.first()
            currentTargetPosition = GameManager.playGround.squareArray[currentTargetNode.x][currentTargetNode.y].position
        }else{
            currentTargetNode = spawnPoint
            currentTargetPosition = GameManager.playGround.squareArray[spawnPoint.x][spawnPoint.y].position
        }

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
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 7 * GameManager.gameLevel else 8
                baseDamage = 2
                killValue = 1
                coinValue = 15
            }
            EnemyType.MAGMACRAB -> {
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 8 * GameManager.gameLevel else 8
                baseDamage = 3
                killValue = 2
                coinValue = 20
            }
            EnemyType.SCORPION ->{
                //TODO: balancing
                speed = 0.015f
                healthPoints = if(GameManager.gameLevel != 0) 8 * GameManager.gameLevel else 8
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.CLAMPBEETLE ->{
                //TODO: balancing
                speed = 0.07f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.FIREWASP ->{
                //TODO: balancing
                speed = 0.10f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.LOCUST -> {
                //TODO: balancing
                speed = 0.08f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.VOIDBUTTERFLY -> {
                //TODO: balancing
                speed = 0.08f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.SKELETONGRUNT -> {
                //TODO: balancing
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.NECROMANCER -> {
                //TODO: balancing
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.SKELETONWARRIOR -> {
                //TODO: balancing
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
                coinValue = 30
            }
            EnemyType.SKELETONKNIGHT -> {
                //TODO: balancing
                speed = 0.02f
                healthPoints = if(GameManager.gameLevel != 0) 1 * GameManager.gameLevel else 1
                baseDamage = 4
                killValue = 5
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
        when(currentState) {
            ActorState.IDLE -> {

            }
            ActorState.IS_MOVING -> {
                moveTo(currentTargetPosition)
            }
            ActorState.ATTACKING -> {
                //TODO()
            }
            ActorState.DEATH -> {
                //TODO()
            }
        }


        //search new path on each Node
        if(distanceToTargetAbs <= nextDistance){
            if(pathToEnd(currentTargetNode)) {
                currentState = ActorState.IS_MOVING
                currentTargetNode = sortedPath.first()
                findNextTarget()
            } else {
                currentState = ActorState.IDLE
            }
        }



    }

    /**
     * Looks for a path from given starting point (enemies current position or spawn point) to finish line
     * @param from Astar.Node which is the starting point
     * @param to Astar.Node which is the target
     * @return only true if a path has been found
     */
    private fun pathToEnd(from: Astar.Node): Boolean {
        val path = alg.findPath(from, finalTarget, GameManager.squaresX, GameManager.squaresY) //find path from spawn to targetNode
        return if(path != null) {//path is available
            sortedPath =
                if(!this::sortedPath.isInitialized) { //first call must initialize sortedPath!
                    path.reversed()
                } else {
                    if(sortedPath != path.reversed()) { //check if new path is different
                        path.reversed()
                    } else {
                        sortedPath
                    }
                }
          true
        } else {
            //waveActive = false
            false
        }
    }
    /**
     * Finds the device coordinates of the next node inside sortedPath
     * @see sortedPath
     */
    private fun findNextTarget() {
        currentTargetNode = sortedPath[targetIndex]
        finalTarget = Astar.Node(currentTargetNode.x, GameManager.squaresY-1)
        if (currentTargetNode != sortedPath.last()) {
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
        LEAFBUG, FIREBUG, MAGMACRAB, SCORPION, //ground insects
        CLAMPBEETLE, FIREWASP, LOCUST, VOIDBUTTERFLY, //flying insects
        SKELETONGRUNT, NECROMANCER, SKELETONWARRIOR, SKELETONKNIGHT, SKELETONKING, //skeletons and necromancer
        //CACODAEMON TODO: "boss" that destroys towers? Spritesheet only contains left/right! Should be a projectile
    }
}

