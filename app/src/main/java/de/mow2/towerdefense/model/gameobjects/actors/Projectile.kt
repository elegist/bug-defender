package de.mow2.towerdefense.model.gameobjects.actors

import de.mow2.towerdefense.model.core.GameLoop
import de.mow2.towerdefense.controller.GameView
import de.mow2.towerdefense.model.core.SquareField
import de.mow2.towerdefense.model.gameobjects.GameObject

class Projectile(squareField: SquareField, val tower: Tower, val creep: Creep) : GameObject(squareField) {
    /**
     * calc pixels per update and init speed
     */
    private var speedPixelsPerSecond: Float = (GameView.gameWidth + GameView.gameHeight)*0.2f
        set(value){
            field = (GameView.gameWidth + GameView.gameHeight)*value
        }
    //init speed
    private val speed = speedPixelsPerSecond / GameLoop.targetUPS

    val baseDamage = 1

    override fun update() {
        var distanceToTargetX: Float = creep.positionX() - positionX()
        var distanceToTargetY: Float = creep.positionY() - positionY()
        //absolute distance
        var distanceToTargetAbs: Float = findDistance(this.positionX(), this.positionY(), creep.positionX(), creep.positionY())
        //direction
        var directionX: Float = distanceToTargetX/distanceToTargetAbs
        var directionY: Float = distanceToTargetY/distanceToTargetAbs
        //check if target has been reached
        if(distanceToTargetAbs > 0f){
            velocityX = directionX*speed
            velocityY = directionY*speed
        }else{
            velocityX = 0f
            velocityY = 0f
        }
        //update coordinates
        coordX += velocityX
        coordY += velocityY
    }

    companion object{
        //set spawn rate
        var spawnsPerMinute: Float = 40f
        private var spawnsPerSecond: Float = spawnsPerMinute / 60
        //link with target updates per second to convert to updates per spawn
        private val updateCycle: Float = GameLoop.targetUPS / spawnsPerSecond
        private var waitUpdates: Float = 0f

        fun canSpawn() :Boolean{
            return if(waitUpdates <= 0f) {
                waitUpdates += updateCycle
                true
            }else{
                waitUpdates--
                false
            }
        }
    }
}