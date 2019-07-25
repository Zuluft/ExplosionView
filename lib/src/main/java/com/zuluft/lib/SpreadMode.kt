package com.zuluft.lib


sealed class SpreadMode
object Unidirectional : SpreadMode()
object Bidirectional : SpreadMode()

fun getSpreadMode(id: Int): SpreadMode {
    return when (id) {
        0 -> Unidirectional
        else -> Bidirectional
    }
}