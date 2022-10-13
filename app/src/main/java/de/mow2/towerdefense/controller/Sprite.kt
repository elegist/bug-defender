package de.mow2.towerdefense.controller

import android.graphics.Rect
import android.graphics.Canvas


class Sprite(private val spriteSheet: SpriteSheet, private val rect: Rect) {

    fun draw(canvas: Canvas, x: Int, y: Int){
        canvas.drawBitmap(spriteSheet.image, rect, Rect(x, y, x+64, y+64), null)
    }
}