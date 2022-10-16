package de.mow2.towerdefense.controller.gameobjects

import android.graphics.Bitmap
import android.graphics.Canvas


abstract class GameObjectFrame(coordX: Float, coordY: Float) : GameObject(coordX, coordY) {

    override fun draw(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(bitmap, coordX, coordY, null)
    }

}