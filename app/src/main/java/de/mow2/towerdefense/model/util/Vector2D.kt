package de.mow2.towerdefense.model.util

/**
 * Helper class to define mathematical 2D-Vectors which hold a x and y variable
 * @param x x-coordinate of the vector
 * @param y y-coordinate of the vector
 * */

data class Vector2D(val x: Int, val y: Int) {
    constructor(x: Float, y: Float) : this(x.toInt(), y.toInt())
    constructor(x: Double, y: Double) : this(x.toInt(), y.toInt())

    operator fun Vector2D.plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun Vector2D.minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun Vector2D.times(other: Vector2D) = Vector2D(x * other.x, y * other.y)
    operator fun Vector2D.div(other: Vector2D) = Vector2D(x / other.x, y / other.y)
    operator fun Vector2D.rem(other: Vector2D) = Vector2D(x % other.x, y % other.y)
    operator fun Vector2D.inc() = Vector2D(x + 1, y + 1)
    operator fun Vector2D.dec() = Vector2D(x - 1, y - 1)
}