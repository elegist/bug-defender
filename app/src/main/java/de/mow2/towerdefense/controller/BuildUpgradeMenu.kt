package de.mow2.towerdefense.controller

class BuildUpgradeMenu(val x: Float, val y: Float, val menuPosition: String) {
    var active = false
    val width = 500f
    val height = 200f

    //TODO: Calculate correct range given the menuPosition
    fun getRangeX(): ClosedFloatingPointRange<Float> {
        return x..(x+width)
    }

    fun getRangeY(): ClosedFloatingPointRange<Float> {
        return y..(y+height)
    }
}