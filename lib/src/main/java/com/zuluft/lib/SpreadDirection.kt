package com.zuluft.lib


sealed class SpreadDirection(value: Int)
object Top : SpreadDirection(0)
object Bottom : SpreadDirection(1)

fun getSpreadDirection(id: Int): SpreadDirection {
    return when (id) {
        0 -> Top
        else -> Bottom
    }
}