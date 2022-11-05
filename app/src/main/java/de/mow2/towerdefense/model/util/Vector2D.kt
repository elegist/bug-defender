package de.mow2.towerdefense.model.util

class Vector2D(val x: Float, val y: Float) {
    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    operator fun Vector2D.plus(other: Vector2D) : Vector2D {
        return Vector2D(x + other.x, y + other.y)
    }

    operator fun Vector2D.minus(other: Vector2D) : Vector2D {
        return Vector2D(x - other.x, y - other.y)
    }

    operator fun Vector2D.times(other: Vector2D) : Vector2D {
        return Vector2D(x * other.x, y * other.y)
    }

    operator fun Vector2D.div(other: Vector2D) : Vector2D {
        return Vector2D(x / other.x, y / other.y)
    }

    operator fun Vector2D.rem(other: Vector2D) : Vector2D {
        return Vector2D(x % other.x, y % other.y)
    }
}