package de.mow2.towerdefense.controller

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.gameobjects.actors.*
import java.util.concurrent.ConcurrentHashMap

/**
 * GameManager holds static access to game variables like bitmaps, in-game values and such
 * it also manages drawing onto canvas
 */
object GameManager {
    //playground variables
    const val squaresX = 9
    const val squaresY = 18
    var playGround = PlayGround(GameView.gameWidth)
    lateinit var resources: Resources

    //all various lists and maps for game objects and their respective bitmaps or animations
    private var projectileList: ConcurrentHashMap<Projectile, Tower> = ConcurrentHashMap()
    var towerList = mutableListOf<Tower>()
    var towerImages = ConcurrentHashMap<TowerTypes, Bitmap>()
    private var creepList = ConcurrentHashMap<Creep, SpriteAnimation?>()
    private var weaponAnims = ConcurrentHashMap<TowerTypes, SpriteAnimation?>()
    //debug
    private val TAG = javaClass.name

    /**
     * call to hard-reset GameManager (remove any remaining towers, creeps, etc.) e.g. Leaving a Game, starting a new one
     */
    fun resetManager() {
        playGround = PlayGround(GameView.gameWidth)
        towerList = mutableListOf()
        creepList = ConcurrentHashMap()
        projectileList = ConcurrentHashMap()
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
    fun updateLogic() {
        if(Projectile.canSpawn()){
            towerList.forEach { tower ->
                tower.isShooting = false
                creepList.forEach{ (creep) ->
                    if (GameObject.findDistance(creep.positionX(), creep.positionY(), tower.x, tower.y) < tower.baseRange){
                        tower.isShooting = true
                        projectileList[Projectile(tower.squareField, tower, creep)] = tower
                    }
                }
            }
        }

        projectileList.forEach { (projectile) ->
//            creepList.forEach{ (creep) ->
//                if(GameObject.findDistance(projectile.positionX(), projectile.positionY(), creep.positionX(), creep.positionY()) < 50){
//                    projectileList.remove(projectile)
//                    creep.takeDamage(projectile.baseDamage)
//                }
//            }
            for ((creep) in creepList){
                if(GameObject.findDistance(projectile.positionX(), projectile.positionY(), creep.positionX(), creep.positionY()) < 50){
                    projectileList.remove(projectile)
                    creep.takeDamage(projectile.baseDamage)
                }
            }
        }

        //add enemies to the spawn
        if (Creep.canSpawn()) { //wait for update timer
            val creep = Creep(CreepTypes.LEAFBUG)
            addCreepToMap(creep) //add creeps to concurrentHashMap
            }
        /**
         * update movement, update target or remove enemy
         */
        creepList.forEach{ (creep) ->
            if(creep.positionY().toInt() >= playGround.squareArray[0][squaresY-1].coordY.toInt() || creep.healthPoints <= 0){
                creepList.remove(creep)
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
}