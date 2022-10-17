package de.mow2.towerdefense.controller




class GameLoop : Thread() {
    private var running = false
    private var avgUps: Double = 0.0

    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }

    override fun run() {
        var startTime: Long = System.currentTimeMillis()
        var elapsedTime: Long
        var waitTime: Long
        var updateCount = 0

        /* Game Loop */
        while (running) {
            GameManager.updateLogic()
            updateCount++

            //pause gameLoop if targetUPS could be exceeded
            elapsedTime = System.currentTimeMillis() - startTime
            waitTime = ((updateCount * targetTime) - elapsedTime).toLong()
            if (waitTime > 0) {
                try {
                    sleep(waitTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            //calc avg ups and fps
            elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime >= 1000) {
                avgUps = (updateCount) / (1E-3 * elapsedTime)
                updateCount = 0
                startTime = System.currentTimeMillis()
                //Log.i("UpsMonitor", "UPS:${avgUps}")
            }
        }
    }
    companion object {
        const val targetUPS = 30
        const val targetTime: Double = 1E+3 / targetUPS
    }
}