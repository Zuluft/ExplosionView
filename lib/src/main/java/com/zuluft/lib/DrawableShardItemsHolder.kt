package com.zuluft.lib

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.util.SparseArray
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.util.valueIterator
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

internal class DrawableShardItemsHolder
constructor(private val width: Int,
            private val height: Int,
            private val explosionViewSettings: ExplosionViewSettings,
            private val view: WeakReference<View>) {

    private val drawableShardItems: ArrayList<DrawableShardItem> = ArrayList()
    private val animators: SparseArray<Animator> = SparseArray()
    private val random = Random()

    init {
        for (i in 0 until explosionViewSettings.itemCount) {
            val shardItem = createBitmapShardItem(i)
            drawableShardItems.add(shardItem)
            animators.put(i, createShardItemAnimator(shardItem,
                    getRandomNumberInRange(0f,
                            explosionViewSettings.animDuration.toFloat()).toLong()))
        }

    }

    fun isAnimRunning(): Boolean {
        return false
    }

    private fun createShardItemAnimator(drawableShardItem: DrawableShardItem,
                                        delayMillis: Long): ObjectAnimator {
        val path = createItemPath(drawableShardItem)
        val animator = ObjectAnimator
                .ofInt(drawableShardItem, DrawableShardItem.X, DrawableShardItem.Y, path)
                .apply {
                    duration = explosionViewSettings.animDuration
                    interpolator = LinearInterpolator()
                    startDelay = delayMillis
                    repeatMode = ObjectAnimator.RESTART
                    repeatCount = ObjectAnimator.INFINITE
                }
        animator.addUpdateListener {
            view.get()?.invalidate()
        }
        return animator
    }


    private fun createItemPath(drawableShardItem: DrawableShardItem): Path {
        return Path().apply {
            var startY = drawableShardItem.getY()
            var startX = drawableShardItem.getX()
            moveTo(startX.toFloat(), startY.toFloat())
            while (isInBounds(startY)) {
                val x2 = getRandomX()
                val y2 = startY + getMoveDistance() / 2
                val x3 = getRandomX()
                val y3 = startY + getMoveDistance()
                cubicTo(
                        startX.toFloat(),
                        startY.toFloat(),
                        x2.toFloat(),
                        y2.toFloat(),
                        x3.toFloat(),
                        y3.toFloat()
                )
                startY = y3
                startX = x3
            }
        }
    }

    private fun isInBounds(value: Int): Boolean {
        return when (explosionViewSettings.spreadDirection) {
            Top -> value >= 0
            else -> value <= height
        }
    }

    private fun getMoveDistance(): Int {
        return (height * explosionViewSettings.moveFactor *
                when (explosionViewSettings.spreadDirection) {
                    Top -> -1
                    else -> 1
                }).toInt()
    }

    private fun createBitmapShardItem(id: Int): DrawableShardItem {
        return DrawableShardItem(
                id,
                BitmapDrawable(
                        explosionViewSettings.context.resources,
                        explosionViewSettings.drawable.bitmap
                )
                        .apply {
                            val x = getRandomX()
                            val y = getInitialY()
                            setBounds(
                                    x,
                                    y,
                                    x + explosionViewSettings.itemSize,
                                    y + explosionViewSettings.itemSize
                            )
                        })
                .apply {
                    setScale(getRandomScale())
                    setX(getRandomX())
                    setY(getInitialY())
                    setAlpha(getRandomAlpha())
                }
    }

    private fun getInitialY(): Int {
        return when (explosionViewSettings.spreadDirection) {
            Top -> height + explosionViewSettings.itemSize
            else -> -explosionViewSettings.itemSize
        }
    }

    private fun getRandomScale(): Float {
        return getRandomNumberInRange(explosionViewSettings.minScale,
                explosionViewSettings.maxScale)
    }

    private fun getRandomAlpha(): Float {
        return getRandomNumberInRange(explosionViewSettings.minAlpha,
                explosionViewSettings.maxAlpha)
    }

    private fun getRandomX(): Int {
        return getRandomNumberInRange(
                0f,
                width.toFloat()
        )
                .toInt()
    }


    private fun getRandomNumberInRange(start: Float, end: Float): Float {
        return random.nextFloat() * (end - start) + start
    }

    fun startAnim() {
        animators.valueIterator().forEach {
            it.start()
        }

    }

    fun endAnim() {
        animators.valueIterator().forEach {
            it.end()
        }
    }

    fun attachAnim(shardItem: DrawableShardItem) {
        animators.put(shardItem.id,
                createShardItemAnimator(shardItem, 0L).apply {
                    start()
                })
    }

    fun findShardItemByLocation(x: Int, y: Int): DrawableShardItem? {
        return drawableShardItems.filter {
            it.contains(x, y)
        }.maxBy {
            it.getAlpha()
        }
    }

    fun detachAnim(shardItem: DrawableShardItem) {
        animators[shardItem.id]
                .apply {
                    removeAllListeners()
                    end()
                    cancel()
                }
        animators.removeAt(shardItem.id)
    }

    fun draw(canvas: Canvas) {
        drawableShardItems.forEach {
            it.draw(canvas)
        }
    }

}