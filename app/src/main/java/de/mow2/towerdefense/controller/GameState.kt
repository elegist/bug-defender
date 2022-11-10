package de.mow2.towerdefense.controller

import android.content.Context
import android.util.Log
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.core.PlayGround
import de.mow2.towerdefense.model.gameobjects.actors.Creep
import de.mow2.towerdefense.model.gameobjects.actors.Projectile
import de.mow2.towerdefense.model.gameobjects.actors.Tower
import java.io.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue


class GameState{
    /**
     * Call this to save game data to local file
     * @param context App context
     */
    fun saveGameState(context: Context) {
        val dir = context.filesDir
        val file = File(dir, "/gameState.json")
        //detects if file exists, creates one if not and calls saveGame to store game variables and objects
        if(!file.exists()) {
            try {
                if(file.createNewFile()) {
                    Log.i("SaveGame: ", "Neue Datei erstellt")
                    saveGame(file)
                } else {
                    Log.i("SaveGame: ", "Datei konnte nicht erstellt werden")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.i("SaveGame: ", "Vorhandene Datei wird beschrieben")
            saveGame(file)
        }
    }
    /**
     * Saves game data to specified file
     * @param file Savegame file
     */
    private fun saveGame(file: File) {
        try {
            //open output stream
            val output = FileOutputStream(file)
            val objectOut = ObjectOutputStream(output)
            //save all needed data
            objectOut.writeObject(GameManager.gameLevel)
            objectOut.writeObject(GameManager.livesAmnt)
            objectOut.writeObject(GameManager.coinAmnt)
            objectOut.writeObject(GameManager.killCounter)
            objectOut.writeObject(GameManager.towerList)
            objectOut.writeObject(GameManager.playGround)
            //close output stream
            objectOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reads all saved game variables and objects from specified file
     * @param context App context
     */
    fun readGameState(context: Context) {
        //get path and file
        val dir = context.filesDir
        val file = File(dir, "/gameState.json")
        //load from file if it exists
        if(file.exists()) {
            val input = ObjectInputStream(FileInputStream(file))
            //read all saved variables and objects
            val level = input.readObject() as Int
            val lives = input.readObject() as Int
            val coins = input.readObject() as Int
            val kills = input.readObject() as Int
            val towerList = input.readUnshared() as PriorityBlockingQueue<Tower>
            val playGround = input.readObject() as PlayGround
            //reset tower objects
            towerList.forEach {
                it.actionsPerMinute = 60f
                it.hasTarget = false
                it.target = null
            }
            //replace GameManager variables with saved ones
            GameManager.gameLevel = level
            GameManager.livesAmnt = lives
            GameManager.coinAmnt = coins
            GameManager.killCounter = kills
            GameManager.towerList = towerList
            GameManager.playGround = playGround
            //close input stream
            input.close()
        }
    }
}