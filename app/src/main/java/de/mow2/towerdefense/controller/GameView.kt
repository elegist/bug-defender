package de.mow2.towerdefense.controller

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.model.playground.PlayGround

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    private var gameLoop: GameLoop
    private var playGround: PlayGround

    //flag to block comparing coordinates (when construction menu is open)
    private var blockInput = false
    private var indexOfSelected: Int = 0

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
        playGround = PlayGround(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels, resources)
    }

    /**
     * Use surfaceCreated to initialize game objects, field and so on...
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        //start game loop
        gameLoop.setRunning(true)
        gameLoop.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

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
     */
    fun update() {
    }

    /**
     * method to draw on canvas
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        //drawing playground
        playGround.squareArray.forEach { it.drawField(canvas) }
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
                if(!blockInput) {
                    compareCoords(x, y)
                    // öffne baumenü an gegebenen koordinaten
                } else {
                    playGround.squareArray[indexOfSelected].unlockSquare()
                    blockInput = false
                    // schließe baumenü bzw. baue turm oder was auch immer
                }
            }
            MotionEvent.ACTION_MOVE -> {}

            MotionEvent.ACTION_UP -> {
                x = ev.x
                y = ev.y
                invalidate()
            }
        }
        return true
    }

    private fun compareCoords(x: Float, y: Float) {
        playGround.squareArray.forEachIndexed { i, it ->
            val coordRangeX = it.coordX..(it.coordX+it.width)
            val coordRangeY = it.coordY..(it.coordY+it.height)
            if(x in coordRangeX && y in coordRangeY) {
                it.lockSquare()
                blockInput = true
                indexOfSelected = i
            }
        }
    }
}