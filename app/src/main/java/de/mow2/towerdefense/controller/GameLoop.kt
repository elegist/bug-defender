package de.mow2.towerdefense.controller

import android.view.SurfaceHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameLoop(val gameView: GameView, val surfaceHolder: SurfaceHolder) {
    fun startLoop() {
        runBlocking {
            launch {

            }
        }
    }
}