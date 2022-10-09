package de.mow2.towerdefense.model.actors

import android.graphics.Bitmap
import android.graphics.Canvas
import de.mow2.towerdefense.model.playground.SquareField

class Tower(var squareField: SquareField, val image: Bitmap) {
    var x: Float
    var y: Float
    var w: Int
    var h: Int

    var resizedImage: Bitmap

    init {
        x = squareField.coordX
        y = squareField.coordY
        w = squareField.width
        // resize height to to keep aspect ratio, (1:2)
        h = (2*w)
        resizedImage = Bitmap.createScaledBitmap(image, w, h, false)
    }


    fun draw(canvas: Canvas){
        canvas.drawBitmap(resizedImage, x, y-(h/2), null)
    }
}

