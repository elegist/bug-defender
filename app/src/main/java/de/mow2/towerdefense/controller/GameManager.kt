package de.mow2.towerdefense.controller

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.*
import android.util.Log
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.gameobjects.actors.*
import java.util.concurrent.ConcurrentHashMap

/**
 * GameManager holds static access to game variables like bitmaps, in-game values and such
 * it also manages drawing onto canvas
 */
class GameManager: ViewModel() {
    lateinit var resources: Resources

    //debug
    private val TAG = javaClass.name
    //variables
    val coinAmnt: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val livesAmnt: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val killCounter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    /**
     * Method to call when increasing coins (e.g. defeating an enemy creature or destroying a tower)
     * @param increaseValue the value to be added to the total coin amount
     */
    fun increaseCoins(increaseValue: Int){
        val oldVal = coinAmnt.value!!
        coinAmnt.postValue(oldVal + increaseValue)
    }
    /**
     * Method to call when decreasing coins (e.g. Building or upgrading a tower)
     * @param decreaseValue the value to subtract from the total amount
     */
    fun decreaseCoins(decreaseValue: Int) : Boolean {
        val oldVal = coinAmnt.value!!
        return if(coinAmnt.value!! >= (0 + decreaseValue)) {
            coinAmnt.postValue(oldVal - decreaseValue)
            true
        } else {
            false
        }
    }
    fun increaseLives(newValue: Int){
        val oldVal = livesAmnt.value!!
        livesAmnt.postValue(oldVal + newValue)
    }
    private fun decreaseLives(newValue: Int) : Boolean {
        val oldVal = livesAmnt.value!!
        return if(livesAmnt.value!! >= (0 + newValue)) {
            livesAmnt.postValue(oldVal - newValue)
            true
        } else {
            false
        }
    }

    /**
     * Increases the value of killCounter. This serves as an indicator for when to start a new / stronger wave of creeps
     */
    private fun increaseKills(newValue: Int){
        val oldVal = killCounter.value!!
        killCounter.postValue(oldVal + newValue)
    }

    private var killsToProgress = 0
    fun initLevel(level: Int, healthBar: ProgressBar, waveBar: ProgressBar) {
        when(level) {
            0 -> {
                /* Start game */
                livesAmnt.value = 30
                coinAmnt.value = 1000
                killsToProgress = 10
                healthBar.max = livesAmnt.value!!
            }
            else -> {
                if(level % 10 == 0) {
                    //spawn boss wave
                }
                /* Define next wave */
                killsToProgress = killCounter.value!!.times(level)
            }
        }
        killCounter.value = 0
        waveBar.max = killsToProgress
        //TODO:
        // make creeps stronger, could be a multiplier or defined values for each wave
    }
    /**
     * Initialize all images and hold references for further use
     * Should improve performance compared to decoding bitmaps while drawing
     */
    fun initImages() {
        //TODO: do the same with creeps and all other images,
        // result should be one Map each holding its type as key and animation as value for all creeps, towers, projectiles, weapons and so on
        val width = playGround.squareArray[0][0].width
        val height = playGround.squareArray[0][0].height
        TowerTypes.values().forEach { key ->
            val towerR: Int
            val weaponAnimR: Int
            val frameCount: Int
            when(key) {
                TowerTypes.BLOCK -> {
                    towerR = R.drawable.tower_block
                    weaponAnimR = R.drawable.tower_block_weapon_anim_1
                    frameCount = 6
                }
                TowerTypes.SLOW -> {
                    towerR = R.drawable.tower_slow
                    weaponAnimR = R.drawable.tower_slow_weapon_anim_1
                    frameCount = 16
                }
                TowerTypes.AOE -> {
                    towerR = R.drawable.tower_aoe
                    weaponAnimR = R.drawable.tower_block_weapon_anim_1
                    frameCount = 6
                }
            }
            towerImages[key] = ScaledImage(resources, width, height * 2, towerR).scaledImage
            weaponAnims[key] = SpriteAnimation(BitmapFactory.decodeResource(resources, weaponAnimR), width, height, 1, frameCount, 100)
        }
    }

    // TODO: create one map out of all things to draw and sort it to get a good drawing order?
    /**
     * Adds a creep and its customized bitmap to the drawing list
     * @param creep the creep to be added
     */
    private fun addCreepToMap(creep: Creep) {
        //TODO: maybe sort map for drawing order? Also: CreepTypes
        val spriteSheet = SpriteAnimation(BitmapFactory.decodeResource(resources, R.drawable.leafbug_anim), creep.w, creep.h)
        creepList[creep] = spriteSheet
    }
    /**
     * decides which objects to draw
     */
    fun drawObjects(canvas: Canvas, resources: Resources) {
        creepList.forEach{ (enemy, animation) ->
            draw(canvas, animation!!.nextFrame(enemy.orientation), enemy.positionX(), enemy.positionY())
        }
        towerList.forEach { tower ->
            draw(canvas, towerImages[tower.type], tower.x, tower.y)
            if(tower.isShooting) {
                draw(canvas, weaponAnims[tower.type]!!.nextFrame(0), tower.x, tower.y)
            } else {
                draw(canvas, weaponAnims[tower.type]!!.idleImage, tower.x, tower.y)
            }
        }
        projectileList.forEach{ (projectile) ->
            draw(canvas, BitmapFactory.decodeResource(resources, R.drawable.projectile), projectile.positionX(), projectile.positionY())
        }
    }

    /**
     * updates to game logic related values
     */
    @SuppressLint("SuspiciousIndentation")
    fun updateLogic() {

        towerList.forEach { tower ->
            if(tower.cooldown()){
                tower.isShooting = false
                creepList.forEach{ (creep) ->
                    if (tower.findDistance(creep.positionX(), creep.positionY(), tower.x, tower.y) < tower.baseRange){
                        tower.isShooting = true
                        projectileList[Projectile(tower, creep)] = tower
                    }
                }
            }
        }

        projectileList.forEach { (projectile) ->
            creepList.forEach{ (creep) ->
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
        creepList.forEach{ (creep) ->
            if(creep.positionY().toInt() >= playGround.squareArray[0][squaresY-1].coordY.toInt()){
                decreaseLives(creep.baseDamage)
                creepList.remove(creep)
            }else if(creep.healthPoints <= 0){
                increaseCoins(10)
                creepList.remove(creep)
                increaseKills(1) //TODO: implement variable for worth of one kill (e.g. Bosses could count for more than 1 kill)
            }else{
                creep.update()
            }
            //TODO: update enemy target
        }

        projectileList.forEach{ (projectile) ->
            projectile.update()
        }
    }

    /**
     * draws a bitmap onto canvas
     */
    @Synchronized private fun draw(canvas: Canvas, bitmap: Bitmap?, posX: Float, posY: Float) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, posX, posY, null)
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
        var projectileList: ConcurrentHashMap<Projectile, Tower> = ConcurrentHashMap()
        private var creepList = ConcurrentHashMap<Creep, SpriteAnimation?>()
        //all various lists and maps for game objects and their respective bitmaps or animations
        var towerImages = ConcurrentHashMap<TowerTypes, Bitmap>()
        private var weaponAnims = ConcurrentHashMap<TowerTypes, SpriteAnimation?>()

        /**
         * Adds a tower and its customized bitmap to the drawing list
         * @param tower the tower to be added
         */
        fun addTowerToMap(tower: Tower) {
            towerList += tower
            towerList.sort()
        }
    }
}