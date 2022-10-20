package de.mow2.towerdefense.controller

import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes


class BuildUpgradeMenu(val x: Float, val y: Float) {
    var active = false

    fun getRangeX(): ClosedFloatingPointRange<Float> {
        return 0f..width
    }

    fun getRangeY(): ClosedFloatingPointRange<Float> {
        val start = (GameView.bottomEnd - height) + GameActivity.scrollOffset
        val end = GameView.bottomEnd + GameActivity.scrollOffset
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

    fun getTowerCost(type: TowerTypes): Int {
        var cost = when(type) {
            TowerTypes.BLOCK -> 100
            TowerTypes.SLOW -> 200
            TowerTypes.AOE -> 300
        }
        return cost
    }

    companion object {
        val width = GameView.gameWidth.toFloat()
        const val height = 200f
    }
}