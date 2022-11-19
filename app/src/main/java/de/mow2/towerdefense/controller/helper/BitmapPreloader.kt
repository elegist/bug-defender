package de.mow2.towerdefense.controller.helper

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.GameManager
import de.mow2.towerdefense.model.gameobjects.actors.Enemy.EnemyType
import de.mow2.towerdefense.model.gameobjects.actors.TowerTypes
import java.util.concurrent.ConcurrentHashMap

class BitmapPreloader(val resources: Resources) {

    /**
     * Initialize all images and hold references for further use
     * Should improve performance compared to decoding bitmaps while drawing
     */
    fun preloadImages() {
        preloadTowers()
        preloadEnemies()
    }

    private fun preloadTowers() {
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
                    //tower
                    towerR = R.drawable.tower_block_1
                    //weapon
                    weaponAnimR = R.drawable.tower_block_weapon_anim_1
                    frameCountWeapon = 6
                    //projectile
                    projectileAnimR = R.drawable.tower_block_projectile_1
                    frameCountProjectile = 3
                    widthProjectile = 20
                    heightProjectile = 80
                }
                TowerTypes.SLOW -> {
                    //tower
                    towerR = R.drawable.tower_slow_1
                    //weapon
                    weaponAnimR = R.drawable.tower_slow_weapon_anim_1
                    frameCountWeapon = 16
                    //projectile
                    projectileAnimR = R.drawable.tower_slow_projectile_1
                    frameCountProjectile = 5
                    widthProjectile = 64
                    heightProjectile = 64
                }
                TowerTypes.AOE -> {
                    //tower
                    towerR = R.drawable.tower_aoe_1
                    //weapon
                    weaponAnimR = R.drawable.tower_aoe_weapon_anim_1
                    frameCountWeapon = 8
                    //projectile
                    projectileAnimR = R.drawable.tower_aoe_projectile_1
                    frameCountProjectile = 6
                    widthProjectile = 20
                    heightProjectile = 20
                }
            }
            towerImages[key] = ScaledImage(resources, width, height * 2, towerR).scaledImage
            weaponAnims[key] = SpriteAnimation(BitmapFactory.decodeResource(resources, weaponAnimR), width, height, 1, frameCountWeapon, 100)
            projectileAnims[key] = SpriteAnimation(BitmapFactory.decodeResource(resources, projectileAnimR), widthProjectile, heightProjectile, 1, frameCountProjectile, 100)
        }
    }

    private fun preloadEnemies() {
        val width = GameManager.playGround.squareSize
        val height = width
        EnemyType.values().forEach { key ->
            val enemyR: Int
            val frameCount: Int
            val rowCount = 4 // 4 rows = 4 animation types (0=down, 1=up, 2=right, 3= left), sprite sheet has to be in that order!
            val frameDuration: Int
            when(key) {
                EnemyType.LEAFBUG -> {
                    enemyR = R.drawable.leafbug_anim
                    frameCount = 7
                    frameDuration = 45
                }
                EnemyType.FIREBUG -> {
                    //TODO: replace with actual fire bug values
                    enemyR = R.drawable.leafbug_anim
                    frameCount = 7
                    frameDuration = 30
                }
                EnemyType.MAGMACRAB -> {
                    enemyR = R.drawable.magacrab_anim
                    frameCount = 8
                    frameDuration = 60
                }
                EnemyType.SKELETONKNIGHT -> {
                    //TODO: replace with skeletonknight_anim
                    enemyR = R.drawable.magacrab_anim
                    frameCount = 8
                    frameDuration = 30
                }
                EnemyType.SKELETONKING -> {
                    enemyR = R.drawable.skeletonking_anim
                    frameCount = 10
                    frameDuration = 120
                }
            }
            enemyAnims[key] = SpriteAnimation(BitmapFactory.decodeResource(resources, enemyR), width, height, rowCount, frameCount, frameDuration)
        }
    }

    companion object {
        //all various lists and maps for game objects and their respective bitmaps or animations
        var towerImages = ConcurrentHashMap<TowerTypes, Bitmap>()
        var enemyAnims = ConcurrentHashMap<EnemyType, SpriteAnimation>()
        var weaponAnims = ConcurrentHashMap<TowerTypes, SpriteAnimation>()
        var projectileAnims = ConcurrentHashMap<TowerTypes, SpriteAnimation>()
    }
}