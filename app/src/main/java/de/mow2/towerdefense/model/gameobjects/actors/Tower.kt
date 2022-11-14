package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject
import kotlin.math.pow
import kotlin.math.sqrt

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

    var baseRange = 0
    var baseDamage = 0

    override fun update() {
    }

    init {
        squareField.hasTower = this
        y = squareField.coordY - w
        actionsPerMinute = 120f
        coordX = x
        coordY = y

        when(type) {
            TowerTypes.BLOCK -> {
                baseRange = 2 * w + level * 100
                baseDamage = 1 + level
            }
            TowerTypes.SLOW -> {
                baseRange = (2.5 * w + level * 100).toInt()
                baseDamage = 0
            }
            TowerTypes.AOE -> {
                baseRange = w + level * 50
                baseDamage = 2 + level * 2
            }
        }
    }
}