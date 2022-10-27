package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.model.gameobjects.actors.Tower

class SquareField(val coordX: Float, val coordY: Float, val width: Int, val height: Int, val mapPos: Map<String, Int>) {

    //var for blocking this field (tower built)
    var isBlocked = false
    var hasTower: Tower? = null

    fun removeTower() {
        isBlocked = false
        hasTower = null
    }

    override fun toString(): String {
        return "${javaClass.name} - posX: $coordX, posY: $coordY, width: $width, height: $height"
    }
}