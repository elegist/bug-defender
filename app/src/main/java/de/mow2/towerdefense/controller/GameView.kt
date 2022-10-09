package de.mow2.towerdefense.controller


import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.model.core.SquareField

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    private var gameLoop: GameLoop
    private var playGround: PlayGround

    private lateinit var selectedSquare: SquareField
    private var blockInput = false //flag to block comparing coordinates (when construction menu is open)

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
        playGround = PlayGround(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels, resources)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
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
     * method to update game objects data
     * should only call GameManager and similar classes update methods
     */
    fun update() {
    }

    /**
     * method to draw on canvas
     * should only call GameManager and similar classes draw methods
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        //drawing playground
        playGround.squareArray.forEach { it.drawField(canvas) }
        GameManager.drawObjects(canvas, resources)
    }

    /**
     * handling user inputs
     */
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        var x: Float; var y: Float

        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                x = ev.x
                y = ev.y
                invalidate()

                //if selected square is not start or finish line, and not blocked by tower TODO: open build & upgrade menu
                if(getSquareAt(x, y).mapPos["y"] in 1 until GameManager.squaresY - 1 && !getSquareAt(x, y).isBlocked) {
                    blockInput = if(!blockInput) {
                        selectedSquare = getSquareAt(x, y)

                        //lock square
                        selectedSquare.selectSquare()
                        // TODO: öffne baumenü an gegebenen koordinaten

                        true
                    } else {
                        //if user clicks on already selected square: build tower, else: free square
                        if(selectedSquare == getSquareAt(x, y)) {
                            GameManager.buildTower(selectedSquare)
                            selectedSquare.isBlocked = true
                        } else {
                            selectedSquare.clearSquare()
                        }

                        // TODO: Prüfe ob Spieler auf Baumenü geklickt hat, wenn ja: Baue Turm, wenn nein: schließe Baumenü
                        false
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {}

            MotionEvent.ACTION_UP -> {
                x = ev.x
                y = ev.y
                invalidate()
                // TODO: Prüfe ob "Loslassen" gleiche Koordinaten hat wie drücken, wenn Nein: nichts tun
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
}