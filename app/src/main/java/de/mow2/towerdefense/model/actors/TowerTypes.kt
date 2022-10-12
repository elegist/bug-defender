package de.mow2.towerdefense.model.actors

/**
 * List of all tower types.
 * Could be used to obtain tower attributes (health, powers, and so on)
 * Currently in use to decide which Bitmap will be drawn
 */
enum class TowerTypes {
    BLOCK
}

//TODO: expand list with new tower types, deposit bitmaps for towers, link them in GameManager