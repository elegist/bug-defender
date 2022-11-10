package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.gameobjects.GameObject

class Projectile(val tower: Tower, val creep: Creep) : GameObject() {
    /**
     * Pixels per update for movement.
     * Will be multiplied with direction to get a velocity.
     * @see moveTo(target: GameObject)
     */
    override var speed: Float = 0f
        set(value){
            val rawPixels = (GameView.gameWidth + GameView.gameHeight)*value
            field = rawPixels / GameLoop.targetUPS
        }
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