package de.mow2.towerdefense.model.pathfinding

import java.util.PriorityQueue

class astar {
    fun FindPath(startNode: Node, targetNode: Node): MutableList<Node>? {
        val openSet = PriorityQueue<Node>()
        val closedSet = mutableSetOf<Node>()

        openSet.add(startNode)

        return null

    }
}

//node represents a single coordinate on the playground
data class Node(val x: Int, val y: Int) : Comparable<Node> {
    // g = distance from selected node to start node
    val g: Int = 0

    // h = distance from selected node to end node -> optimistic assignment: either equal or less than real distance
    var h: Int = 0

    // f = g + h -> the lower the value the more attractive it is as a path option
    val f: Int = g + h

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