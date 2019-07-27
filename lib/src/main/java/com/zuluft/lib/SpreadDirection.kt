package com.zuluft.lib


sealed class SpreadDirection
object Top : SpreadDirection()
object Bottom : SpreadDirection()

fun getSpreadDirection(id: Int): SpreadDirection {
    return when (id) {
        0 -> Top
        else -> Bottom
    }
}

fun getOppositeSpreadDirection(spreadDirection: SpreadDirection): SpreadDirection {
    return when (spreadDirection) {
        Top -> Bottom
        else -> Top
    }
}