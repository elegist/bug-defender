package de.mow2.towerdefense.model.core

import com.shashank.sony.fancytoastlib.FancyToast
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.gameobjects.actors.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue

/**
 * GameManager handles the game logic, updates game objects and calls updates on UI Thread
 */
class GameManager(private val callBack: GameActivity) {
    //debug
    private val TAG = javaClass.name
    //variables
    var coinAmnt: Int = 0
    var livesAmnt: Int = 0
    var killCounter: Int = 0

    private fun updateGUI() {
        callBack.runOnUiThread {
            callBack.coinsTxt.text = coinAmnt.toString()
            callBack.healthBar.progress = livesAmnt
            callBack.waveBar.progress = killCounter
        }
    }
    /**
     * Method to call when increasing coins (e.g. defeating an enemy creature or destroying a tower)
     * @param increaseValue the value to be added to the total coin amount
     */
    fun increaseCoins(increaseValue: Int){
        coinAmnt += increaseValue
        updateGUI()
    }
    /**
     * Method to call when decreasing coins (e.g. Building or upgrading a tower)
     * @param decreaseValue the value to subtract from the total amount
     */
    fun decreaseCoins(decreaseValue: Int) : Boolean {
        return if(coinAmnt >= (0 + decreaseValue)) {
            coinAmnt -= decreaseValue
            updateGUI()
            true
        } else {
            false
        }
    }
    private fun increaseLives(newValue: Int){
        livesAmnt += newValue
        updateGUI()
    }
    private fun decreaseLives(newValue: Int) : Boolean {
        return if(livesAmnt > (0 + newValue)) {
            livesAmnt -= newValue
            updateGUI()
            true
        } else {
            callBack.runOnUiThread { callBack.onGameOver() }
            false
        }
    }
    /**
     * Increases the value of killCounter. This serves as an indicator for when to start a new / stronger wave of creeps
     */
    private fun increaseKills(newValue: Int){
        killCounter += newValue
        if(killCounter >= callBack.waveBar.max) {
            initLevel(++gameLevel)
        }
        updateGUI()
    }

    private var killsToProgress = 0
    fun initLevel(level: Int) {
        when(level) {
            0 -> {
                /* Start game */
                livesAmnt = 10
                coinAmnt = 400
                killsToProgress = 10
                callBack.runOnUiThread { callBack.healthBar.max = livesAmnt }
            }
            else -> {
                if(level % 10 == 0) {
                    //TODO: spawn boss wave
                    increaseLives(5)
                }
                /* Define next wave */
                killsToProgress = killCounter * level
            }
        }
        callBack.runOnUiThread {
            callBack.waveBar.max = killsToProgress
            FancyToast.makeText(callBack, "Welle: $gameLevel", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false ).show()
        }
        killCounter = 0
        updateGUI()
        //TODO:
        // make creeps stronger, could be a multiplier or defined values for each wave
    }

    /**
     * updates to game logic related values
     */
    fun updateLogic() {
        towerList.forEach { tower ->
            //TODO: apply different damage types and effects
            if(tower.cooldown()){
                tower.hasTarget = false
                creepList.forEach{ creep ->
                    if (tower.findDistance(creep.positionX(), creep.positionY(), tower.x, tower.y) < tower.baseRange){//if creep is in range of tower
                        if(tower.target == null) {//select new target if tower has none
                            tower.target = creep
                            tower.hasTarget = true
                        } else {//tower already has a target: shoot
                            addProjectile(Projectile(tower, tower.target!!))
                        }
                    } else {//target is lost: stop shooting
                        tower.target = null
                    }
                }
            }
        }

        projectileList.forEach { projectile ->
            val creep = projectile.creep
            //TODO: Best solution to collision detection would be using Rect.intersects, which needs android.graphics import ???
            if(creep.findDistance(projectile.positionX(), projectile.positionY(), creep.positionX(), creep.positionY()) <= 15){
                projectileList.remove(projectile)
            }
            projectile.update()
        }
        //TODO(): different spawn rates for different creepTypes
        //add enemies to the spawn
        if (Creep.canSpawn()) { //wait for update timer
            val creep = Creep(CreepTypes.LEAFBUG)
            addCreep(creep) //add creeps to concurrentHashMap
            }
        /**
         * update movement, update target or remove enemy
         */
        creepList.forEach { creep ->
            if(creep.positionY() >= playGround.squareArray[0][squaresY - 1].coordY){
                decreaseLives(creep.baseDamage)
                creepList.remove(creep)
            }else if(creep.healthPoints <= 0){
                increaseCoins(10)
                creepList.remove(creep)
                increaseKills(1) //TODO: implement variable for worth of one kill (e.g. Bosses could count for more than 1 kill)
            }else{
                creep.update()
            }
        }
    }

    companion object {
        //playground variables
        const val squaresX = 9
        const val squaresY = 18
        var playGround = PlayGround(GameView.gameWidth)
        //static game variables
        var gameLevel = 0
        var towerList = PriorityBlockingQueue<Tower>()
        var creepList = LinkedBlockingQueue<Creep>()
        var projectileList = LinkedBlockingQueue<Projectile>()

        fun reset() {
            towerList = PriorityBlockingQueue()
            creepList = LinkedBlockingQueue()
            projectileList = LinkedBlockingQueue()
            gameLevel = 0
            playGround = PlayGround(GameView.gameWidth)
        }
        fun addTower(tower: Tower) {
            towerList += tower
        }
        // TODO: create one map out of all things to draw and sort it to get a good drawing order?
        private fun addCreep(creep: Creep) {
            creepList += creep
        }
        private fun addProjectile(projectile: Projectile) {
            projectileList += projectile
        }
    }
}