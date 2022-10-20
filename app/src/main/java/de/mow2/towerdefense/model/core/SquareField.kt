package de.mow2.towerdefense.model.core

import android.util.Log

//TODO: remove log after "real" methods are implemented
class SquareField(val coordX: Float, val coordY: Float, val width: Int, val height: Int, val mapPos: Map<String, Int>) {

    //var for blocking this field (tower built)
    var isBlocked = false

    override fun toString(): String {
        return "${javaClass.name} - posX: $coordX, posY: $coordY, width: $width, height: $height"
    }
}