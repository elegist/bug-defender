package de.mow2.towerdefense.controller

import android.graphics.Canvas
import android.view.SurfaceHolder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class GameLoop(val gameView: GameView, val surfaceHolder: SurfaceHolder) {
    private var running = false

    fun startLoop() {
        runBlocking {
            launch {
                while(running) {
                    try {
                        canvas = surfaceHolder.lockCanvas()
                        gameView.update()
                        gameView.draw(canvas)
                    } catch (e: java.lang.Exception) {
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
                }
            }
        }
    }

    companion object {
        private var canvas: Canvas? = null
    }
}