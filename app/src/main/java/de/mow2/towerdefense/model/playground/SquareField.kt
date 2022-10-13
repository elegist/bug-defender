package de.mow2.towerdefense.model.playground

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get

class SquareField(val coordX: Float, val coordY: Float, val width: Int, val height: Int, val mapPos: Map<String, Int>, private val image: Bitmap) {
    val TAG = javaClass.name
    private val mutableImg: Bitmap = Bitmap.createScaledBitmap(image, width, height, false)
    private var color = mutableImg[0, 0]

    fun drawField(canvas: Canvas) {
        canvas.drawBitmap(mutableImg, coordX, coordY, null)
        
    }

    override fun toString(): String {
        return "${javaClass.name} - posX: $coordX, posY: $coordY, width: $width, height: $height"
    }

    fun lockSquare() {
        Log.i(TAG, "Hallo Welt! Ich bin ein Quadrat auf dem Spielfeld. Meine Koordinaten sind: $coordX, $coordY")
        mapPos.forEach { Log.i(TAG, it.toString()) }
        mutableImg.eraseColor(Color.BLUE)
    }

    fun unlockSquare() {
        Log.i(TAG, "Hallo Welt! Ich wurde wieder freigegeben!")
        mutableImg.eraseColor(color)
    }
}