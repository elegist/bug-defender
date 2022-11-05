package de.mow2.towerdefense.controller

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.gameobjects.actors.*
import de.mow2.towerdefense.model.pathfinding.Astar
import java.util.concurrent.ConcurrentHashMap

/**
 * GameManager holds static access to game variables like bitmaps, ingame values and such
 * it also manages drawing onto canvas
 */
object GameManager {
    //playground variables
    const val squaresX = 9
    const val squaresY = 18
    var playGround = PlayGround(GameView.gameWidth)
    lateinit var resources: Resources

    //currently as array, should be a matrix (map or list)
    var projectileList: ConcurrentHashMap<Projectile, Tower> = ConcurrentHashMap()
    var towerList = ConcurrentHashMap<Tower, Bitmap?>()
    private var creepList = ConcurrentHashMap<Creep, Bitmap?>()
    lateinit var spriteSheet: SpriteSheet

    //debug
    private val TAG = javaClass.name

    /**
     * call to hard-reset GameManager (remove any remaining towers, creeps, etc.) e.g. Leaving a Game, starting a new one
     */
    fun resetManager() {
        playGround = PlayGround(GameView.gameWidth)
        towerList = ConcurrentHashMap()
        creepList = ConcurrentHashMap()
        projectileList = ConcurrentHashMap()
    }

    /**
     * Adds a tower and its customized bitmap to the drawing list
     * @param tower the tower to be added
     */
    fun addTowerToMap(tower: Tower) {
        towerList[tower] = ScaledImage(resources, tower.w, tower.h, tower.type).getImage()
        towerList.toSortedMap()
    }
    // TODO: create one map out of all things to draw and sort it to get a good drawing order?
    /**
     * Adds a creep and its customized bitmap to the drawing list
     * @param creep the creep to be added
     */
    private fun addCreepToMap(creep: Creep) {
        //TODO: maybe sort map for drawing order? Also: CreepTypes
        creepList[creep] = ScaledImage(resources, creep.w, creep.h, null, CreepTypes.LEAFBUG).getImage()
    }

    /**
     * decides which objects to draw
     */
    fun drawObjects(canvas: Canvas, resources: Resources) {
        var sprite = SpriteSheet(resources, BitmapFactory.decodeResource(resources, R.drawable.leafbug)).cutSprite()
        //draw towers
        towerList.forEach { (tower, image) ->
            draw(canvas, image, tower.x, tower.y)
        }
        creepList.forEach{ (enemy, image) ->
            draw(canvas, image, enemy.positionX(), enemy.positionY())
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
            towerList.forEach { (tower) ->
                creepList.forEach{ (creep) ->
                    if (GameObject.findDistance(creep.positionX(), creep.positionY(), tower.x, tower.y) < tower.baseRange){
                        projectileList[Projectile(tower.squareField, tower, creep)] = tower
                    }
                }
            }
        }

        projectileList.forEach { (projectile) ->
            creepList.forEach{ (creep) ->
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
                //Log.i("enemyUpdater", "enemy removed")
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