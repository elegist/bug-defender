package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

class Projectile(val tower: Tower, val enemy: Enemy) : GameObject() {
    override var position = tower.positionCenter
    override var width: Int = 0
    override var height: Int = 0
    val baseDamage = tower.baseDamage

    init{
        // TODO(): each tower could have different projectile speeds
        speed = 0.2f
    }

    override fun update() {
        moveTo(enemy.positionCenter)

        orientation = if(distance.x < -5) {
            3 //left
        } else if(distance.x > 5) {
            1 //right
        } else if(distance.y < 0) {
            0 //up
        } else {
            2 //down (default)
        }
    }
}