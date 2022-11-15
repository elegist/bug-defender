package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

/**
 * A specific tower
 * @param squareField references the field on which the tower has been built
 * @param type the towers type
 */
class Tower(val squareField: SquareField, var type: TowerTypes) : Comparable<Tower>, GameObject(), java.io.Serializable {
    override val speed: Float = 0f
    //scale
    override var width = squareField.width
    override var height = 2 * width
    //position
    override var position = Vector2D(squareField.position.x, squareField.position.y - width)
    //game variables
    var level: Int = 0
    var hasTarget = false
    var target: Enemy? = null
    //queue sorting
    override fun compareTo(other: Tower): Int = this.position.y.compareTo(other.position.y)

    private var baseRange = 2 * width + width / 2
    var range = 0
    var baseDamage = 0

    override fun update() {
    }

    init {
        squareField.tower = this
        actionsPerMinute = 120f

        //define range and damage scaling for each type of tower
        when(type) {
            TowerTypes.BLOCK -> {
                range = baseRange + level * width
                baseDamage = 1 + level
            }
            TowerTypes.SLOW -> {
                range = baseRange / 2 + level * width / 2
                baseDamage = 0
            }
            TowerTypes.AOE -> {
                range = baseRange + level * width / 2
                baseDamage = 2 + level * 2
            }
        }
    }
}