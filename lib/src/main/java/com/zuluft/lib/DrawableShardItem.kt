package com.zuluft.lib

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable


internal class DrawableShardItem
constructor(
    val id: Int,
    private val drawable: Drawable,
    private val initialWidth: Int,
    private val initialHeight: Int,
    initialX: Int,
    initialY: Int,
    initialAlpha: Float,
    initialScale: Float,
    private val startDelay: Long,
    private val speed: Long
) : ShardItem {

    init {
        setX(initialX)
        setY(initialY)
        setWidth(initialWidth)
        setHeight(initialHeight)
        setAlpha(initialAlpha)
        setScale(initialScale)
    }

    override fun setHeight(height: Int) {
        val bounds = getBounds()
        drawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.top + height)
    }

    override fun setWidth(width: Int) {
        val bounds = getBounds()
        drawable.setBounds(bounds.left, bounds.top, bounds.left + width, bounds.bottom)
    }

    private fun getBounds(): Rect {
        return drawable.bounds
    }

    override fun getScaledHeight(): Int {
        return getBounds().height()
    }

    override fun getScaledWidth(): Int {
        return getBounds().width()
    }

    override fun getSpeed(): Long {
        return speed
    }

    override fun getStartDelay(): Long {
        return startDelay
    }

    override fun getWidth(): Int {
        return initialWidth
    }

    override fun getHeight(): Int {
        return initialHeight
    }

    override fun contains(x: Int, y: Int): Boolean {
        return getBounds().contains(x, y)
    }

    override fun getX(): Int {
        return getBounds().left
    }

    override fun getY(): Int {
        return getBounds().top
    }

    override fun getAlpha(): Float {
        return drawable.alpha / 255f
    }

    override fun getScale(): Float {
        return getBounds().width() / initialWidth.toFloat()
    }

    override fun setX(x: Int) {
        val width = getBounds().width()
        val height = getBounds().height()
        drawable.setBounds(x, getBounds().top, x + width, getBounds().top + height)
    }

    override fun setY(y: Int) {
        val width = getBounds().width()
        val height = getBounds().height()
        drawable.setBounds(getBounds().left, y, getBounds().left + width, y + height)
    }


    override fun setScale(scale: Float) {
        val bounds = getBounds()
        val width = bounds.width()
        val height = bounds.height()
        val targetWidth = (width * scale).toInt()
        val targetHeight = (height * scale).toInt()
        val x = (bounds.left - (targetWidth - width) / 2f).toInt()
        val y = (bounds.top - (targetHeight - height) / 2f).toInt()
        drawable.setBounds(x, y, x + targetWidth, y + targetHeight)
    }


    override fun setAlpha(alpha: Float) {
        drawable.alpha = (255 * alpha).toInt()
    }

    override fun draw(canvas: Canvas) {
        drawable.draw(canvas)
    }

    companion object {
        const val X = "x"
        const val Y = "y"
    }
}