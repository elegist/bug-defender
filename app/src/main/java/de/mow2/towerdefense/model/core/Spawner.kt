
package de.mow2.towerdefense.model.core

import android.util.Log
import de.mow2.towerdefense.model.gameobjects.actors.Enemy

/**
 * Provides wave spawning related functions. TODO: maybe obsolete. Functionality could be moved to GameManager
 */
class Spawner() {

    /**
     * Takes a wave object as an parameter to determine how to spawn enemies.
     * Enemy Type and spawn rate is provided by the wave object
     * @see Wave
     */
    fun spawnWave(currentWave: Wave) {
        if(currentWave.canSpawn() && currentWave.remaining != 0){
            GameManager.addEnemy(Enemy(currentWave.waveEnemyList.random()))
            currentWave.remaining--
        }
    }

}


