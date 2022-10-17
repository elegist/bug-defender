package de.mow2.towerdefense.controller.gameobjects

import de.mow2.towerdefense.controller.GameLoop
import de.mow2.towerdefense.controller.GameView.Companion.gameHeight
import de.mow2.towerdefense.controller.GameView.Companion.gameWidth
import kotlin.random.Random
import kotlin.random.nextInt


class Enemy(var target: Target, coordX: Float = (Random.nextInt(1 until gameWidth).toFloat()), coordY: Float = 0f
): GameObject(coordX, coordY) {
    /**
     * link movement speed to gam
     * calc pixels per update and init speed
     */
    private var speedPixelsPerSecond: Float = (gameWidth+gameHeight)*0.02f
        set(value){
            field = (gameWidth+gameHeight)*value
        }
    private val speed = speedPixelsPerSecond / GameLoop.targetUPS

    override fun update(){
        /**
         * math for the movement calculation:
         * https://www.codeproject.com/articles/990452/interception-of-two-moving-objects-in-d-space
         */
        //vector between enemy and target
        var distanceToTargetX: Float = target.getPositionX() - coordX
        var distanceToTargetY: Float = target.getPositionY() - coordY
        //absolute distance
        var distanceToTargetAbs: Float = getDistanceBetweenObjects(this, target)
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
        var spawnsPerMinute: Float = 200f
        var spawnsPerSecond: Float = spawnsPerMinute / 60
        //link with updates per second
        private val updateCycle: Float = GameLoop.targetUPS / spawnsPerSecond
        private var waitUpdates: Float = updateCycle

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

