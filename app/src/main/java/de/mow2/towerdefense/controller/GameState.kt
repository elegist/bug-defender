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


class GameState (context: Context){
    private val dir = context.filesDir.path.toString()
    private val file = File(dir, "gameState.json")
    fun saveGameState() {
        val file = File(dir, "gameState.json")
        try {
            if(file.createNewFile()) {
                val output = FileOutputStream(file)
                val objectOut = ObjectOutputStream(output)

                objectOut.writeObject(GameManager.gameLevel)
                objectOut.writeObject(GameManager.creepList)
                objectOut.writeObject(GameManager.towerList)
                objectOut.writeObject(GameManager.projectileList)
                objectOut.writeObject(GameManager.playGround)

                objectOut.close()
                Log.i("Tag", "Datei erstellt")
            } else {
                Log.i("Tag", "Datei wurde nicht erstellt")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun readGameState() {
        val input = ObjectInputStream(FileInputStream(file))
        GameManager.gameLevel = input.readObject() as Int
        GameManager.creepList = input.readObject() as LinkedBlockingQueue<Creep>
        GameManager.towerList = input.readObject() as PriorityBlockingQueue<Tower>
        GameManager.projectileList = input.readObject() as LinkedBlockingQueue<Projectile>
        GameManager.playGround = input.readObject() as PlayGround

        input.close()
    }
}