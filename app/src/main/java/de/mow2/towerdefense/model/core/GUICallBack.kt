package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

interface GUICallBack {
    fun toggleBuildMenu(squareField: SquareField)
    fun buildTower(type: TowerTypes, level: Int = 0)
}