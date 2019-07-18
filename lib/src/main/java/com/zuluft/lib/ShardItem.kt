package com.zuluft.lib

import android.graphics.Canvas
import android.graphics.Point


interface ShardItem {

    fun setX(x: Int)

    fun setY(y: Int)

    fun setAlpha(alpha: Float)

    fun setScale(scale: Float)

    fun draw(canvas: Canvas)
}