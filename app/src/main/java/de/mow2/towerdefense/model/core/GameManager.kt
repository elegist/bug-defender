package de.mow2.towerdefense.model.core

import android.util.Log
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.Sounds
import de.mow2.towerdefense.controller.helper.GameState
import de.mow2.towerdefense.model.gameobjects.actors.*
import de.mow2.towerdefense.model.pathfinding.Astar
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.CopyOnWriteArrayList

interface GameController {
    var gameState: GameState
    fun updateGUI()
    fun updateHealthBarMax(newMax: Int)
    fun updateProgressBarMax(newMax: Int)
    fun onGameOver()
    fun showToastMessage(type: String)
}

/**
 * GameManager handles the game logic, updates game objects and calls updates on UI Thread
 * @param controller Class which handles UI related work and implements GameController interface
 */
class GameManager(private val controller: GameController) {
    private var towerDestroyerPatience = 3
    private var towerDestroyerPatienceCooldown: Long = 1000 * 300 // 5 minutes

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

    val waveSpawner = WaveSpawner()
    fun initLevel(level: Int) {
        gameLevel = 12
        //set the wave
        waveSpawner.initWave(gameLevel)
        when (level) {
            0 -> {
                /* Start game */
                livesAmnt = 10
                if (coinAmnt == 0) { //prevents save game cheating
                    coinAmnt = 500
                }
                controller.updateHealthBarMax(livesAmnt)
            }
            else -> {
                if (level % 10 == 0) {
                    increaseLives(level)
                }
                SoundManager.soundPool.play(Sounds.WAVE.id, 1F, 1F, 1, 0, 1F)
                // TODO: wave.remaining insufficient. Each enemy should have their own remaining stat
                controller.gameState.saveGameState() //auto-save progress
                controller.showToastMessage("wave")
            }
        }
        killsToProgress = waveSpawner.enemyCount
        controller.updateProgressBarMax(killsToProgress)

        killCounter = 0
        controller.updateGUI()
    }

    //check if target can be reached from spawn
    private val algs = Astar() //TODO: move into companion object?
    fun validatePlayGround() {
        waveActive = if (algs.findPath(
                Astar.Node(0, 0),
                Astar.Node(squaresX - 1, squaresY - 1),
                squaresX,
                squaresY
            ) != null
        ) {
            true
        } else {
            SoundManager.soundPool.play(Sounds.DESTROYER.id, 1f, 1f, 1, 0, 1f)
            towerDestroyer = TowerDestroyer(lastTower!!)
            towerDestroyerPatience--
            false
        }
    }

    /**
     * updates to game logic related values
     */
    fun updateLogic() {
        if (waveActive) {
            towerList.forEach towerIteration@{ tower ->
                if (tower.cooldown()) {
                    if (tower.target != null) {//tower already has a target
                        val distance =
                            tower.findDistance(tower.positionCenter, tower.target!!.positionCenter)
                        if (!tower.target!!.isDead && distance < tower.finalRange) {
                            tower.update()
                            tower.isShooting = true
                            when (tower.type) {
                                TowerTypes.AOE -> {
                                    addProjectile(Projectile(tower, tower.targetArray.last()))
                                    tower.targetArray.forEach { enemy ->
                                        enemy.takeDamage(tower.damage, tower)
                                    }
                                }
                                else -> {
                                    addProjectile(Projectile(tower, tower.target!!))
                                }
                            }
                        } else {
                            tower.target = null
                            tower.targetArray = emptyArray()
                            tower.isShooting = false
                        }
                    } else {//look for new target
                        when (tower.type) {
                            TowerTypes.AOE -> {
                                enemyList.forEach { enemy ->
                                    if (tower.findDistance(tower, enemy) < tower.finalRange) {
                                        tower.targetArray = tower.targetArray.plus(enemy)
                                        tower.target = enemy
                                    }
                                }
                            }
                            else -> {
                                enemyList.forEach { enemy ->
                                    if (tower.findDistance(tower, enemy) < tower.finalRange) {
                                        tower.target = enemy
                                        tower.targetArray = tower.targetArray.plus(enemy)
                                        return@towerIteration
                                    }
                                }
                            }
                        }
                    }
                }
            }
            projectileList.forEach { projectile ->
                val enemy = projectile.enemy
                //TODO: Best solution to collision detection would be using Rect.intersects, which needs android.graphics import ???
                if (enemy.findDistance(projectile.positionCenter, enemy.positionCenter) <= 15) {
                    enemy.takeDamage(projectile.baseDamage, projectile.tower)
                    projectileList.remove(projectile)
                }
                if (enemy.isDead) projectileList.remove(projectile)
                projectile.update()
            }

            /**
             * update movement, update target or remove enemy
             */
            enemyList.forEach { enemy ->
                if (enemy.position.y >= playGround.squareArray[0][squaresY - 1].position.y) { //enemy reached finish line
                    decreaseLives(enemy.baseDamage)
                    enemy.die()
                    increaseKills(enemy.killValue)
                    SoundManager.soundPool.play(Sounds.LIVELOSS.id, 1F, 1F, 1, 0, 1F)
                } else if (enemy.healthPoints <= 0) { //enemy dies
                    increaseCoins(enemy.coinValue)
                    enemy.die()
                    increaseKills(enemy.killValue)
                    SoundManager.soundPool.play(Sounds.CREEPDEATH.id, 10F, 10F, 1, 0, 1F)
                    enemiesKilled++
                } else {
                    enemy.update()
                }
            }

            /**
             * spawning enemies depending on the current gameLevel
             */
            waveSpawner.spawnEnemy()
        } else {
            /**
             * if the wave cannot find a valid path, a towerdestroyer will spawn and destroy the last tower
             * that was placed. If the player repeats this action a set amount of times it will destroy
             * the complete row of the recent placed tower
             * @see towerDestroyerPatience
             */
            if (towerDestroyer!!.isDone) {
                validatePlayGround()
                towerDestroyer = null
            } else {
                if (towerDestroyerPatience > 0) {
                    if (lastTower!!.positionCenter.x - 20 < towerDestroyer!!.positionCenter.x && towerDestroyer!!.positionCenter.x < lastTower!!.positionCenter.x + 20) {
                        lastTower!!.squareField.isBlocked = false
                        towerList.remove(lastTower!!)
                    }
                } else {
                    towerList.forEach { tower ->
                        if (tower.squareField.mapPos["y"] == lastTower!!.squareField.mapPos["y"]) {
                            if (tower.positionCenter.x - 20 < towerDestroyer!!.positionCenter.x && towerDestroyer!!.positionCenter.x < tower.positionCenter.x + 20) {
                                tower.squareField.isBlocked = false
                                towerList.remove(tower)
                            }
                        }
                    }
                }
                towerDestroyer!!.update()
            }

            /**
             * if the towerdestroyer already has destroyed a tower, this timer will determine,
             * when the patience value will be reset to its default value to give some leniency
             * to the player
             */
            if (towerDestroyerPatience < 3) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        towerDestroyerPatience = 3
                    }
                }, towerDestroyerPatienceCooldown)
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
        var waveActive = true
        var gameLevel = 0 // current level/wave
        var enemiesKilled: Int = 0 //total enemies spawned
        var enemiesAlive: Int = 0 //enemies currently on the PlayGround

        //game objects
        const val maxTowerLevel = 2
        var towerList = CopyOnWriteArrayList<Tower>()
        var enemyList = CopyOnWriteArrayList<Enemy>()
        var projectileList = CopyOnWriteArrayList<Projectile>()
        var towerDestroyer: TowerDestroyer? = null
        var lastTower: Tower? = null

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
            enemiesKilled = 0
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
        }

        private fun addProjectile(projectile: Projectile) {
            projectileList += projectile
        }

    }
}