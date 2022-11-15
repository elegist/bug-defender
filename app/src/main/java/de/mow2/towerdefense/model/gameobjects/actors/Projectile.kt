package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

class Projectile(val tower: Tower, val enemy: Enemy) : GameObject() {
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
    override var position = tower.position
    override var width: Int = 0
    override var height: Int = 0
    val baseDamage = tower.baseDamage

    init{
        // TODO(): each tower could have different projectile speeds
        speed = 0.2f
    }

    override fun update() {
        moveTo(enemy.positionCenter)
    }
}