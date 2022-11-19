package de.mow2.towerdefense.controller.helper

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat.getColor
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
        this.setBackgroundColor(getColor(context, R.color.green_overlay))
        this.setPadding(0, 0, 0, 30)
    }

    private fun initMenu(type: TowerTypes) {
        when(type) {
            TowerTypes.BLOCK -> {
                this.setImageResource(R.drawable.tower_block_1)
            }
            TowerTypes.SLOW -> {
                this.setImageResource(R.drawable.tower_slow_1)
            }
            TowerTypes.AOE -> {
                this.setImageResource(R.drawable.tower_aoe_1)
            }
        }
    }
}