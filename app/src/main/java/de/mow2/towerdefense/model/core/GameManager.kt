package de.mow2.towerdefense.model.core

import com.shashank.sony.fancytoastlib.FancyToast
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.Sounds
import de.mow2.towerdefense.model.gameobjects.actors.*
import de.mow2.towerdefense.model.pathfinding.Astar
import java.util.concurrent.CopyOnWriteArrayList

interface GameController {
    fun updateGUI()
    fun updateHealthBarMax(newMax: Int)
    fun updateProgressBarMax(newMax: Int)
    fun onGameOver()
    fun showToastMessage(message: String, type: Int)
}

/**
 * GameManager handles the game logic, updates game objects and calls updates on UI Thread
 * @param controller Class which handles UI related work and implements GameController interface
 */
class GameManager(private val controller: GameController) {
    /**
     * Method to call when increasing coins (e.g. defeating an enemy creature or destroying a tower)
     * @param increaseValue the value to be added to the total coin amount
     */
    fun increaseCoins(increaseValue: Int) {
        coinAmnt += increaseValue
        controller.updateGUI()
    }

    /**
     * Decrease players available coins by given value
     * @param decreaseValue the value to subtract from the total amount
     * @return true if succeeds, returns false if player has not enough coins
     */
    fun decreaseCoins(decreaseValue: Int): Boolean {
        return if (coinAmnt >= (0 + decreaseValue)) {
            coinAmnt -= decreaseValue
            controller.updateGUI()
            true
        } else {
            false
        }
    }

    private fun increaseLives(newValue: Int) {
        livesAmnt += newValue
        controller.updateGUI()
    }

    private fun decreaseLives(newValue: Int): Boolean {
        return if (livesAmnt > (0 + newValue)) {
            livesAmnt -= newValue
            controller.updateGUI()
            true
        } else {
            controller.onGameOver()
            false
        }
    }

    /**
     * Increases the value of killCounter. This serves as an indicator for when to start a new / stronger wave of enemies
     */
    private fun increaseKills(newValue: Int) {
        killCounter += newValue
        if (killCounter >= killsToProgress) {
            initLevel(++gameLevel)
        }
        controller.updateGUI()
    }

    //TODO: (load game) GameState initialisiert nicht mit 0, daher stimmen Max health und max kills / wave nicht
    fun initLevel(level: Int) {
        //use to change starting game level
        gameLevel = 0 //default = 0
        //set the wave
        wave = Wave(gameLevel)
        when (level) {
            0 -> {
                /* Start game */
                livesAmnt = 100
                if (coinAmnt == 0) { //prevents save game cheating
                    coinAmnt = 5500
                }
                killsToProgress = 10
                controller.updateHealthBarMax(livesAmnt)
            }
            else -> {
                if (level % 10 == 0) {
                    increaseLives(5)
                }
                // TODO: wave.remaining insufficient. Each enemy should have their own remaining stat
                killsToProgress = wave.remaining
            }
        }
        controller.updateProgressBarMax(killsToProgress)
        controller.showToastMessage("Level:  $gameLevel", FancyToast.SUCCESS)

        killCounter = 0
        controller.updateGUI()
    }

    //check if target can be reached from spawn
    private val algs = Astar() //TODO: move into companion object?
    fun validatePlayGround() {
        waveActive = algs.findPath(
            Astar.Node(0, 0),
            Astar.Node(squaresX - 1, squaresY - 1),
            squaresX,
            squaresY
        ) != null
    }

    /**
     * updates to game logic related values
     */
    fun updateLogic() {
        if(waveActive){
            //TODO: apply different damage types and effects
            towerList.forEach towerIteration@{ tower ->
                if(tower.cooldown()) {
                    if(tower.target != null) {//tower already has a target
                        val distance = tower.findDistance(tower.positionCenter, tower.target!!.positionCenter)
                        if(!tower.target!!.isDead && distance < tower.finalRange) {
                            tower.update()
                            addProjectile(Projectile(tower, tower.target!!))
                            tower.isShooting = true
                        } else {
                            tower.target = null
                            tower.isShooting = false
                        }
                    } else {//look for new target
                        enemyList.forEach{ enemy ->
                            if(tower.findDistance(tower, enemy) < tower.finalRange) {
                                tower.target = enemy
                                return@towerIteration
                            }
                        }
                    }
                }
            }
            projectileList.forEach { projectile ->
                val enemy = projectile.enemy
                //TODO: Best solution to collision detection would be using Rect.intersects, which needs android.graphics import ???
                if(enemy.findDistance(projectile.positionCenter, enemy.positionCenter) <= 15){
                    enemy.takeDamage(projectile.baseDamage, projectile.tower.type)
                    projectileList.remove(projectile)
                }
                if(enemy.isDead) projectileList.remove(projectile)
                projectile.update()
            }

            /**
             * spawning enemies depending on the current gameLevel
             */
            spawner.spawnWave(wave)
        }
        /**
         * update movement, update target or remove enemy
         */
        enemyList.forEach { enemy ->
            if(enemy.position.y >= playGround.squareArray[0][squaresY - 1].position.y){ //enemy reached finish line
                decreaseLives(enemy.baseDamage)
                enemy.die()
                SoundManager.soundPool.play(Sounds.LIVELOSS.id, 1F, 1F, 1, 0, 1F)
                increaseKills(enemy.killValue)
            }else if(enemy.healthPoints <= 0){ //enemy dies
                increaseCoins(enemy.coinValue)
                enemy.die()
                SoundManager.soundPool.play(Sounds.CREEPDEATH.id, 1F, 1F, 1, 0, 1F)
                increaseKills(enemy.killValue) //TODO: implement variable for worth of one kill (e.g. Bosses could count for more than 1 kill)
            }else{
                enemy.update()
            }
        }
    }

    companion object {
        //tutorials
        var tutorialsActive = true

        //playground variables
        const val squaresX = 9
        const val squaresY = 18
        var playGround = PlayGround(GameView.gameWidth)

        //static game variables
        var coinAmnt: Int = 0
        var livesAmnt: Int = 0
        var killCounter: Int = 0
        var killsToProgress: Int = 0

        //game objects
        const val maxTowerLevel = 2
        var towerList = CopyOnWriteArrayList<Tower>()
        var enemyList = CopyOnWriteArrayList<Enemy>()
        var projectileList = CopyOnWriteArrayList<Projectile>()
        var lastTower: Tower? = null

        //spawner variables
        val spawner = Spawner()
        var wave = Wave(0) // default wave is 0 (gameLevel 0)
        var waveActive = true
        var gameLevel = 0 // current level/wave
        var enemyCounter: Int = 0 //total enemies spawned
        var enemiesAlive: Int = 0 //enemies currently on the PlayGround

        // build menu variables
        var selectedTool: Int? = null
        var selectedTower = TowerTypes.SINGLE // default tower

        /**
         * Reset all game variables
         */
        fun reset() {
            playGround = PlayGround(GameView.gameWidth)
            towerList = CopyOnWriteArrayList<Tower>()
            enemyList = CopyOnWriteArrayList()
            projectileList = CopyOnWriteArrayList()
            coinAmnt = 0
            livesAmnt = 0
            killCounter = 0
            selectedTool = null
            selectedTower = TowerTypes.SINGLE
            lastTower = null
            gameLevel = 0
            enemyCounter = 0
            enemiesAlive = 0
        }

        fun addTower(tower: Tower) {
            towerList += tower
            towerList.sort()
        }

        // TODO: create one map out of all things to draw and sort it to get a good drawing order?
        fun addEnemy(enemy: Enemy) {
            enemyList += enemy
            enemyList.sort()
            enemyList.reverse()
            enemyCounter++
        }

        private fun addProjectile(projectile: Projectile) {
            projectileList += projectile
        }

    }
}