package de.mow2.towerdefense.model.core

import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

interface GUICallBack {
    fun initializeBuildMenu()
    fun toggleBuildMenu(squareField: SquareField)
    fun buildTower(type: TowerTypes)
}