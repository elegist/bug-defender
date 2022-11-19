package de.mow2.towerdefense.model.core

import android.view.Gravity
import com.shashank.sony.fancytoastlib.FancyToast
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.Sounds
import de.mow2.towerdefense.model.gameobjects.actors.*
import de.mow2.towerdefense.model.gameobjects.actors.Enemy.EnemyType
import java.util.concurrent.CopyOnWriteArrayList

/**
 * GameManager handles the game logic, updates game objects and calls updates on UI Thread
 */
class GameManager(private val callBack: GameActivity) {
    /**
     * Method to write all GUI-related data into their respective layout element
     */
    private fun updateGUI() {
        callBack.runOnUiThread {
            callBack.coinsTxt.text = coinAmnt.toString()
            callBack.healthBar.progress = livesAmnt
            callBack.waveBar.progress = killCounter
            val livesText = "$livesAmnt / ${callBack.healthBar.max}"
            callBack.healthText.text = livesText
            val waveText = "$killCounter / ${callBack.waveBar.max}"
            callBack.waveText.text = waveText
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
     * Decrease players available coins by given value
     * @param decreaseValue the value to subtract from the total amount
     * @return true if succeeds, returns false if player has not enough coins
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
     * Increases the value of killCounter. This serves as an indicator for when to start a new / stronger wave of enemies
     */
    private fun increaseKills(newValue: Int){
        killCounter += newValue
        if(killCounter >= callBack.waveBar.max) {
            initLevel(++gameLevel)
        }
        updateGUI()
    }

    //TODO: (load game) GameState initialisiert nicht mit 0, daher stimmen Max health und max kills / wave nicht
    private var killsToProgress = 0
    fun initLevel(level: Int) {
        when(level) {
            0 -> {
                /* Start game */
                livesAmnt = 10
                if(coinAmnt == 0) { //prevents save game cheating
                    coinAmnt = 5500
                }
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
            val toast = FancyToast.makeText(callBack, "Welle: $gameLevel", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false )
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
        killCounter = 0
        updateGUI()
        //TODO:
        // make enemies stronger, could be a multiplier or defined values for each wave
    }

    /**
     * updates to game logic related values
     */
    fun updateLogic() {
        //TODO: apply different damage types and effects
        towerList.forEach towerIteration@{ tower ->
            if(tower.cooldown()) {
                if(tower.target != null) {//tower already has a target
                    val distance = tower.findDistance(tower.positionCenter, tower.target!!.positionCenter)
                    if(!tower.target!!.isDead && distance < tower.finalRange) {
                        addProjectile(Projectile(tower, tower.target!!))
                    } else {
                        tower.target = null
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
            projectile.update()
        }
        //TODO(): different spawn rates for different enemyTypes
        if(canSpawn() && waveActive){
            //add enemies to the spawn
            addEnemy(Enemy(EnemyType.LEAFBUG))
            addEnemy(Enemy(EnemyType.MAGMACRAB))
            if(gameLevel > 1) {
                addEnemy(Enemy(EnemyType.SKELETONKING))
            }
        }

        /**
         * update movement, update target or remove enemy
         */
        
        enemyList.forEach { enemy ->
            if(enemy.position.y >= playGround.squareArray[0][squaresY - 1].position.y){ //enemy reached finish line
                decreaseLives(enemy.baseDamage)
                enemy.isDead = true
                SoundManager.soundPool.play(Sounds.LIVELOSS.id, 1F, 1F, 1, 0, 1F)
                enemyList.remove(enemy)
            }else if(enemy.healthPoints <= 0){ //enemy dies
                increaseCoins(enemy.coinValue)
                enemyList.remove(enemy)
                enemy.isDead = true
                SoundManager.soundPool.play(Sounds.CREEPDEATH.id, 1F, 1F, 1, 0, 1F)
                increaseKills(enemy.killValue) //TODO: implement variable for worth of one kill (e.g. Bosses could count for more than 1 kill)
            }else{
                enemy.update()
            }
        }
    }

    companion object {
        //playground variables
        const val squaresX = 9
        const val squaresY = 18
        var playGround = PlayGround(GameView.gameWidth)
        //static game variables
        var coinAmnt: Int = 0
        var livesAmnt: Int = 0
        var killCounter: Int = 0
        var gameLevel = 0
        //game objects
        const val maxTowerLevel = 2
        var towerList = CopyOnWriteArrayList<Tower>()
        var enemyList = CopyOnWriteArrayList<Enemy>()
        var projectileList = CopyOnWriteArrayList<Projectile>()
        var waveActive = true //TODO(): set to toggle enemy waves

        // build menu variables
        var selectedTool: Int? = null
        var selectedTower = TowerTypes.BLOCK // default tower

        /**
         * Reset all game variables
         */
        fun reset() {
            playGround = PlayGround(GameView.gameWidth)
            towerList = CopyOnWriteArrayList<Tower>()
            enemyList = CopyOnWriteArrayList()
            projectileList = CopyOnWriteArrayList()
            gameLevel = 0
            coinAmnt = 0
            livesAmnt = 0
            killCounter = 0
            selectedTool = null
            selectedTower = TowerTypes.BLOCK
        }
        fun addTower(tower: Tower) {
            towerList += tower
            towerList.sort()
        }
        // TODO: create one map out of all things to draw and sort it to get a good drawing order?
        private fun addEnemy(enemy: Enemy) {
            enemyList += enemy
            enemyList.sort()
            enemyList.reverse()
        }
        private fun addProjectile(projectile: Projectile) {
            projectileList += projectile
        }


        private var updateCycle: Float = 0f
        private var waitUpdates: Float = 0f
        //set spawn rate
        var actionsPerMinute: Float = 0f
            set(value){
                field = value
                val actionsPerSecond: Float = field / 60
                //link with target updates per second to convert to updates per spawn
                updateCycle = GameLoop.targetUPS / actionsPerSecond

            }
        fun canSpawn(): Boolean{
            actionsPerMinute = 10f
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