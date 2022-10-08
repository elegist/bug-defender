package de.mow2.towerdefense.model.playground

import android.content.res.Resources
import android.graphics.BitmapFactory
import de.mow2.towerdefense.R

class PlayGround(val width: Int, val height: Int, resources: Resources) {
    val TAG = javaClass.name
    var squareArray = emptyArray<SquareField>()
    private val squaresX = 10
    private val squaresY = 20
    init {
        val squareWidth = width / squaresX
        val squareHeight = height / squaresY
        var posX = 0
        var posY: Int
        var squareId = 0
        for(i in 0..squaresX) {
            posY = 0
            for(j in 0..squaresY) {
                squareArray =
                    if((i + j) % 2 == 0) {
                        squareArray.plus(SquareField(posX.toFloat(), posY.toFloat(), squareWidth, squareHeight, BitmapFactory.decodeResource(resources, R.drawable.square_green)))
                    } else {
                        squareArray.plus(SquareField(posX.toFloat(), posY.toFloat(), squareWidth, squareHeight, BitmapFactory.decodeResource(resources, R.drawable.square_lightgreen)))
                    }
                posY += squareHeight
                squareId++
            }
            posX += squareWidth
        }
    }
}