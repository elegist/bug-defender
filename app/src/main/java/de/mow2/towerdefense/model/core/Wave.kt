package de.mow2.towerdefense.model.core


import android.content.ClipData.Item
import android.util.Log
import de.mow2.towerdefense.model.gameobjects.actors.Enemy
import de.mow2.towerdefense.model.gameobjects.actors.Enemy.EnemyType
import java.util.*
import kotlin.collections.HashMap


/**
 * Decides what enemy has to be spawned, how many enemies are in this instance of a wave
 * and at which rate a specific enemy type will spawn
 * @see GameManager.gameLevel is used to create a wave object inside GameManager
 * @see GameManager.initLevel updates the wave variable
 */
class Wave(wave: Int){ // TODO: save current wave data in preferences?
    //enemy types and count
    var waveEnemyList = mutableListOf<EnemyType>()
    var remaining: Int = 0  //the number of enemies for this wave


    // List mit enemies die einen Killcount haben
    // nur der Boss soll den killcounter voranbringen


    //spawn rate
    private var updateCycle: Float = 0f
    private var waitUpdates: Float = 0f
    private var spawnsPerMinute: Float = 0f
        set(value){
            field = value
            val spawnsPerSecond: Float = field / 60
            //link with target updates per second to convert to updates per spawn
            updateCycle = GameLoop.targetUPS / spawnsPerSecond
        }

    //spawn rate cycle
    fun canSpawn(): Boolean{
        return if(waitUpdates <= 0f) {
            waitUpdates += updateCycle
            true
        }else{
            waitUpdates--
            false
        }
    }

    init{
        when{
            wave == 0 -> {
                waveEnemyList.add(EnemyType.LEAFBUG)
                spawnsPerMinute = 30f
                remaining = 10
            }
            wave == 1 -> {
                waveEnemyList.add(EnemyType.FIREBUG)
                spawnsPerMinute = 30f
                remaining = 10
            }
            wave == 2 -> {
                waveEnemyList.add(EnemyType.MAGMACRAB)
                spawnsPerMinute = 60f
                remaining = 30
            }
            wave == 3 -> {
                waveEnemyList.add(EnemyType.SCORPION)
                spawnsPerMinute = 60f
                remaining = 10
            }
            wave == 4 -> {
                waveEnemyList.add(EnemyType.CLAMPBEETLE)
                spawnsPerMinute = 120f
                remaining = 10
            }
            wave == 5 -> {
                waveEnemyList.add(EnemyType.FIREWASP)
                spawnsPerMinute = 120f
                remaining = 10
            }
            wave == 6 -> {
                waveEnemyList.add(EnemyType.LOCUST)
                spawnsPerMinute = 120f
                remaining = 10
            }
            wave == 7 -> {
                waveEnemyList.add(EnemyType.VOIDBUTTERFLY)
                spawnsPerMinute = 240f
                remaining = 10
            }
            wave == 8 -> {
                waveEnemyList.add(EnemyType.SKELETONGRUNT)
                waveEnemyList.add(EnemyType.NECROMANCER)
                spawnsPerMinute = 20f
                remaining = 10
            }
            wave == 9 -> {
                waveEnemyList.add(EnemyType.SKELETONWARRIOR)
                waveEnemyList.add(EnemyType.SKELETONKNIGHT)
                spawnsPerMinute = 60f
                remaining = 20
            }
            wave % 10 == 0 -> {
                waveEnemyList.add(EnemyType.SKELETONGRUNT)
                waveEnemyList.add(EnemyType.NECROMANCER)
                waveEnemyList.add(EnemyType.SKELETONWARRIOR)
                waveEnemyList.add(EnemyType.SKELETONKNIGHT)
                waveEnemyList.add(EnemyType.SKELETONKING)
                spawnsPerMinute = 1f
                remaining = 1
            }
            wave >= 11 -> {
                waveEnemyList.addAll(EnemyType.values())
                spawnsPerMinute = 60f
                remaining = 60
            }
        }

    }
    companion object{
       //TODO: static access to a variable (currentWaveEnemies???)
    }
}


