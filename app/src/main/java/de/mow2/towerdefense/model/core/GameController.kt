package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.controller.helper.GameState

interface GameController {
    var gameManager: GameManager
    var gameLoop: GameLoop
    var gameHeight: Int
    var gameWidth: Int
    var playGround: PlayGround

    var gameState: GameState
    fun updateGUI()
    fun updateHealthBarMax(newMax: Int)
    fun updateProgressBarMax(newMax: Int)
    fun onGameOver()
    fun showToastMessage(type: String)

    fun initGameLoop()
    fun toggleGameLoop(setRunning: Boolean)
}