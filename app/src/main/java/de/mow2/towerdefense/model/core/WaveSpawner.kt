
package de.mow2.towerdefense.model.core

import android.util.Log
import de.mow2.towerdefense.model.gameobjects.actors.Enemy
import de.mow2.towerdefense.model.gameobjects.actors.Enemy.EnemyType
import kotlin.random.Random

/**
 * TODO: First few waves are predetermined show the player all the enemies.
 * TODO: Boss round with creeps
 * TODO: infinite waves: increase chance of enemies that haven't been spawned in a set amount of waves to make it more engaging
 * TODO: alternate between hard and easy waves so it doesn't get dull //
 */
class WaveSpawner(val gameLevel: Int) {

    //number of enemies based on difficulty
    private val baseSpawnCount = 10 //Random.nextInt(startingWeak, startingStrong) // TODO: to balance this, it should not apply to the first few rounds

    //enemy types and count for a specific wave
    private var waveEnemyList = arrayListOf<EnemyType>()
    var enemyCount: Int = 0  //the number of enemies for this wave.
    var bossCounter: Int = 0

    //spawn rate values
    private var updateCycle: Float = 0f
    private var waitUpdates: Float = 0f
    private var spawnsPerMinute: Float = 0f
        set(value){
            field = value
            val spawnsPerSecond: Float = field / 60
            //link with target updates per second to convert to updates per spawn
            updateCycle = GameLoop.targetUPS / spawnsPerSecond
            waitUpdates = 0f
        }

    /**
     * spawn rate cycle. Wait a specific amount of updates
     * @see spawnsPerMinute controls how often per minute this function returns true
     * @return true || false
     */
    private fun canSpawn(): Boolean{
        return if(waitUpdates <= 0f) {
            waitUpdates += updateCycle
            true
        }else{
            waitUpdates--
            false
        }
    }

    private fun spawnWave(){
        val enemy = Enemy(waveEnemyList.random())
        if(enemyCount > 0){
            GameManager.addEnemy(enemy)
            enemyCount--
        }
        debugSpawnerValues()
    }

    fun update() {
        if(canSpawn()){
            spawnWave()
        }
    }

    init{
        when {
            gameLevel == 0 -> {
                waveEnemyList.add(EnemyType.values()[gameLevel])
                spawnsPerMinute = baseSpawnsPerMinute
                enemyCount = baseSpawnCount
            }
            gameLevel in 1 until EnemyType.values().size-1 -> { //size-1 because SKELETONKING (BOSS) is on the last position
                waveEnemyList.add(EnemyType.values()[gameLevel])
                spawnsPerMinute = baseSpawnsPerMinute*gameLevel
                enemyCount = baseSpawnCount*gameLevel
            }
            gameLevel % EnemyType.values().size-1 == 0 -> { //TODO: waveEnemyList is empty but should have all enemies from
                EnemyType.values().forEachIndexed{ i, type ->
                    if(i in 8..12){
                        waveEnemyList.add(type)
                    }
                }
                spawnsPerMinute = baseSpawnsPerMinute*gameLevel
                enemyCount = baseSpawnCount*gameLevel
            }

            //------------------TODO: Wave after first Boss round unfinished-------------------------
            gameLevel > EnemyType.values().size-1 -> { //TODO: Boss round unfinished
                EnemyType.values().forEachIndexed{ i, type ->
                    if(i in 8..12){
                        waveEnemyList.add(type)
                    }
                }
                //now every 5 levels the minimum and maximum amount of enemies will increase by 10 TODO: if difficultyIncrease is only used here then just use gameLevel instead
                if(difficultyIncrease % 5 == 0){
                    startingWeak+=10
                    startingStrong+=10
                }
                //spawnsPerMinute = //TODO: slower increase for spawnrate
                enemyCount = baseSpawnCount + Random.nextInt(startingWeak+1, startingStrong+1)

                //after the first boss wave, difficulty will increase
                difficultyIncrease++
                bossCounter++
            }
        }

    }

    private fun debugSpawnerValues(){
        Log.i("spacer", "----------")
        Log.i("gameLevel", "$gameLevel")
        Log.i("spawnCount", "$enemyCount")
        Log.i("startingWeak", "$startingWeak")
        Log.i("startingStrong", "$startingStrong")
        Log.i("difficultyIncrease", "$difficultyIncrease")
        Log.i("spawnsPerMinute", "$spawnsPerMinute")
        Log.i("waveEnemyList", "$waveEnemyList")
    }

    companion object{
        //spawn rate
        private var baseSpawnsPerMinute = 10f

        //spawn count ranges to be used after the first boss wave
        private var startingWeak = 5 //default = 5
        private var startingStrong = 15 //default = 5
        private var difficultyIncrease = 0 // increase difficulty based on this value
    }
}


