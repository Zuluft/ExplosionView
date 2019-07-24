package com.zuluft.lib

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.util.SparseArray
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.util.valueIterator
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

internal class DrawableShardItemsHolder
constructor(
    private val width: Int,
    private val height: Int,
    private val explosionViewSettings: ExplosionViewSettings,
    private val view: WeakReference<View>
) {

    private val drawableShardItems: ArrayList<DrawableShardItem> = ArrayList()
    private val animators: SparseArray<Animator> = SparseArray()
    private val tmpAnimators: SparseArray<Animator> = SparseArray()
    private val random = Random()

    init {
        for (id in 0 until explosionViewSettings.itemCount) {
            val shardItem = createShardItem(id)
            drawableShardItems.add(shardItem)
            animators.put(id, createShardItemAnimator(shardItem))
        }
    }

    fun isAnimRunning(): Boolean {
        val runningAnimatorsCount = animators.valueIterator().asSequence().count {
            it.isRunning
        } + tmpAnimators.valueIterator().asSequence().count {
            it.isRunning
        }
        return runningAnimatorsCount != 0
    }

    private fun createShardItemAnimator(
        drawableShardItem: DrawableShardItem
    ): ObjectAnimator {

        val path = createItemPath(drawableShardItem)
        val animator = ObjectAnimator
            .ofInt(drawableShardItem, DrawableShardItem.X, DrawableShardItem.Y, path)
            .apply {
                duration = drawableShardItem.getSpeed()
                interpolator = LinearInterpolator()
                startDelay = drawableShardItem.getStartDelay()
                repeatMode = ObjectAnimator.RESTART
                repeatCount = ObjectAnimator.INFINITE
            }
        animator.addUpdateListener {
            view.get()?.invalidate()
        }
        return animator
    }


    private fun getRandomMoveFactor(): Float {
        return getRandomNumberInRange(
            explosionViewSettings.minMoveFactor,
            explosionViewSettings.maxMoveFactor
        )
    }


    private fun createItemPath(drawableShardItem: DrawableShardItem): Path {
        return Path().apply {
            var startY = drawableShardItem.getY()
            var startX = drawableShardItem.getX()
            moveTo(startX.toFloat(), startY.toFloat())
            while (isInBounds(startY, drawableShardItem.getHeight())) {
                val moveDistance=getMoveDistance()
                val x2 = getRandomX(drawableShardItem.getScaledWidth())
                val y2 = startY + moveDistance / 2
                val x3 = getRandomX(drawableShardItem.getScaledWidth())
                val y3 = startY + moveDistance
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

    private fun isInBounds(yValue: Int, itemHeight: Int): Boolean {
        return when (explosionViewSettings.spreadDirection) {
            Top -> yValue >= -itemHeight
            else -> yValue <= height + itemHeight
        }
    }

    private fun getMoveDistance(): Int {
        return (height * getRandomMoveFactor() *
                when (explosionViewSettings.spreadDirection) {
                    Top -> -1
                    else -> 1
                }).toInt()
    }

    private fun createShardItem(id: Int): DrawableShardItem {
        val scale = getRandomScale()
        val targetWidth = explosionViewSettings.itemWidth * scale
        val targetHeight = explosionViewSettings.itemHeight * scale
        return DrawableShardItem(
            id,
            explosionViewSettings.drawable.constantState!!
                .newDrawable(explosionViewSettings.context.resources),
            explosionViewSettings.itemWidth,
            explosionViewSettings.itemHeight,
            getRandomX(targetWidth.toInt()),
            getInitialY(targetHeight.toInt()),
            getRandomAlpha(),
            scale,
            getRandomStartDelay(),
            getRandomAnimDuration()
        )
    }

    private fun getRandomAnimDuration(): Long {
        return getRandomNumberInRange(
            explosionViewSettings.minAnimDuration.toFloat(),
            explosionViewSettings.maxAnimDuration.toFloat()
        ).toLong()
    }

    private fun getRandomStartDelay(): Long {
        return getRandomNumberInRange(
            explosionViewSettings.minAnimDelay.toFloat(),
            explosionViewSettings.maxAnimDelay.toFloat()
        ).toLong()
    }

    private fun getInitialY(itemHeight: Int): Int {
        return when (explosionViewSettings.spreadDirection) {
            Top -> height
            else -> -itemHeight
        }
    }

    private fun getRandomScale(): Float {
        return getRandomNumberInRange(
            explosionViewSettings.minScale,
            explosionViewSettings.maxScale
        )
    }

    private fun getRandomAlpha(): Float {
        return getRandomNumberInRange(
            explosionViewSettings.minAlpha,
            explosionViewSettings.maxAlpha
        )
    }

    private fun getRandomX(itemWidth: Int): Int {
        return getRandomNumberInRange(
            explosionViewSettings.horizontalOffset.toFloat(),
            (width - explosionViewSettings.horizontalOffset - itemWidth).toFloat()
        ).toInt()
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
        tmpAnimators.put(shardItem.id,
            createTemporaryAnimator(shardItem).apply {
                start()
            })
    }

    private fun createTemporaryAnimator(shardItem: DrawableShardItem): Animator {
        val path = createItemPath(shardItem)
        val animator = ObjectAnimator
            .ofInt(shardItem, DrawableShardItem.X, DrawableShardItem.Y, path)
            .apply {
                duration = shardItem.getSpeed() // todo compute distance percentage
                interpolator = AccelerateInterpolator()
            }
        animator.addUpdateListener {
            view.get()?.invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                tmpAnimators[shardItem.id]
                    .apply {
                        removeAllListeners()
                        end()
                        cancel()
                    }
                tmpAnimators.remove(shardItem.id)
                animators[shardItem.id].start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        return animator
    }

    fun findShardItemByLocation(x: Int, y: Int): DrawableShardItem? {
        return drawableShardItems.filter {
            it.contains(x, y)
        }.maxBy {
            it.getAlpha()
        }
    }

    fun detachAnim(shardItem: DrawableShardItem) {
        if (tmpAnimators.indexOfKey(shardItem.id) >= 0) {
            tmpAnimators[shardItem.id].apply {
                removeAllListeners()
                cancel()
            }
        }
        animators[shardItem.id].cancel()
    }

    fun draw(canvas: Canvas) {
        drawableShardItems.forEach {
            it.draw(canvas)
        }
    }

}