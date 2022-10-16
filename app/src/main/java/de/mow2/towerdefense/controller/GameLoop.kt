package de.mow2.towerdefense.controller

import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import androidx.core.view.doOnPreDraw


class GameLoop(private val gameView: GameView, private val surfaceHolder: SurfaceHolder) :
    Thread() {
    private var running = false
    private var avgUps: Double = 0.0
    private var avgFps: Double = 0.0


    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }

    override fun run() {
        var startTime: Long = System.currentTimeMillis()
        var elapsedTime: Long
        var waitTime: Long
        var updateCount = 0
        var frameCount = 0
        var canvas: Canvas? = null
        /* Game Loop */
        while (running) {
            try{
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.doOnPreDraw { GameManager.updateLogic() }
                    updateCount++
                    gameView.invalidate()
                }
            }catch(e : Exception){
                e.printStackTrace()
            }finally {
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas)
                        frameCount++
                    }catch(e: Exception){
                        e.printStackTrace()
                    }
                }
            }
            //pause gameLoop if targetUPS is exceeded
            elapsedTime = System.currentTimeMillis() - startTime
            waitTime = ((updateCount * targetTime) - elapsedTime).toLong()
            if (waitTime > 0) {
                try {
                    sleep(waitTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            //
            while (waitTime < 0 && updateCount < targetUPS - 1) {
                //Log.i("Skipped Frame:", updateCount.toString())
                gameView.doOnPreDraw { GameManager.updateLogic() }
                updateCount++
                elapsedTime = System.currentTimeMillis() - startTime
                waitTime = ((updateCount * targetTime) - elapsedTime).toLong()
            }

            //calc avg Ups and Fps
            elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime >= 1000) {
                avgUps = (updateCount) / (1E-3 * elapsedTime)
                avgFps = (frameCount) / (1E-3 * elapsedTime)
                updateCount = 0
                frameCount = 0
                startTime = System.currentTimeMillis()
                //Log.i("UPSandFPS", "UPS:${avgUps} FPS:${avgFps}")
            }
        }
    }

    companion object {
        const val targetUPS = 30
        const val targetTime: Double = 1E+3 / targetUPS
    }
}