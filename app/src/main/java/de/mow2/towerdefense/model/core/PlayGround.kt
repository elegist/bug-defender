package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.controller.GameManager

class PlayGround(val width: Int, val height: Int) {
    var squareArray = emptyArray<SquareField>()
    private val squaresX = GameManager.squaresX
    private val squaresY = GameManager.squaresY
    val squareSize = width / squaresX
    init {
        var posY = 0
        var posX: Int
        for(i in 0..squaresY) {
            posX = 0
            for(j in 0..squaresX) {
                val mapPos = mapOf("x" to j, "y" to i)
                squareArray = squareArray.plus(SquareField(posX.toFloat(), posY.toFloat(), squareSize, squareSize, mapPos))
                posX += squareSize
            }
            posY += squareSize
        }
    }
}