package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject

class Projectile(val tower: Tower, val creep: Creep
) : GameObject() {

    val baseDamage = 1

    init{
        coordX = tower.squareField.coordX
        coordY = tower.squareField.coordY
        // TODO(): each tower could have different projectile speeds
        speed = 0.3f
    }

    override fun update() {
        moveTo(creep.positionX(), creep.positionY())
        cooldown()
    }

}