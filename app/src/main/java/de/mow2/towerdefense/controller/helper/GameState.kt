package de.mow2.towerdefense.controller.helper

import android.content.Context
import android.util.Log
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.actors.Tower
import java.io.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Saves and loads game data
 * @param context App context
 */
class GameState(val context: Context) {

    /**
     * Returns save game file
     * @param context App context
     */
    fun defineFile(context: Context): File {
        val dir = context.filesDir
        return File(dir, "/gameState.json")
    }

    /**
     * Call this to save game data to local file
     */
    fun saveGameState() {
        val file = defineFile(context)
        //detects if file exists, creates one if not and calls saveGame to store game variables and objects
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
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
     * @param file save game file
     */
    private fun saveGame(file: File) {
        try {
            //open output stream
            val output = FileOutputStream(file)
            val objectOut = ObjectOutputStream(output)
            //save all needed data
            objectOut.writeObject(GameManager.gameLevel)
            objectOut.writeObject(GameManager.enemiesKilled)
            objectOut.writeObject(GameManager.livesAmnt)
            objectOut.writeObject(GameManager.coinAmnt)
            objectOut.writeObject(GameManager.killCounter)
            objectOut.writeObject(GameManager.towerList)
            //close output stream
            objectOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reads all saved game data from specified file, if it exists
     */
    fun readGameState() {
        val file = defineFile(context)
        //load from file if it exists
        if (file.exists()) {
            val input = ObjectInputStream(FileInputStream(file))
            //read all saved variables and objects
            val level = input.readObject() as Int
            val enemiesKilled = input.readObject() as Int
            val lives = input.readObject() as Int
            val coins = input.readObject() as Int
            val kills = input.readObject() as Int
            val towerList = input.readObject() as CopyOnWriteArrayList<Tower>
            //reset tower objects
            towerList.forEach {
                it.actionsPerMinute = 60f
                it.hasTarget = false
                it.target = null
            }
            //replace GameManager variables with saved ones
            GameManager.gameLevel = level
            GameManager.enemiesKilled = enemiesKilled
            GameManager.livesAmnt = lives
            GameManager.coinAmnt = coins
            GameManager.killCounter = kills
            GameManager.towerList = towerList
            //close input stream
            input.close()
        }
    }

    /**
     * Deletes the save game file if exists
     * @return Boolean - true if file is deleted; false otherwise
     */
    fun deleteSaveGame(): Boolean {
        val file = defineFile(context)
        return file.delete()
    }
}