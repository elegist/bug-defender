package de.mow2.towerdefense.model.actors

import de.mow2.towerdefense.model.playground.SquareField

class Tower(var squareField: SquareField, var type: TowerTypes) {
    var x: Float = squareField.coordX
    var y: Float
    var w: Int = squareField.width
    var h: Int = (2*w)

    init {
        y = squareField.coordY - w
    }
}