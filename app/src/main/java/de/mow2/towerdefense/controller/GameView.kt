package de.mow2.towerdefense.controller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception
import java.util.jar.Attributes

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    var gameLoop: GameLoop

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
    }

    /**
     * Use surfaceCreated to initialize game objects, field and so on...
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
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
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        //testing stuff
        if (canvas != null) {
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.FILL
            canvas.drawRect(0f, 0f, 100f, 100f, paint)
        }
    }
}