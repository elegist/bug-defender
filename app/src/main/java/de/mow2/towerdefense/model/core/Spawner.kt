
package de.mow2.towerdefense.model.core

/**
 * Provides wave spawning related functions. TODO: maybe obsolete. Functionality moved to GameManager
 */
/*
class Spawner() {
    */
/*private lateinit var wave: Wave*//*

    private var wave = Wave(0)


    fun spawnWave(currentWave: Int){
       */
/*if(!this::wave.isInitialized){
            wave = Wave(GameManager.gameLevel)
        }*//*

        wave = Wave(currentWave)
        if(Wave.canSpawn()){
            GameManager.addEnemy(wave.enemy)
        }
    }
}*/
