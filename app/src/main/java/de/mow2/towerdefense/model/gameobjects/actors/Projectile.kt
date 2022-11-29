package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.gameobjects.GameObject

class Projectile(val tower: Tower, val enemy: Enemy) : GameObject() {
    override var position = tower.positionCenter
    override var width: Int = 0
    override var height: Int = 0
    val baseDamage = tower.damage

    init{
        // TODO(): each tower could have different projectile speeds
        speed = 0.2f
    }

    override fun update() {
        moveTo(enemy.positionCenter)
    }
}