package de.mow2.towerdefense.model.gameobjects.actors


import de.mow2.towerdefense.controller.SoundManager
import de.mow2.towerdefense.controller.Sounds
import de.mow2.towerdefense.model.gameobjects.GameObject

class Projectile(val tower: Tower, val enemy: Enemy) : GameObject() {
    override var position = tower.positionCenter
    override var width: Int = 0
    override var height: Int = 0
    val baseDamage = tower.baseDamage

    init{
        // TODO(): each tower could have different projectile speeds
        speed = 0.2f
        when(tower.type){
             TowerTypes.SINGLE -> {
                 SoundManager.soundPool.play(Sounds.ARROWSHOT.id, 0.2F, 0.2F, 2, 0, 1F)
            }
            TowerTypes.SLOW -> {
                SoundManager.soundPool.play(Sounds.SLOWSHOT.id, 0.2F, 0.2F, 2, 0, 1F)
            }
            TowerTypes.AOE -> {
                SoundManager.soundPool.play(Sounds.AOESHOT.id, 0.2F, 0.2F, 2, 0, 1F)
            }
            TowerTypes.MAGIC -> {
                SoundManager.soundPool.play(Sounds.MAGICSHOT.id, 0.2F, 0.2F, 2, 0, 1F)
            }
        }
    }

    override fun update() {
        moveTo(enemy.positionCenter)
    }
}