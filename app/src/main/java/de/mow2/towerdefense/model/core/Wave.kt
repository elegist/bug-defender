package de.mow2.towerdefense.model.core


import de.mow2.towerdefense.model.gameobjects.actors.Enemy.EnemyType

/**
 * Decides what enemy has to be spawned, how many enemies are in this instance of a wave
 * and at which rate a specific enemy type will spawn. Currently depends on GameManager.gameLevel to
 * work correctly. TODO: remove parameter and use gameLevel directly?
 * @see GameManager.gameLevel
 */
class Wave(wave: Int): java.io.Serializable { // TODO: save current wave data in preferences?
    // enemy for a wave

    lateinit var enemyType: EnemyType
    var remaining: Int = 0

    init{
        when{
            wave == 0 -> {
                enemyType = EnemyType.LEAFBUG
                spawnsPerMinute = 30f
                remaining = 10
            }
            wave == 1 -> {
                enemyType = EnemyType.FIREBUG
                spawnsPerMinute = 60f
                remaining = 20
            }
            wave == 2 -> {
                enemyType = EnemyType.MAGMACRAB
                spawnsPerMinute = 60f
                remaining = 30
            }
            wave == 3 -> {
                enemyType = EnemyType.SCORPION
                spawnsPerMinute = 60f
                remaining = 10
            }
            wave == 4 -> {
                enemyType = EnemyType.CLAMPBEETLE
                spawnsPerMinute = 120f
                remaining = 10
            }
            wave == 5 -> {
                enemyType = EnemyType.FIREWASP
                spawnsPerMinute = 120f
                remaining = 10
            }
            wave == 6 -> {
                enemyType = EnemyType.LOCUST
                spawnsPerMinute = 120f
                remaining = 10
            }
            wave == 7 -> {
                enemyType = EnemyType.SKELETONGRUNT
                spawnsPerMinute = 240f
                remaining = 10
            }
            wave == 8 -> {
                enemyType = EnemyType.NECROMANCER
                spawnsPerMinute = 90f
                remaining = 10
            }
            wave == 9 -> {
                enemyType = EnemyType.SKELETONWARRIOR
                spawnsPerMinute = 100f
                remaining = 10
            }
            wave == 10 -> {
                enemyType = EnemyType.SKELETONKNIGHT
                spawnsPerMinute = 30f
                remaining = 10
            }
            wave == 11 -> {
                enemyType = EnemyType.SKELETONKING
                spawnsPerMinute = 10f
                remaining = 10
            }
            wave >= 12 -> {
                EnemyType.values().random().also{ type ->
                    enemyType = type
                }
                spawnsPerMinute = 120f
                remaining = 10
            }
        }
    }
    companion object{
        //spawn rate variables
        private var updateCycle: Float = 0f
        private var waitUpdates: Float = 0f
        private var spawnsPerMinute: Float = 0f
            set(value){
                field = value
                val spawnsPerSecond: Float = field / 60
                //link with target updates per second to convert to updates per spawn
                updateCycle = GameLoop.targetUPS / spawnsPerSecond
            }
        //spawn rate per wave
        fun canSpawn(): Boolean{
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