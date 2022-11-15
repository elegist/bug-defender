package de.mow2.towerdefense.controller


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.helper.BitmapPreloader
import de.mow2.towerdefense.model.core.GUICallBack
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.helper.Vector2D

@SuppressLint("ViewConstructor")
class GameView(context: Context, private val callBack: GUICallBack, val gameManager: GameManager) : SurfaceView(context), SurfaceHolder.Callback {
    private var gameLoop: GameLoop
    //background tiles
    private var bgPaint: Paint
    private var bgBitmap: Bitmap

    init {
        holder.addCallback(this)
        holder.setFormat(0x00000004)
        gameLoop = GameLoop(gameManager)

        //initializing background tiles
        bgPaint = Paint()
        bgPaint.style = Paint.Style.FILL
        val bgTileDimension = GameManager.playGround.squareSize * 2
        bgBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.green_chess_bg), bgTileDimension, bgTileDimension, false)
        bgPaint.shader = BitmapShader(bgBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setWillNotDraw(false)

        //start game loop
        gameLoop.setRunning(true)
        gameLoop.start()
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
        //towers
        GameManager.towerList.forEach { tower ->
            draw(canvas, BitmapPreloader.towerImages[tower.type], tower.position)
            if(tower.target != null) {
                draw(canvas, BitmapPreloader.weaponAnims[tower.type]!!.nextFrame(0), tower.position)
            } else {
                draw(canvas, BitmapPreloader.weaponAnims[tower.type]!!.idleImage, tower.position)
            }
        }
        //enemies
        GameManager.enemyList.forEach { enemy ->
            draw(canvas, BitmapPreloader.enemyAnims[enemy.type]!!.nextFrame(enemy.orientation), enemy.position)
        }
        //projectiles
        GameManager.projectileList.forEach { projectile ->
            draw(canvas, BitmapPreloader.projectileAnims[projectile.tower.type]!!.nextFrame(0), projectile.position)
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


    private var lastX = 0f
    private var lastY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val x: Float; val y: Float

        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {}

            MotionEvent.ACTION_UP -> {
                x = ev.x
                y = ev.y
                val selectedField = getTouchedSquare(x, y)
                invalidate()
                if(x == lastX && y == lastY) {
                    if(selectedField.mapPos["y"] in 1 until GameManager.squaresY - 1) {
                        //open up build and upgrade menu for selected square
                        callBack.toggleBuildMenu(selectedField)
                    }
                }
            }
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