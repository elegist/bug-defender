package de.mow2.towerdefense.model.core

import com.shashank.sony.fancytoastlib.FancyToast
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.gameobjects.actors.*
import java.util.concurrent.ConcurrentHashMap

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
            if(tower.cooldown()){
                tower.isShooting = false
                creepList.forEach{ creep ->
                    if (tower.findDistance(creep.positionX(), creep.positionY(), tower.x, tower.y) < tower.baseRange){
                        tower.isShooting = true
                        projectileList[Projectile(tower, creep)] = tower
                    }
                }
            }
        }

        projectileList.forEach { (projectile) ->
            creepList.forEach{ creep ->
                if(creep.findDistance(projectile.positionX(), projectile.positionY(), creep.positionX(), creep.positionY()) < 50){
                    projectileList.remove(projectile)
                    creep.takeDamage(projectile.baseDamage)
                }
            }

        }
        //TODO(): different spawn rates for different creepTypes
        //add enemies to the spawn
        if (Creep.canSpawn()) { //wait for update timer
            val creep = Creep(CreepTypes.LEAFBUG)
            addCreepToMap(creep) //add creeps to concurrentHashMap
            }
        /**
         * update movement, update target or remove enemy
         */
        val creepIterator = creepList.iterator()
        while(creepIterator.hasNext()) {
            val creep = creepIterator.next()
            if(creep.positionY().toInt() >= playGround.squareArray[0][squaresY -1].coordY.toInt()){
                decreaseLives(creep.baseDamage)
                creepIterator.remove()
                creepList.remove(creep)
            }else if(creep.healthPoints <= 0){
                increaseCoins(10)
                creepIterator.remove()
                increaseKills(1) //TODO: implement variable for worth of one kill (e.g. Bosses could count for more than 1 kill)
            }else{
                creep.update()
            }
        }

        projectileList.forEach{ (projectile) ->
            projectile.update()
        }
    }

    companion object {
        //playground variables
        const val squaresX = 9
        const val squaresY = 18
        var playGround = PlayGround(GameView.gameWidth)
        //static game variables
        var gameLevel = 0
        var towerList = mutableListOf<Tower>()
        var creepList = mutableListOf<Creep>()
        var projectileList: ConcurrentHashMap<Projectile, Tower> = ConcurrentHashMap()

        /**
         * Adds a tower and its customized bitmap to the drawing list
         * @param tower the tower to be added
         */
        fun addTowerToMap(tower: Tower) {
            towerList += tower
            towerList.sort()
        }
        // TODO: create one map out of all things to draw and sort it to get a good drawing order?
        /**
         * Adds a creep and its customized bitmap to the drawing list
         * @param creep the creep to be added
         */
        private fun addCreepToMap(creep: Creep) {
            creepList += creep
        }
    }
}