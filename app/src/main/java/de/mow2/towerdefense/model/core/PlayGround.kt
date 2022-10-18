package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.controller.GameManager

class PlayGround(val width: Int, val height: Int) {
    var squareArray = emptyArray<Array<SquareField>>()
    private val squaresX = GameManager.squaresX
    private val squaresY = GameManager.squaresY
    val squareSize = width / squaresX
    init {
        var posX = 0
        var posY: Int
        for(i in 0..squaresX) {
            var cols = emptyArray<SquareField>()
            posY = 0
            for(j in 0..squaresY) {
                val mapPos = mapOf("x" to i, "y" to j)
                cols = cols.plus(SquareField(posX.toFloat(), posY.toFloat(), squareSize, squareSize, mapPos))
                posY += squareSize
            }
            posX += squareSize
            squareArray = squareArray.plus(cols)
        }
    }
}