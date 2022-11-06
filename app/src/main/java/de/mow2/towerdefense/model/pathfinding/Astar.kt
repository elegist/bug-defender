package de.mow2.towerdefense.model.pathfinding

import android.util.Log
import de.mow2.towerdefense.controller.GameManager
import kotlin.math.*

/**
 * Astar search algorithm, used by creeps to find the fastest way across the map
 */
class Astar {
    fun findPath(startNode: Node, targetNode: Node, playGroundRows: Int, playGroundCols: Int): MutableSet<Node>? {
        val openSet = mutableSetOf<Node>()
        val closedSet = mutableSetOf<Node>()
        openSet.add(startNode)

        while (openSet.any()) {
            var currentNode = openSet.first()

            for(i in openSet) {
                if(i.f < currentNode.f) {
                    currentNode = i
                }
            }

            openSet.remove(currentNode)
            closedSet.add(currentNode)

            if (currentNode == targetNode) {
                val path = mutableSetOf<Node>()
                var tempNode = currentNode
                while (tempNode.parent != null) {
                    path.add(tempNode)
                    tempNode = tempNode.parent!!
                }
                return path
            }

            val neighbors = currentNode.getNeighbors(playGroundRows, playGroundCols)

            neighbors.forEach neighbors@{ node ->

                if (GameManager.playGround.squareArray[node.x][node.y].isBlocked) {
                    return@neighbors
                }

                for(closed_child in closedSet){
                    if(closed_child.x == node.x && closed_child.y == node.y) {
                        return@neighbors
                    }
                }

                node.parent = currentNode
                node.g = currentNode.g + 1
                node.h = calculateHeuristics(node, targetNode)
                node.f = node.g + node.h

                openSet.forEach { openNode ->
                    if(node == openNode && node.g > openNode.g) {
                        return@neighbors
                    }
                }
                openSet.add(node)
            }
        }
        return null
    }

    /**
     * Represents a node on the playground holding important information for the algorithm to work
     * @param x horizontal position
     * @param y vertical position
     */
    data class Node(val x: Int, val y: Int) : Comparable<Node> {
        // parent is the node that came previous to the current one
        var parent: Node? = null
        // g = distance from selected node to start node
        var g: Int = 0
        // h = distance from selected node to end node -> optimistic assignment: either equal or less than real distance
        var h: Int = 0
        // f = g + h -> the lower the value the more attractive it is as a path option
        var f: Int = g + h

        /**
         * returns all neighbor nodes of this node in a MutableSet
         * @param maxRows max rows on the 2D grid
         * @param maxCols max columns on the 2D grid
         * @return neighbors
         */
        fun getNeighbors(maxRows: Int, maxCols: Int): MutableSet<Node> {
            val neighbors = mutableSetOf<Node>()

            //left
            if (x - 1 >= 0) neighbors.add(Node(x - 1, y))
            //top
            if (y + 1 < maxCols) neighbors.add(Node(x, y + 1))
            //right
            if (x + 1 < maxRows) neighbors.add(Node(x + 1, y))
            //bottom
            if (y - 1 >= 0) neighbors.add(Node(x, y - 1))

            //diagonal bottom left
            if (x - 1 > 0 && y - 1 > 0) neighbors.add(Node(x - 1, y - 1))
            //diagonal top left
            if (x - 1 > 0 && y + 1 < maxCols) neighbors.add(Node(x - 1, y + 1))
            //diagonal bottom right
            if (x + 1 < maxRows && y - 1 > 0) neighbors.add(Node(x + 1, y - 1))
            //diagonal top right
            if (x + 1 < maxRows && y + 1 < maxCols) neighbors.add(Node(x + 1, y + 1))

            return neighbors
        }

        override fun compareTo(other: Node): Int = this.f.compareTo(other.f)
    }

    /**
     * Calculates the correct values to decide which way should be preferred (straight or diagonal).
     * 8 directions, diagonal cost and straight cost are the same.
     */
    private fun calculateHeuristics(from: Node, to: Node): Int {
        val xDist = abs(from.x - to.x)
        val yDist = abs(from.y - to.y)

        return max(xDist, yDist)
    }
}