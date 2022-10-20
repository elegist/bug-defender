package de.mow2.towerdefense.controller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LevelGenerator(): ViewModel() {
    //variables
    val coinAmnt: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    private var livesAmnt = 0

    fun initLevel(level: Int) {
        when(level) {
            0 -> {
                /* Endless mode */
                livesAmnt = 3
                coinAmnt.value = 1000
            }
            1 -> {/* Level 1 */}
            2 -> {/* Level 2 */}
            else -> {
                /* Endless mode */
                livesAmnt = 3
                coinAmnt.value = 2000
            }
        }
    }

    //TODO: Trigger function when enemy is defeated / wave is completed / default income etc.
    fun increaseCoins(newValue: Int){
        val oldVal = coinAmnt.value!!
        coinAmnt.value = oldVal + newValue
    }

    //TODO: Different values for different TowerTypes
    fun decreaseCoins(newValue: Int) : Boolean {
        val oldVal = coinAmnt.value!!
        return if(coinAmnt.value!! >= (0 + newValue)) {
            coinAmnt.value = oldVal - newValue
            true
        } else {
            false
        }
    }
}