package de.mow2.towerdefense.controller

import android.content.res.Resources
import android.graphics.*
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.gameobjects.Enemy
import de.mow2.towerdefense.model.gameobjects.Target
import de.mow2.towerdefense.model.gameobjects.actors.Tower
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.pathfinding.Astar
import java.util.concurrent.ConcurrentHashMap


object GameManager {
    //playground variables
    const val squaresX = 9
    const val squaresY = 18
    //game variables
    var lives: Int = 3
    var coins: Int = 100

    //currently as array, should be a matrix (map or list)
    private var towerList = emptyArray<Tower>()
    private var creepList: ConcurrentHashMap<Enemy, Target> = ConcurrentHashMap()
    lateinit var sprite: Sprite
    lateinit var spriteSheet: SpriteSheet

    //nodes test
    lateinit var path: MutableSet<Astar.Node>
    var compoundPath: MutableList<SquareField> = mutableListOf()
    private var target: Target = Target(GameView.gameWidth/2.toFloat(), GameView.gameHeight.toFloat())
    //build and upgrade menu
    var buildMenuButtons = emptyArray<Bitmap>()
    var buildMenuButtonRanges = emptyArray<ClosedFloatingPointRange<Float>>()
    private val TAG = javaClass.name

    init {
        //TODO: get actual lives and coins
    }

    fun comparePathCoords() {
        GameView.playGround.squareArray.forEach { square ->
            path.forEach { node ->
                if (square.mapPos["x"] == node.x && square.mapPos["y"] == node.y) {
                    compoundPath.add(square)
                }
            }
        }
    }

    fun buildTower(selectedField: SquareField, towerType: TowerTypes) {
        val tower = when(towerType) {
            TowerTypes.BLOCK -> {
                Tower(selectedField, TowerTypes.BLOCK)
            }
            TowerTypes.SLOW -> {
                Tower(selectedField, TowerTypes.SLOW)
            }
            TowerTypes.AOE -> {
                Tower(selectedField, TowerTypes.AOE)
            }
        }
        towerList = towerList.plus(tower)
        towerList.sort() //sorting array to avoid overlapped drawing
    }

    fun initBuildMenu(resources: Resources) {
        val dimensionX = 100
        val dimensionY = 200
        var drawable: Int
        enumValues<TowerTypes>().forEach {
            drawable = when(it) {
                TowerTypes.BLOCK -> {
                    R.drawable.tower_block
                }
                TowerTypes.SLOW -> {
                    R.drawable.tower_slow
                }
                TowerTypes.AOE -> {
                    R.drawable.tower_aoe
                }
            }
            buildMenuButtons = buildMenuButtons.plus(resizeImage(BitmapFactory.decodeResource(resources, drawable), dimensionX, dimensionY))
        }
    }

    fun drawBuildMenu(canvas: Canvas, x: Float, y: Float) {
        buildMenuButtonRanges = emptyArray()
        var offsetX = 0
        var offsetY = -GameView.bottomGuiHeight * 2
        buildMenuButtons.forEach {
            draw(canvas, it, x + offsetX, y + offsetY)
            val range = ((x+offsetX)..(x+offsetX+it.width))
            buildMenuButtonRanges = buildMenuButtonRanges.plus(range)
            offsetX += 120
        }
    }

    /**
     * decides which objects to draw
     */
    fun drawObjects(canvas: Canvas, resources: Resources) {
        //draw towers
        towerList.forEach {
            when (it.type) {
                TowerTypes.BLOCK -> {
                    draw(canvas, resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_block), it.w, it.h), it.x, it.y)
                }
                TowerTypes.SLOW -> {
                    draw(canvas, resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_slow), it.w, it.h), it.x, it.y)
                }
                TowerTypes.AOE -> {
                    draw(canvas, resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_aoe), it.w, it.h), it.x, it.y)
                }
            }
        }
        //draw creeps
        creepList.forEach { (enemy) ->
            draw(canvas, BitmapFactory.decodeResource(resources, R.drawable.leafbug_down1), enemy.getPositionX(), enemy.getPositionY())
        }
/*        //for testing purposes
        compoundPath.forEach {
            draw(canvas, resizeImage(BitmapFactory.decodeResource(resources, R.drawable.tower_block), 50, 50), it.coordX, it.coordY)
        }*/
    }

    /**
     * updates to game logic related values
     */
    fun updateLogic() {
        //add enemies to the spawn
        if (Enemy.canSpawn()) { //wait for update timer
            //add creeps and their individual target to concurrentHashMap
            creepList[Enemy(target)] = target //creepList.put(Enemy(target), target)

            //Log.i(TAG, "${creepList.size} enemies spawned")
        }

        /**
         * update movement, update target or remove enemy
         */
        creepList.forEach{ (enemy) ->
            if(enemy.getPositionY().toInt() >= GameView.gameHeight){
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
}