package de.mow2.towerdefense.controller

import android.graphics.Canvas
import android.view.SurfaceHolder
import java.lang.Exception

class GameLoop(private val gameView: GameView, private val surfaceHolder: SurfaceHolder) : Thread() {
    private var running = false

    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }
    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        val targetTime = (1000 / targetUPS).toLong()

        /* Game Loop */
        while (running) {
            startTime = System.nanoTime()
            canvas = null
            try {
                startTime = System.nanoTime()
                //locking canvas to draw onto
                canvas = this.surfaceHolder.lockCanvas()
                //synchronize threads, so this is the only one to draw onto canvas
                synchronized(surfaceHolder) {
                    //updating gameview
                    gameView.update()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if(canvas != null) {
                    try {
                        //draw canvas and post
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }

            //calculate elapsed time, then wait
            timeMillis = (System.nanoTime() - startTime) / 1_000_000
            waitTime = targetTime - timeMillis

            if(waitTime >= 0) {
                try {
                    sleep(waitTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    companion object {
        private var canvas: Canvas? = null
        const val targetUPS = 60
    }
}