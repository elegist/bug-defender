package de.mow2.towerdefense.model.pathfinding

import java.util.PriorityQueue
import kotlin.math.abs

class Astar {
    fun findPath(startNode: Node, targetNode: Node, playGroundRows: Int, playGroundCols: Int): MutableSet<Node>? {
        val openSet = PriorityQueue<Node>()
        val closedSet = mutableSetOf<Node>()

        openSet.add(startNode)

        while (openSet.any()) {
            val currentNode = openSet.first()
            openSet.remove(currentNode)
            closedSet.add(currentNode)

            if (currentNode == targetNode) {
                val path = mutableSetOf<Node>()
                var tempNode = currentNode
                while (tempNode.parent != null) {
                    path.add(tempNode.parent!!)
                    tempNode = tempNode.parent!!
                }
                return path
            }

            val neighbors = currentNode.getNeighbors(playGroundRows, playGroundCols)

            neighbors.forEach {
                if (!closedSet.contains(it)) {
                    val tempG = currentNode.g + 1

                    if (openSet.contains(it)) {
                        if (tempG < it.g) {
                            it.g = tempG
                        }
                    } else {
                        it.g = tempG
                        openSet.add(it)
                    }

                    it.h = abs(it.x - targetNode.x) + abs(it.y - targetNode.y)
                    it.f = it.g + it.h

                    it.parent = currentNode
                }
            }
        }

        return null
    }

    //node represents a single coordinate on the playground holding important information for the algorithm to work
    data class Node(val x: Int, val y: Int) : Comparable<Node> {
        // parent is the node that came previous to the current one
        var parent: Node? = null

        // g = distance from selected node to start node
        var g: Int = 0

        // h = distance from selected node to end node -> optimistic assignment: either equal or less than real distance
        var h: Int = 0

        // f = g + h -> the lower the value the more attractive it is as a path option
        var f: Int = g + h

        fun getNeighbors(maxRows: Int, maxCols: Int): MutableSet<Node> {
            val neighbors = mutableSetOf<Node>()

            if (x - 1 >= 0) neighbors.add(Node(x - 1, y))
            if (x + 1 < maxCols) neighbors.add(Node(x + 1, y))
            if (y - 1 >= 0) neighbors.add(Node(x, y - 1))
            if (y + 1 < maxRows) neighbors.add(Node(x, y + 1))

            return neighbors
        }

        override fun compareTo(other: Node): Int {
            if (this.x > other.x) return 1
            if (this.x < other.x) return -1
            if (this.y > other.y) return 1
            if (this.y < other.y) return -1

            return 0
        }
    }
}

