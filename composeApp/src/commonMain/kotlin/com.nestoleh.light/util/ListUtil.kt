package com.nestoleh.light.util

fun <T> List<List<T>>.nextElementIndexes(i: Int, j: Int): Pair<Int, Int> {
    if ((i < 0 || i >= this.size) || (j < 0 || j >= this[i].size)) {
        return Pair(-1, -1)
    }

    var nextI = i
    var nextJ = j + 1

    // if we've reached the end of the current row
    if (nextJ >= this[nextI].size) {
        nextJ = 0
        nextI++

        // continue to the next row, wrapping around if necessary
        while (nextI != i) {
            if (nextI >= this.size) {
                nextI = 0
            }
            if (this[nextI].isNotEmpty()) {
                break
            }
            nextI++
        }
    }

    // if we end up back where we started, it means there are no more elements
    if (nextI == i && nextJ == j) {
        return Pair(-1, -1)
    }

    return Pair(nextI, nextJ)
}