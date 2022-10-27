package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.SquareField

/**
 *
 */
class Tower(val squareField: SquareField, var type: TowerTypes) : Comparable<Tower> {
    //position
    var x: Float = squareField.coordX
    var y: Float
    //scale
    var w: Int = squareField.width
    var h: Int = (2*w)
    //game variables
    var level: Int = 0
    //array sorting
    private val sortingNr: Int = squareField.mapPos["y"]!!
    override fun compareTo(other: Tower): Int = this.sortingNr.compareTo(other.sortingNr)

    init {
        squareField.hasTower = this
        y = squareField.coordY - w
    }
}