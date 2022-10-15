package de.mow2.towerdefense.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import de.mow2.towerdefense.R

class BuildUpgradeMenu(var x: Float, var y: Float) {
    var active = false
    val width = 500f
    val height = 200f

    fun getRangeX(): ClosedFloatingPointRange<Float> {
        return x..(x+width)
    }

    fun getRangeY(): ClosedFloatingPointRange<Float> {
        return y..(y+height)
    }
}