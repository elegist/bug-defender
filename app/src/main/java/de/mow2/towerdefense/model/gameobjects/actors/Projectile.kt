package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.gameobjects.GameObject

class Projectile(val tower: Tower, val creep: Creep) : GameObject() {

    val baseDamage = 1

    init{
        coordX = tower.x
        coordY = tower.y
        // TODO(): each tower could have different projectile speeds
        speed = 0.2f
    }

    override fun update() {
        moveTo(creep.positionX(), creep.positionY())
        cooldown()
    }
}