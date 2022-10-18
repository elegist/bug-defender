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
    //background tiles
    private var bgPaint: Paint
    private var bgBitmap: Bitmap
    //build and upgrade menu
    private lateinit var buildMenu: BuildUpgradeMenu

    private lateinit var selectedSquare: SquareField
    private var blockInput = false //flag to block comparing coordinates (when construction menu is open)

    //testing of the astar
    val startNode = Astar.Node(0,0)
    val endNode = Astar.Node(GameManager.squaresX - 1, GameManager.squaresY - 1)

    init {
        holder.addCallback(this)
        gameLoop = GameLoop()

        //initializing background tiles
        bgPaint = Paint()
        bgPaint.style = Paint.Style.FILL
        val bgTileDimension = playGround.squareSize * 2
        bgBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.green_chess_bg), bgTileDimension, bgTileDimension, false)
        bgPaint.shader = BitmapShader(bgBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

//        playGround.squareArray[0][1].isBlocked = true
//        playGround.squareArray[1][1].isBlocked = true
//        playGround.squareArray[2][1].isBlocked = true
//        playGround.squareArray[3][1].isBlocked = true
//
//        GameManager.path = astar.findPath(Astar.Node(0, 0), Astar.Node(4, 4), 9, 18)!!
//        GameManager.comparePathCoords()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setWillNotDraw(false)

        //start game loop
        gameLoop.setRunning(true)
        gameLoop.start()

        Log.i("SquareArray:", "${playGround.squareArray.size}, ${playGround.squareArray[0].size}")
        Log.i("Field:", "${playGround.squareArray[8][17].mapPos["x"]}, ${playGround.squareArray[8][17].mapPos["y"]}")
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
            GameManager.drawBuildMenu(canvas, buildMenu.x, buildMenu.y)
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
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        var x: Float; var y: Float

        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
                invalidate()
                Log.i("Get Range", "user input x: $lastX , y: $lastY")
                Log.i("Array Pos 2 :", "x : ${playGround.squareArray[3][5].isBlocked} y : ${playGround.squareArray[3][5].mapPos["y"]}")
                Log.i("Array Pos:", "x: ${getTouchedSquare(lastX, lastY).mapPos["x"]} y: ${getTouchedSquare(lastX, lastY).mapPos["y"]}")
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
                            selectedSquare.selectSquare()
                            //open up build and upgrade menu
                            Log.i("Location BottomGUI", bottomEnd.toString())
                            buildMenu = BuildUpgradeMenu(0f, bottomEnd)
                            buildMenu.active = true
                            blockInput = true
                        }
                    } else { // build and upgrade menu is opened
                        if(x in buildMenu.getRangeX() && y in buildMenu.getRangeY()) {
                            val towerType = buildMenu.getTowerType(x)
                            GameManager.buildTower(selectedSquare, towerType)
                            selectedSquare.isBlocked = true

                            Log.i("ArrayInfo", "START: $startNode, END: $endNode, WIDTH: ${GameManager.squaresX}, HEIGHT: ${GameManager.squaresY}")

                            GameManager.path = astar.findPath(startNode, endNode, GameManager.squaresX, GameManager.squaresY )!!
                            GameManager.comparePathCoords()
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

    private var yScrollOffset: Float = 0f
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //TODO: when build upgrade menu is active, change its position according to users scroll behaviour
        Log.i("User Scrolling: ", "Old: $oldl $oldt - New: $l $t")
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
        val playGround = PlayGround(gameWidth, gameHeight)
        var bottomEnd = 0f
        //path finding algorithm
        var astar = Astar()
    }
}