package de.mow2.towerdefense.controller

import android.util.Log
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes


class BuildUpgradeMenu(val x: Float, val y: Float) {
    var active = false

    fun getRangeX(): ClosedFloatingPointRange<Float> {
        //Log.i("Get Range", " x ${0f..width}")
        return 0f..width
    }

    fun getRangeY(): ClosedFloatingPointRange<Float> {
        val start = GameView.bottomEnd - height
        val end = GameView.bottomEnd
        Log.i("Get Range", " y ${start..end}")
        return start..end
    }

    fun getTowerType(x: Float) : TowerTypes {
        var towerType: TowerTypes = TowerTypes.BLOCK
        GameManager.buildMenuButtonRanges.forEachIndexed { i, element ->
            if(x in element) {
                towerType = TowerTypes.values()[i]
            }
        }
        return towerType
    }

    companion object {
        val width = GameView.gameWidth.toFloat()
        const val height = 200f
    }
}