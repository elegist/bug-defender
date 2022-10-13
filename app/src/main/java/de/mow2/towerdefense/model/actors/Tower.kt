package de.mow2.towerdefense.model.actors

import de.mow2.towerdefense.model.core.SquareField

class Tower(var squareField: SquareField, var type: TowerTypes) : Comparable<Tower> {
    //position
    var x: Float = squareField.coordX
    var y: Float
    //scale
    var w: Int = squareField.width
    var h: Int = (2*w)
    //array sorting
    private val sortingNr: Int = squareField.mapPos["y"]!!
    override fun compareTo(other: Tower): Int = this.sortingNr.compareTo(other.sortingNr)

    init {
        y = squareField.coordY - w
    }
}