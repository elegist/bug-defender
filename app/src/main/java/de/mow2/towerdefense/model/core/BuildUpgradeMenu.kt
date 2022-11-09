package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.model.gameobjects.actors.Tower
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes


class BuildUpgradeMenu {

    /**
     * Calculates tower value based on its type and level
     * @param type value of TowerTypes
     * @param level this towers level
     */
    fun getTowerCost(type: TowerTypes, level: Int = 0): Int {
        val cost = when(type) {
            TowerTypes.BLOCK -> 100
            TowerTypes.SLOW -> 200
            TowerTypes.AOE -> 300
        }
        return cost * (level + 1)
    }

    /**
     * Adds a specific tower to the drawing list and blocks the underlying field for enemy movement
     * @param selectedField the field at which the tower is to be placed
     * @param towerType the specific type of the tower to be built
     */
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
        GameManager.addTower(tower)
    }

    /**
     * Destroys a tower and resets the squareField
     */
    fun destroyTower(tower: Tower) {
        tower.squareField.removeTower() //free square
        GameManager.towerList.remove(tower) //remove tower from drawing list
    }

    /**
     * Upgrades a tower
     */
    fun upgradeTower(selectedField: SquareField) {
        val tower = selectedField.hasTower
        if(tower != null) {
            tower.level++
        }
    }
}