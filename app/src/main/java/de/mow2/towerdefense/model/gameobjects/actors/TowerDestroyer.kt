package de.mow2.towerdefense.model.gameobjects.actors

import android.util.Log
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

class TowerDestroyer(val towerToDestroy: Tower) : GameObject() {
    override var position = Vector2D(0f, towerToDestroy.position.y)
    override var height = GameManager.playGround.squareSize
    override var width = height

    init {
        speed = 0.15f
    }

    override fun update() {
        moveTo(Vector2D(960f, position.y))
    }
}