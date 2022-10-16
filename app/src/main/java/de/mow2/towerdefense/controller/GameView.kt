package de.mow2.towerdefense.controller

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.pathfinding.Astar

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    private var gameLoop: GameLoop
    //background tiles
    private var bgPaint: Paint
    private var bgBitmap: Bitmap
    //build and upgrade menu
    private lateinit var buildMenu: BuildUpgradeMenu

    private lateinit var selectedSquare: SquareField
    private var blockInput = false //flag to block comparing coordinates (when construction menu is open)

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)

        //initializing background tiles
        bgPaint = Paint()
        bgPaint.style = Paint.Style.FILL
        val bgTileDimension = playGround.squareSize * 2
        bgBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.green_chess_bg), bgTileDimension, bgTileDimension, false)
        bgPaint.shader = BitmapShader(bgBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        GameManager.path = astar.findPath(Astar.Node(0, 0), Astar.Node(15, 15), 16, 16)!!
        GameManager.comparePathCoords()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setWillNotDraw(false)
        // create test creep
        GameManager.createCreep(playGround.squareArray[0])
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

    /**
     * method to update game objects data
     * should only call GameManager and similar classes update methods
     */
    fun update() {
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //drawing background
        canvas!!.drawPaint(bgPaint)
        //drawing objects
        GameManager.drawObjects(canvas, resources)

        if(this::buildMenu.isInitialized && buildMenu.active) {
            GameManager.drawBuildMenu(canvas, resources, buildMenu.x, buildMenu.y, buildMenu.menuPosition)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = gameHeight
        val width = gameWidth

        setMeasuredDimension(width, height)
    }

    /**
     * method to draw on canvas
     * should only call GameManager and similar classes draw methods
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }

    /**
     * handling user inputs
     */
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        var x: Float; var y: Float

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
                invalidate()

                if(x == lastX && y == lastY) {
                    if(!blockInput && !getSquareAt(x, y).isBlocked) {
                        if(getSquareAt(x, y).mapPos["y"] in 1 until GameManager.squaresY - 1) {
                            selectedSquare = getSquareAt(x, y)
                            selectedSquare.selectSquare()
                            //open up build and upgrade menu
                            //detect distance to screen edge
                            var menuDirection = if(x < gameWidth / 2) {
                                if(y < gameHeight / 2)
                                { "rightBottom" }
                                else { "rightTop" }
                            } else {
                                if(y < gameHeight / 2) { "leftBottom" }
                                else { "leftTop" }
                            }

                            buildMenu = BuildUpgradeMenu(ev.x, ev.y, menuDirection)
                            buildMenu.active = true
                            blockInput = true
                        }
                    } else { // build and upgrade menu is opened
                        if(x in buildMenu.getRangeX() && y in buildMenu.getRangeY()) {
                            GameManager.buildTower(selectedSquare)
                            selectedSquare.isBlocked = true
                        } else {
                            selectedSquare.clearSquare()
                        }
                        blockInput = false
                        buildMenu.active = false
                    }
                }
            }
        }
        return true
    }

    private fun getSquareAt(x: Float, y: Float): SquareField {
        var indexOfSelected = 0
        playGround.squareArray.forEachIndexed { i, it ->
            val coordRangeX = it.coordX..(it.coordX+it.width)
            val coordRangeY = it.coordY..(it.coordY+it.height)
            if(x in coordRangeX && y in coordRangeY) {
                indexOfSelected = i
            }
        }
        return playGround.squareArray[indexOfSelected]
    }

    companion object {
        var gameWidth = Resources.getSystem().displayMetrics.widthPixels
        var gameHeight = 2 * gameWidth
        val playGround = PlayGround(gameWidth, gameHeight)
        //path finding algorithm
        var astar = Astar()
    }
}