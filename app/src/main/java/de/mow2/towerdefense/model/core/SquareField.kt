package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.model.gameobjects.actors.Tower

/**
 * Represents a single field on the playground
 * @param coordX the horizontal screen position in pixels
 * @param coordY the vertical screen position in pixels
 * @param width width of the field
 * @param height height of the field
 * @param mapPos a map representing x and y coordinates based on its position on screen / in 2D-array
 */
class SquareField(val coordX: Float, val coordY: Float, val width: Int, val height: Int, val mapPos: Map<String, Int>) {

    //var for blocking this field (tower built)
    var isBlocked = false
    var hasTower: Tower? = null

    /**
     * call when removing a tower, frees itself from blocked state
     */
    fun removeTower() {
        isBlocked = false
        hasTower = null
    }

    override fun toString(): String {
        return "${javaClass.name} - posX: $coordX, posY: $coordY, width: $width, height: $height"
    }
}