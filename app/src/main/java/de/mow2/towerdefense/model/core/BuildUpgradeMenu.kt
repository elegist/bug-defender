package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.controller.GameManager
import de.mow2.towerdefense.model.gameobjects.actors.Tower
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes


class BuildUpgradeMenu {

    fun getTowerCost(type: TowerTypes): Int {
        var cost = when(type) {
            TowerTypes.BLOCK -> 100
            TowerTypes.SLOW -> 200
            TowerTypes.AOE -> 300
        }
        return cost
    }

    fun buildTower(selectedField: SquareField, towerType: TowerTypes) {
        val tower = when(towerType) {
            TowerTypes.BLOCK -> {
                Tower(selectedField, TowerTypes.BLOCK)
            }
            TowerTypes.SLOW -> {
                Tower(selectedField, TowerTypes.SLOW)
            }
            TowerTypes.AOE -> {
                Tower(selectedField, TowerTypes.AOE)
            }
        }
        selectedField.isBlocked = true //important!! block field for path finding
        GameManager.towerList = GameManager.towerList.plus(tower)
        GameManager.towerList.sort() //sorting array to avoid overlapped drawing
    }
}