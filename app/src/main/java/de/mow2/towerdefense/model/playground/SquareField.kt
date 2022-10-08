package de.mow2.towerdefense.model.playground

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log

class SquareField(val coordX: Float, val coordY: Float, val width: Int, val height: Int, private val image: Bitmap) {
    val TAG = javaClass.name
    private val mutableImg: Bitmap = Bitmap.createScaledBitmap(image, width, height, false)

    fun drawField(canvas: Canvas) {
        canvas.drawBitmap(mutableImg, coordX, coordY, null)
    }

    override fun toString(): String {
        return "${javaClass.name} - posX: $coordX, posY: $coordY, width: $width, height: $height"
    }

    fun lockSquare() {
        mutableImg.eraseColor(Color.BLUE)
        Log.i(TAG, "Hallo Welt! Ich bin ein Quadrat auf dem Spielfeld. Meine Koordinaten sind: $coordX, $coordY")
    }

    fun unlockSquare() {
        mutableImg.eraseColor(Color.GREEN)
        Log.i(TAG, "Hallo Welt! Ich wurde wieder freigegeben!")
    }
}