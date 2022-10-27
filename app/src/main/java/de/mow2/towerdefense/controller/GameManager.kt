package de.mow2.towerdefense.controller

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.SquareField
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

    //currently as array, should be a matrix (map or list)
    var towerList = emptyArray<Tower>()
    var creepList: ConcurrentHashMap<Creep, Astar.Node> = ConcurrentHashMap()
    lateinit var spriteSheet: SpriteSheet

    //nodes test
    var compoundPath: MutableList<SquareField> = mutableListOf()
    private var target: Astar.Node = Astar.Node(5, 5)

    //debug
    private val TAG = javaClass.name

    fun comparePathCoords(path: List<Astar.Node>) {
        compoundPath.clear()
        path.forEach {
            compoundPath.add(GameView.playGround.squareArray[it.x][it.y])
        }
    }

    /**
     * decides which objects to draw
     */
    fun drawObjects(canvas: Canvas, resources: Resources) {
        var sprite = SpriteSheet(resources, BitmapFactory.decodeResource(resources, R.drawable.leafbug)).cutSprite()
        //draw towers
        towerList.forEach {
            draw(canvas, getTowerBitmap(it, resources), it.x, it.y)
        }
        creepList.forEach{ (enemy) ->
            draw(canvas, resizeImage(BitmapFactory.decodeResource(resources, R.drawable.leafbug_down), enemy.w, enemy.h), enemy.positionX(), enemy.positionY())
        }
    }

    /**
     * updates to game logic related values
     */
    fun updateLogic() {
        //add enemies to the spawn
        if (Creep.canSpawn()) { //wait for update timer
            val creep = Creep(CreepTypes.LEAFBUG)
            val posX = creep.squareField.mapPos["x"]!!
            val posY = creep.squareField.mapPos["y"]!!
            val creepNode = Astar.Node(posX, posY)
            val targetNode = Astar.Node(posX, 17)
            val alg = Astar()
            val path = alg.findPath(creepNode, targetNode, squaresX, squaresY)
            if(path != null) {
                val sortedPath = path.reversed()
                creep.path = sortedPath
                //add creeps and their individual target to concurrentHashMap
                creepList[creep] = target
            }
        }


        /**
         * update movement, update target or remove enemy
         */
        creepList.forEach{ (enemy) ->
            if(enemy.positionY().toInt() >= GameView.playGround.squareArray[0][squaresY-1].coordY.toInt()){
                creepList.remove(enemy)
                //Log.i("enemyUpdater", "enemy removed")
            }else{
                //update movement
                enemy.update()
            }
            //TODO: update enemy target
        }
    }

    /**
     * actually draws objects
     */
    @Synchronized private fun draw(canvas: Canvas, bitmap: Bitmap, posX: Float, posY: Float) {
        canvas.drawBitmap(bitmap, posX, posY, null)
    }

    /**
     * placeholder for the time being.
     * could be expanded to perform various action such as change color, alpha etc.
     */
    private fun resizeImage(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    /**
     * takes a tower and return its specific, already scaled image
     * @param tower a specific existing tower
     * @param resources reference to android resources
     */
    private fun getTowerBitmap(tower: Tower, resources: Resources): Bitmap {
        val image = when (tower.type) {
            TowerTypes.BLOCK -> {
                when(tower.level) {
                    1 -> resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_block1), tower.w, tower.h)
                    else -> resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_block), tower.w, tower.h)
                }
            }
            TowerTypes.SLOW -> {
                when(tower.level) {
                    1 -> resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_slow1), tower.w, tower.h)
                    else -> resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_slow), tower.w, tower.h)
                }
            }
            TowerTypes.AOE -> {
                when(tower.level) {
                    1 -> resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_aoe1), tower.w, tower.h)
                    else -> resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_aoe), tower.w, tower.h)
                }
            }
        }
        return image
    }
}