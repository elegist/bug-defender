package de.mow2.towerdefense.controller.helper

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes

/**
 * Custom ImageButton - This button is part of the in-game build menu
 */
class BuildButton(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : AppCompatImageButton(context, attributeSet, defStyleAttr) {
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, type: TowerTypes) : this(context, attributeSet, defStyleAttr) {
        initMenu(type)
    }

    init {
        this.setPadding(30, 0, 30, 30)
    }

    private fun initMenu(type: TowerTypes) {
        when(type) {
            TowerTypes.SINGLE -> {
                this.setImageResource(R.drawable.tower_single_imagebtn)
            }
            TowerTypes.SLOW -> {
                this.setImageResource(R.drawable.tower_slow_imagebtn)
            }
            TowerTypes.AOE -> {
                this.setImageResource(R.drawable.tower_aoe_imagebtn)
            }
            TowerTypes.MAGIC -> {
                this.setImageResource(R.drawable.tower_magic_imagebtn)
            }
        }
    }
}