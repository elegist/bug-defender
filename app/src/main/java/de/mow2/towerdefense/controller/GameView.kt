package de.mow2.towerdefense.controller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.jar.Attributes

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    var gameLoop: GameLoop

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

        gameLoop.startLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    fun update() {
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.drawRect(100f, 100f, 100f, 100f, Paint().color )
        }
    }
}