package de.mow2.towerdefense.controller

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.actors.CreepTypes
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes
import java.util.concurrent.ConcurrentHashMap

class BitmapPreloader(val resources: Resources) {

    /**
     * Initialize all images and hold references for further use
     * Should improve performance compared to decoding bitmaps while drawing
     */
    fun preloadImages() {
        preloadTowers()
        preloadCreeps()
    }

    fun preloadTowers() {
        val width = GameManager.playGround.squareSize
        val height = width
        TowerTypes.values().forEach { key ->
            //tower just needs a scaled image
            val towerR: Int
            //weapon needs sprite sheet and frame count
            val weaponAnimR: Int
            val frameCountWeapon: Int
            //projectile gets different scaling and animation values
            val projectileAnimR: Int
            val frameCountProjectile: Int
            val widthProjectile: Int
            val heightProjectile: Int
            //build images and animation maps
            when(key) {
                TowerTypes.BLOCK -> {
                    towerR = R.drawable.tower_block
                    weaponAnimR = R.drawable.tower_block_weapon_anim_1
                    frameCountWeapon = 6
                    projectileAnimR = R.drawable.tower_block_projectile_1
                    frameCountProjectile = 3
                    widthProjectile = 20
                    heightProjectile = 80
                }
                TowerTypes.SLOW -> {
                    towerR = R.drawable.tower_slow
                    weaponAnimR = R.drawable.tower_slow_weapon_anim_1
                    frameCountWeapon = 16
                    projectileAnimR = R.drawable.tower_block_projectile_1 //TODO: replace with correct values for specific projectile
                    frameCountProjectile = 3 //TODO: replace with correct values for specific projectile
                    widthProjectile = 20 //TODO: replace with correct values for specific projectile
                    heightProjectile = 80 //TODO: replace with correct values for specific projectile
                }
                TowerTypes.AOE -> {
                    towerR = R.drawable.tower_aoe
                    weaponAnimR = R.drawable.tower_block_weapon_anim_1
                    frameCountWeapon = 6
                    projectileAnimR = R.drawable.tower_block_projectile_1 //TODO: replace with correct values for specific projectile
                    frameCountProjectile = 3 //TODO: replace with correct values for specific projectile
                    widthProjectile = 20 //TODO: replace with correct values for specific projectile
                    heightProjectile = 80 //TODO: replace with correct values for specific projectile
                }
            }
            towerImages[key] = ScaledImage(resources, width, height * 2, towerR).scaledImage
            weaponAnims[key] = SpriteAnimation(BitmapFactory.decodeResource(resources, weaponAnimR), width, height, 1, frameCountWeapon, 100)
            projectileAnims[key] = SpriteAnimation(BitmapFactory.decodeResource(resources, projectileAnimR), widthProjectile, heightProjectile, 1, frameCountProjectile, 50)
        }
    }

    fun preloadCreeps() {
        val width = GameManager.playGround.squareSize
        val height = width
        CreepTypes.values().forEach { key ->
            val creepR: Int
            var frameCount: Int
            var rowCount = 4 // 4 rows = 4 animation types (0=down, 1=up, 2=right, 3= left), sprite sheet has to be in that order!
            var frameDuration: Int
            when(key) {
                CreepTypes.LEAFBUG -> {
                    creepR = R.drawable.leafbug_anim
                    frameCount = 7
                    frameDuration = 30
                }
                CreepTypes.FIREBUG -> {
                    //TODO: replace with actual fire bug values
                    creepR = R.drawable.leafbug_anim
                    frameCount = 7
                    frameDuration = 30
                }
            }
            creepAnims[key] = SpriteAnimation(BitmapFactory.decodeResource(resources, creepR), width, height, rowCount, frameCount, frameDuration)
        }
    }

    companion object {
        //all various lists and maps for game objects and their respective bitmaps or animations
        var towerImages = ConcurrentHashMap<TowerTypes, Bitmap>()
        var creepAnims = ConcurrentHashMap<CreepTypes, SpriteAnimation>()
        var weaponAnims = ConcurrentHashMap<TowerTypes, SpriteAnimation>()
        var projectileAnims = ConcurrentHashMap<TowerTypes, SpriteAnimation>()
    }
}