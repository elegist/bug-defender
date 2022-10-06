package de.mow2.towerdefense.controller

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    var gameLoop: GameLoop

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

        gameLoop.startLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    fun update() {

    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }
}