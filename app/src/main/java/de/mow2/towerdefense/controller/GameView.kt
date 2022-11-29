package de.mow2.towerdefense.controller


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.helper.BitmapPreloader
import de.mow2.towerdefense.model.core.*
import de.mow2.towerdefense.model.helper.Vector2D

@SuppressLint("ViewConstructor")
class GameView(context: Context, private val callBack: GameActivity, val gameManager: GameManager) : SurfaceView(context), SurfaceHolder.Callback {
    private var gameLoop: GameLoop
    //background tiles
    private var bgPaint: Paint
    private val buildMenu = BuildUpgradeMenu(gameManager, callBack)

    init {
        holder.addCallback(this)
        holder.setFormat(0x00000004)
        gameLoop = GameLoop(gameManager)

        //initializing background tiles
        bgPaint = Paint()
        bgPaint.style = Paint.Style.FILL
        bgPaint.shader = BitmapShader(BitmapPreloader.playgroundBG, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }

    fun toggleGameLoop(setRunning: Boolean) {
        if(!setRunning) {
            gameLoop.setRunning(false)
            gameLoop.join()
        } else {
            gameLoop = GameLoop(gameManager)
            gameLoop.setRunning(true)
            gameLoop.start()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setWillNotDraw(false)
        //start game loop
        Log.i("TutActive:", "${GameManager.tutorialsActive}")
        if(!GameManager.tutorialsActive) {
            toggleGameLoop(true)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                gameLoop.setRunning(false)
                gameLoop.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            retry = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //drawing background
        canvas.drawPaint(bgPaint)

        ////////////////////
        //object draw area//

        drawObjects(canvas)

        //object draw area end//
        ///////////////////////

        //redraw canvas if canvas has changed
        invalidate()
    }

    /**
     * Iterates through game objects and calls draw method
     *
     * ! Use iterators for lists, or use ConcurrentHashMaps to avoid ConcurrentModificationException !
     */
    private fun drawObjects(canvas: Canvas) {
        //enemies
        GameManager.enemyList.forEach { enemy ->
            draw(canvas, BitmapPreloader.enemyAnims[enemy.type]!!.nextFrame(enemy.orientation), enemy.position)
        }
        //towers
        GameManager.towerList.forEach { tower ->
            draw(canvas, BitmapPreloader.towerImagesArray[tower.towerLevel][tower.type], tower.position)
            if(GameManager.selectedTool == R.id.upgradeButton && tower.towerLevel < GameManager.maxTowerLevel && buildMenu.getTowerCost(tower.type, tower.towerLevel + 1) <= GameManager.coinAmnt) {
                draw(canvas, BitmapPreloader.upgradeOverlay, tower.position)
            }
            if(tower.isShooting && GameManager.waveActive) {
                draw(canvas, BitmapPreloader.weaponAnimsArray[tower.towerLevel][tower.type]!!.nextFrame(tower.orientation), Vector2D(tower.position.x, tower.position.y + tower.weaponOffset))
            } else {
                draw(canvas, BitmapPreloader.weaponAnimsArray[tower.towerLevel][tower.type]!!.idleImage, Vector2D(tower.position.x, tower.position.y + tower.weaponOffset))
            }
        }
        //projectiles
        GameManager.projectileList.forEach { projectile ->
            draw(canvas, BitmapPreloader.projectileAnimsArray[projectile.tower.towerLevel][projectile.tower.type]!!.nextFrame(projectile.orientation), projectile.position)
        }
    }
    /**
     * draws a bitmap onto canvas
     */
    private fun draw(canvas: Canvas, bitmap: Bitmap?, position: Vector2D) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, position.x, position.y, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = gameHeight
        val width = gameWidth

        setMeasuredDimension(width, height)
    }

    /**
     * handling user inputs
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val x: Float; val y: Float

        if (ev?.action == MotionEvent.ACTION_UP){
            x = ev.x
            y = ev.y
            val selectedField = getTouchedSquare(x, y)
            if(selectedField.mapPos["y"] in 1 until GameManager.squaresY - 1) {
                Log.i("Tool", "${GameManager.selectedTool}")

                when (GameManager.selectedTool) {
                    R.id.deleteButton -> {
                        if (selectedField.tower != null){
                            buildMenu.destroyTower(selectedField.tower!!)
                        }
                    }
                    R.id.buildButton -> {
                        buildMenu.buildTower(selectedField, GameManager.selectedTower)
                    }
                    R.id.upgradeButton -> {
                        buildMenu.upgradeTower(selectedField.tower)
                    }
                }
            }
            invalidate()
        }
        return true
    }

    /**
     * Takes x and y position of a touch event and returns the specific SquareField to work with
     * @param x The horizontal position on screen in pixels
     * @param y The vertical position on screen in pixels
     */
    private fun getTouchedSquare(x: Float, y: Float): SquareField {
        var xPos = 0
        var yPos = 0
        GameManager.playGround.squareArray.forEachIndexed { i, it ->
            it.forEachIndexed {j, element ->
                val coordRangeX = element.position.x..(element.position.x+element.width)
                val coordRangeY = element.position.y..(element.position.y+element.height)
                if(x in coordRangeX && y in coordRangeY) {
                    xPos = i
                    yPos = j
                }
            }
        }
        return GameManager.playGround.squareArray[xPos][yPos]
    }

    companion object {
        var gameWidth = Resources.getSystem().displayMetrics.widthPixels
        var gameHeight = 2 * gameWidth
    }
}