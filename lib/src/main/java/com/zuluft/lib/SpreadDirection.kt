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