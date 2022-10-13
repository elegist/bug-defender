package de.mow2.towerdefense.controller


import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import de.mow2.towerdefense.model.actors.Tower
import de.mow2.towerdefense.model.actors.TowerTypes
import de.mow2.towerdefense.model.playground.PlayGround
import de.mow2.towerdefense.model.playground.SquareField

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {
    private var gameLoop: GameLoop
    private var playGround: PlayGround
    private var tower: Tower? = null



    //flag to block comparing coordinates (when construction menu is open)
    private var blockInput = false
    private lateinit var selectedSquare: SquareField

    init {
        holder.addCallback(this)
        gameLoop = GameLoop(this, holder)
        playGround = PlayGround(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels, resources)
    }

    /**
     * Use surfaceCreated to initialize game objects, field and so on...
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        //start game loop
        gameLoop.setRunning(true)
        gameLoop.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                gameLoop.setRunning(false)
                gameLoop.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            retry = false
        }
    }

    /**
     * method to update game objects data
     */
    fun update() {
    }

    /**
     * method to draw on canvas
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        //drawing playground
        playGround.squareArray.forEach { it.drawField(canvas) }

        // create test creep
        GameManager.createCreep(playGround.squareArray[0])

        //draw sprites
        GameManager.drawObjects(canvas, resources)
    }

    /**
     * handling user inputs
     */
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        var x: Float; var y: Float
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                x = ev.x
                y = ev.y
                invalidate()
                blockInput = if(!blockInput) {
                    selectedSquare = selectSquare(x, y)

                    //if square is not start or end row of map
                    if(selectedSquare.mapPos["y"] in 1 until GameManager.squaresY - 1) {
                        //lock square
                        selectedSquare.lockSquare()
                    }
                    // TODO: öffne baumenü an gegebenen koordinaten
                    // TODO: Prüfen ob schon ein Turm an gegebener Stelle, wenn Ja: Öffne Upgrademenü, wenn Nein: Turm löschen?

                    true
                } else {
                    //if user clicks on already selected square build tower, else unlock square
                    if(selectedSquare == selectSquare(x, y)) {
                        GameManager.buildTower(selectedSquare)
                    } else {
                        selectedSquare.unlockSquare()
                    }

                    // TODO: schließe baumenü bzw. baue turm oder was auch immer
                    // TODO: Prüfe ob Spieler auf Baumenü geklickt hat, wenn ja: Baue Turm, wenn nein: schließe Baumenü
                    false
                }
            }
            MotionEvent.ACTION_MOVE -> {}

            MotionEvent.ACTION_UP -> {
                x = ev.x
                y = ev.y
                invalidate()
                // TODO: Prüfe ob "Loslassen" gleiche Koordinaten hat wie drücken, wenn Nein: nichts tun
            }
        }
        return true
    }

    private fun selectSquare(x: Float, y: Float): SquareField {
        var indexOfSelected = 0
        playGround.squareArray.forEachIndexed { i, it ->
            val coordRangeX = it.coordX..(it.coordX+it.width)
            val coordRangeY = it.coordY..(it.coordY+it.height)
            if(x in coordRangeX && y in coordRangeY) {
                indexOfSelected = i
            }
        }
        return playGround.squareArray[indexOfSelected]
    }
}