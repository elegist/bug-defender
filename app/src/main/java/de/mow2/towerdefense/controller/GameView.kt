package de.mow2.towerdefense.controller


import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.pathfinding.Astar

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    private var gameLoop: GameLoop
    lateinit var levelGenerator: LevelGenerator
    //background tiles
    private var bgPaint: Paint
    private var bgBitmap: Bitmap
    //build and upgrade menu
    private lateinit var buildMenu: BuildUpgradeMenu

    private lateinit var selectedSquare: SquareField
    private var blockInput = false //flag to block comparing coordinates (when construction menu is open)

    init {
        holder.addCallback(this)
        holder.setFormat(0x00000004)
        gameLoop = GameLoop()

        //initializing background tiles
        bgPaint = Paint()
        bgPaint.style = Paint.Style.FILL
        val bgTileDimension = playGround.squareSize * 2
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

    /**
     * use onDraw to render on the canvas
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //drawing background
        canvas!!.drawPaint(bgPaint)

        ////////////////////
        //object draw area//

        GameManager.drawObjects(canvas, resources)

        //object draw area end//
        ///////////////////////

        //build menu should always draw on top
        if(this::buildMenu.isInitialized && buildMenu.active) {
            GameManager.drawBuildMenu(canvas, buildMenu.x, buildMenu.y + GameActivity.scrollOffset)
        }
        //redraw canvas if canvas has changed
        invalidate()
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
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        var x: Float; var y: Float

        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
                invalidate()
                Log.i("Quadrat angeklickt: ", "${getTouchedSquare(lastX, lastY)}")
            }
            MotionEvent.ACTION_MOVE -> {}

            MotionEvent.ACTION_UP -> {
                x = ev.x
                y = ev.y
                invalidate()

                if(x == lastX && y == lastY) {
                    if(!blockInput && !getTouchedSquare(x, y).isBlocked) {
                        if(getTouchedSquare(x, y).mapPos["y"] in 1 until GameManager.squaresY - 1) {
                            selectedSquare = getTouchedSquare(x, y)
                            //open up build and upgrade menu
                            buildMenu = BuildUpgradeMenu(0f, bottomEnd)
                            buildMenu.active = true
                            blockInput = true
                        }
                    } else { // build and upgrade menu is opened
                        if(x in buildMenu.getRangeX() && y in buildMenu.getRangeY()) {
                            val towerType = buildMenu.getTowerType(x)
                            if(levelGenerator.decreaseCoins(buildMenu.getTowerCost(towerType))) {
                                GameManager.buildTower(selectedSquare, towerType)
                                selectedSquare.isBlocked = true
                            }
                        }
                        blockInput = false
                        buildMenu.active = false
                    }
                }
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }
    private fun getTouchedSquare(x: Float, y: Float): SquareField {
        var xPos = 0
        var yPos = 0
        playGround.squareArray.forEachIndexed {i, it ->
            it.forEachIndexed {j, element ->
                val coordRangeX = element.coordX..(element.coordX+element.width)
                val coordRangeY = element.coordY..(element.coordY+element.height)
                if(x in coordRangeX && y in coordRangeY) {
                    xPos = i
                    yPos = j
                }
            }
        }
        return playGround.squareArray[xPos][yPos]
    }

    companion object {
        var gameWidth = Resources.getSystem().displayMetrics.widthPixels
        var gameHeight = 2 * gameWidth
        val playGround = PlayGround(gameWidth)
        var bottomEnd = 0f
        //path finding algorithm
        var astar = Astar()
    }
}