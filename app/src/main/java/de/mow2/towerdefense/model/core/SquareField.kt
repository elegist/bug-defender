package de.mow2.towerdefense.model.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get

class SquareField(val coordX: Float, val coordY: Float, val width: Int, val height: Int, val mapPos: Map<String, Int>, private val image: Bitmap) {
    private val TAG = javaClass.name
    //initializing image and color
    private val mutableImg: Bitmap = Bitmap.createScaledBitmap(image, width, height, false)
    private var color = mutableImg[0, 0]
    //var for blocking this field (tower built) can be used e.g. for routing
    var isBlocked = false

    fun drawField(canvas: Canvas) {
        canvas.drawBitmap(mutableImg, coordX, coordY, null)
    }

    override fun toString(): String {
        return "${javaClass.name} - posX: $coordX, posY: $coordY, width: $width, height: $height"
    }

    /**
     * locks this square. TODO: display half-transparent image of tower to be built
     */
    fun selectSquare() {
        Log.i(TAG, "Hallo Welt! Ich bin ein Quadrat auf dem Spielfeld. Meine Koordinaten sind: ${mapPos.toString()}")
        mutableImg.eraseColor(Color.BLUE)
    }

    /**
     * unlock if user selects another square
     */
    fun clearSquare() {
        Log.i(TAG, "Hallo Welt! Ich wurde wieder freigegeben! ${mapPos.toString()}")
        mutableImg.eraseColor(color)
    }
}