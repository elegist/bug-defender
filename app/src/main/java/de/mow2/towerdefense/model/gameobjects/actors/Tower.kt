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
    //visual scaling
    override var width = squareField.width
    override var height = 2 * width
    //position on screen
    override var position = Vector2D(squareField.position.x, squareField.position.y - width)
    //tower-specific game variables
    var level: Int = 0
    var hasTarget = false
    var target: Enemy? = null
    //queue sorting
    override fun compareTo(other: Tower): Int = this.position.y.compareTo(other.position.y)
    //range,dmg,speed base settings
    private var baseRange = 2 * width + width / 2
    var finalRange = 0
    var baseDamage = 0
    private var baseSpeed = 120f

    override fun update() {
    }

    init {
        squareField.tower = this

        //define range, damage and speed scaling for each type of tower
        when(type) {
            TowerTypes.BLOCK -> {
                finalRange = baseRange + level * width
                baseDamage = 1 + level
                actionsPerMinute = baseSpeed + level * 10
            }
            TowerTypes.SLOW -> {
                finalRange = baseRange + level * width / 2
                baseDamage = 0
                actionsPerMinute = baseSpeed / 2 + level * 10
            }
            TowerTypes.AOE -> {
                finalRange = baseRange + level * width / 2
                baseDamage = 2 + level * 2
                actionsPerMinute = baseSpeed / 2 + level * 20
            }
        }
    }
}