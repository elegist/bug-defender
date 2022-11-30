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
    //visual scaling
    override var width = squareField.width
    override var height = 2 * width
    //position on screen
    override var position = Vector2D(squareField.position.x, squareField.position.y - width)
    var weaponOffset = 0f
    var rotationCorrection = 0f
    private var isRotatable = true
    //tower-specific game variables
    var level: Int = 0
        set(value) {
            field = value
            scaleTowerValues()
        }
    var hasTarget = false
    var target: Enemy? = null
    //queue sorting
    override fun compareTo(other: Tower): Int = this.position.y.compareTo(other.position.y)
    //range,dmg,speed base settings
    private var baseRange = 2 * width + width / 2
    var finalRange = 0
    var baseDamage = 0
    private var baseSpeed = 120f

    //detects if the tower should be shooting right now. received from gamemanager
    var isShooting = false

    override fun update() {
        if (isRotatable) {
            if (target != null){
                distance = target!!.position - position
                val directionTolerance = 80

                if (distance.x > -directionTolerance && distance.x < directionTolerance) { // up / down
                    orientation = if (distance.y < 0) {
                        rotationCorrection = 0f
                        0 // up
                    } else {
                        rotationCorrection = 0f
                        4 // down
                    }
                } else if (distance.y > -directionTolerance && distance.y < directionTolerance) { //left / right
                    orientation = if (distance.x < 0) {
                        rotationCorrection = 0f
                        6 // left
                    } else {
                        rotationCorrection = 0f
                        2 // right
                    }
                } else if (distance.x < -directionTolerance) { // left-diagonals
                    orientation = if (distance.y < 0) {
                        rotationCorrection = -squareField.width / 6f
                        7 // left-up
                    }else {
                        rotationCorrection = -squareField.width / 6f
                        5 // left-down
                    }
                } else if (distance.x > directionTolerance) { //right-diagonals
                    orientation = if (distance.y < 0) {
                        rotationCorrection = -squareField.width / 6f
                        1 // right-up
                    } else {
                        rotationCorrection = -squareField.width / 6f
                        3 // right-down
                    }
                }
            }
        }
    }

    init {
        squareField.tower = this
        scaleTowerValues()
    }

    /**
     * Method to call after tower has been built or upgraded
     * calculates speed, damage and range for this towers level
     */
    private fun scaleTowerValues() {
        when (level) {
            0 -> weaponOffset = height / 6f
            1 -> weaponOffset = height / 9f
            2 -> weaponOffset = 0f
        }
        when(type) {
            TowerTypes.SINGLE -> {
                finalRange = baseRange + level * width
                baseDamage = 1 + level
                actionsPerMinute = baseSpeed + level * 10
            }
            TowerTypes.SLOW -> {
                isRotatable = false
                finalRange = baseRange + level * width / 2
                baseDamage = 0
                actionsPerMinute = baseSpeed / 2 + level * 10
            }
            TowerTypes.AOE -> {
                isRotatable = false
                finalRange = baseRange + level * width / 2
                baseDamage = 2 + level * 2
                actionsPerMinute = baseSpeed / 2 + level * 20
            }
            TowerTypes.MAGIC -> {
                isRotatable = false
                finalRange = baseRange + level * width
                baseDamage = 5 + level * 5
                actionsPerMinute = baseSpeed / 4 + level * 10
            }
        }
    }
}