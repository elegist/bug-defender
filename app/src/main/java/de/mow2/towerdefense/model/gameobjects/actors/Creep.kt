package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.SquareField

/**
 * for later use
 */

class Creep(var squareField: SquareField, var type: CreepTypes) {
    var x: Float = squareField.coordX
    var y: Float = squareField.coordY
    var w: Int = squareField.width
    var h: Int = (2*w)

    init {

    }




}