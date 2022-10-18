package de.mow2.towerdefense.model.core

import android.util.Log

//TODO: remove log after "real" methods are implemented
class SquareField(val coordX: Float, val coordY: Float, val width: Int, val height: Int, val mapPos: Map<String, Int>) {
    private val TAG = javaClass.name

    //var for blocking this field (tower built) can be used e.g. for routing
    var isBlocked = false

    override fun toString(): String {
        return "${javaClass.name} - posX: $coordX, posY: $coordY, width: $width, height: $height"
    }

    /**
     * locks this square. TODO: display half-transparent image of tower to be built
     */
    fun selectSquare() {
        Log.i(TAG, "Hallo Welt! Ich bin ein Quadrat auf dem Spielfeld. Meine Koordinaten sind: $mapPos")
        //TODO: visuals
    }

    /**
     * unlock if user selects another square
     */
    fun clearSquare() {
        Log.i(TAG, "Hallo Welt! Ich wurde wieder freigegeben! $mapPos")
        //TODO: remove visuals
    }
}