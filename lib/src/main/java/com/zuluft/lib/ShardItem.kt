package com.zuluft.lib

import android.graphics.Canvas


internal interface ShardItem {

    fun setX(x: Int)

    fun setY(y: Int)

    fun setAlpha(alpha: Float)

    fun setScale(scale: Float)

    fun setWidth(width:Int)

    fun setHeight(height:Int)

    fun draw(canvas: Canvas)

    fun getX(): Int

    fun getY(): Int

    fun getAlpha(): Float

    fun getScale(): Float

    fun getHeight(): Int

    fun getWidth(): Int

    fun getScaledHeight():Int

    fun getScaledWidth():Int

    fun getSpeed():Long

    fun getStartDelay():Long

    fun contains(x: Int, y: Int): Boolean
}