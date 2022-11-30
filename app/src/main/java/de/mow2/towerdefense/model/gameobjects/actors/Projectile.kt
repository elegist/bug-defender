package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.controller.helper.BitmapPreloader
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.GameObject
import de.mow2.towerdefense.model.helper.Vector2D

class Projectile(val tower: Tower, val enemy: Enemy) : GameObject() {
    override var position = tower.positionCenter
    override var width: Int = 0
    override var height: Int = 0
    val baseDamage = tower.damage

    init{
        // TODO(): each tower could have different projectile speeds
        speed = 0.2f
        if(tower.type == TowerTypes.AOE) {
            width = BitmapPreloader.projectileAnimsArray[tower.towerLevel][tower.type]!!.width / 2
            height = width
            position = Vector2D(position.x - width, position.y - height)
        }
    }

    override fun update() {
        if(tower.type != TowerTypes.AOE) {
            moveTo(enemy.positionCenter)
        } else {
            if(!tower.isShooting) {
                GameManager.projectileList.remove(this)
            }
        }
    }
}