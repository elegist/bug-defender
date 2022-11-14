package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject

/**
 * A specific tower
 * @param squareField references the field on which the tower has been built
 * @param type the towers type
 */
class Tower(val squareField: SquareField, var type: TowerTypes) : Comparable<Tower>, GameObject(), java.io.Serializable {
    override val speed: Float = 0f

    //position
    var x: Float = squareField.coordX
    var y: Float
    //scale
    var w: Int = squareField.width
    var h: Int = (2*w)
    //game variables
    var level: Int = 0
    var hasTarget = false
    var target: Enemy? = null
    //queue sorting
    override fun compareTo(other: Tower): Int = this.y.compareTo(other.y)

    val baseRange = 500
    override fun update() {
        //TODO("Not yet implemented")
    }

    init {
        squareField.hasTower = this
        y = squareField.coordY - w
        actionsPerMinute = 120f
    }
}