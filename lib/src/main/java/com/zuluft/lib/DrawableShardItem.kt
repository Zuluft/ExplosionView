package com.zuluft.lib

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable


internal class DrawableShardItem
constructor(
        val id: Int,
        private val bitmapDrawable: BitmapDrawable
) : ShardItem {
    override fun contains(x: Int, y: Int): Boolean {
        return bitmapDrawable.bounds.contains(x, y)
    }

    private val originalBounds: Rect = bitmapDrawable.bounds

    override fun getX(): Int {
        return bitmapDrawable.bounds.left
    }

    override fun getY(): Int {
        return bitmapDrawable.bounds.top
    }

    override fun getAlpha(): Float {
        return bitmapDrawable.alpha / 255f
    }

    override fun getScale(): Float {
        return bitmapDrawable.bounds.width() / originalBounds.width().toFloat()
    }

    override fun setX(x: Int) {
        val bounds = bitmapDrawable.bounds
        val width = bounds.width()
        val height = bounds.height()
        bitmapDrawable.setBounds(x, bounds.top, x + width, bounds.top + height)
    }

    override fun setY(y: Int) {
        val bounds = bitmapDrawable.bounds
        val width = bounds.width()
        val height = bounds.height()
        bitmapDrawable.setBounds(bounds.left, y, bounds.left + width, y + height)
    }


    override fun setScale(scale: Float) {
        val bounds = bitmapDrawable.bounds
        val width = bounds.width()
        val height = bounds.height()
        val targetWidth = (width * scale).toInt()
        val targetHeight = (height * scale).toInt()
        val x = (bounds.left - (targetWidth - width) / 2f).toInt()
        val y = (bounds.top - (targetHeight - height) / 2f).toInt()
        bitmapDrawable.setBounds(x, y, x + targetWidth, y + targetHeight)
    }


    override fun setAlpha(alpha: Float) {
        bitmapDrawable.alpha = (255 * alpha).toInt()
    }

    override fun draw(canvas: Canvas) {
        bitmapDrawable.draw(canvas)
    }

    companion object {
        const val X = "x"
        const val Y = "y"
    }
}