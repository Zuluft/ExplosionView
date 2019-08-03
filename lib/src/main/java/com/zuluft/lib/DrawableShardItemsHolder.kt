package com.zuluft.lib

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.util.SparseArray
import android.view.View
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
            animators.put(
                id, createShardItemAnimator(
                    shardItem, if (isInBidirectionalMode() &&
                        shouldChangeDirectionInBidirectionalMode(id)
                    ) {
                        getOppositeSpreadDirection(explosionViewSettings.spreadDirection)
                    } else {
                        explosionViewSettings.spreadDirection
                    }
                )
            )
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

    fun endScene() {
        animators.valueIterator().forEach {
            it.end()
            it.cancel()
        }
        tmpAnimators.valueIterator().forEach {
            it.end()
            it.cancel()
        }
    }

    private fun createShardItemAnimator(
        drawableShardItem: DrawableShardItem,
        spreadDirection: SpreadDirection
    ): ObjectAnimator {

        val path = createItemPath(drawableShardItem, spreadDirection)
        val animator = ObjectAnimator
            .ofInt(drawableShardItem, DrawableShardItem.X, DrawableShardItem.Y, path)
            .apply {
                duration = drawableShardItem.getSpeed()
                interpolator = LinearInterpolator()
                startDelay = drawableShardItem.getStartDelay()
                repeatMode = ObjectAnimator.RESTART
                repeatCount = explosionViewSettings.repeatCount
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


    private fun createItemPath(
        drawableShardItem: DrawableShardItem,
        spreadDirection: SpreadDirection
    ): Path {
        return Path().apply {
            var startY = drawableShardItem.getY()
            var startX = drawableShardItem.getX()
            moveTo(startX.toFloat(), startY.toFloat())
            while (isInBounds(startY, drawableShardItem.getHeight(), spreadDirection)) {
                val moveDistance = getMoveDistance(spreadDirection)
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

    private fun isInBounds(
        yValue: Int, itemHeight: Int,
        spreadDirection: SpreadDirection
    ): Boolean {
        return when (spreadDirection) {
            Top -> yValue >= -itemHeight
            else -> yValue <= height + itemHeight
        }
    }

    private fun getMoveDistance(spreadDirection: SpreadDirection): Int {
        return (height * getRandomMoveFactor() *
                when (spreadDirection) {
                    Top -> -1
                    else -> 1
                }).toInt()
    }

    private fun createShardItem(id: Int): DrawableShardItem {
        val scale = getRandomScale()
        val targetWidth = explosionViewSettings.itemWidth * scale
        val offsetHeight = if (scale >= 1f)
            explosionViewSettings.itemHeight * scale
        else {
            explosionViewSettings.itemHeight.toFloat()
        }
        return DrawableShardItem(
            id,
            explosionViewSettings.drawable.constantState!!
                .newDrawable(explosionViewSettings.context.resources),
            explosionViewSettings.itemWidth,
            explosionViewSettings.itemHeight,
            getRandomX(targetWidth.toInt()),
            getInitialY(
                offsetHeight.toInt(), if (isInBidirectionalMode() &&
                    shouldChangeDirectionInBidirectionalMode(id)
                ) {
                    getOppositeSpreadDirection(explosionViewSettings.spreadDirection)
                } else {
                    explosionViewSettings.spreadDirection
                }
            ),
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

    private fun getInitialY(itemHeight: Int, spreadDirection: SpreadDirection): Int {
        return when (spreadDirection) {
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
            it.cancel()
        }
        tmpAnimators.valueIterator().forEach {
            it.end()
            it.cancel()
        }
    }

    fun attachAnim(shardItem: DrawableShardItem, restartAnim: Boolean) {
        tmpAnimators.put(shardItem.id,
            createTemporaryAnimator(shardItem, restartAnim).apply {
                start()
            })
    }

    private fun getYDistancePercentage(
        yValue: Float,
        spreadDirection: SpreadDirection
    ): Float {
        val percentage = yValue / height
        return when (spreadDirection) {
            Top -> 1f - percentage
            else -> percentage
        }
    }

    private fun shouldChangeDirectionInBidirectionalMode(shardItemId: Int): Boolean {
        return shardItemId % 2 == 0
    }

    private fun isInBidirectionalMode(): Boolean {
        return explosionViewSettings.spreadMode == Bidirectional
    }

    private fun getRelativeSpeedOfAnimator(shardItem: DrawableShardItem): Long {
        val relativeSpeed = (shardItem.getSpeed() * (1f -
                getYDistancePercentage(
                    shardItem.getY().toFloat(),
                    if (isInBidirectionalMode() &&
                        shouldChangeDirectionInBidirectionalMode(shardItem.id)
                    ) {
                        getOppositeSpreadDirection(explosionViewSettings.spreadDirection)
                    } else {
                        explosionViewSettings.spreadDirection
                    }
                ))).toLong()
        return when (relativeSpeed >= 0) {
            true -> relativeSpeed
            else -> 0
        }
    }

    private fun createTemporaryAnimator(
        shardItem: DrawableShardItem,
        startMainAnimatorWhenFinished: Boolean
    ): Animator {
        val path = createItemPath(
            shardItem,
            if (isInBidirectionalMode() &&
                shouldChangeDirectionInBidirectionalMode(shardItem.id)
            ) {
                getOppositeSpreadDirection(explosionViewSettings.spreadDirection)
            } else {
                explosionViewSettings.spreadDirection
            }
        )
        val animator = ObjectAnimator
            .ofInt(shardItem, DrawableShardItem.X, DrawableShardItem.Y, path)
            .apply {
                duration = getRelativeSpeedOfAnimator(shardItem)
                interpolator = LinearInterpolator()
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
                if (startMainAnimatorWhenFinished) {
                    val anim = animators[shardItem.id]
                    decreseaAnimatorRepeatCount(anim)
                    anim.start()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        return animator
    }

    fun decreseaAnimatorRepeatCount(animator: Animator) {
        val anim = animator as ObjectAnimator
        val currentRepeatCount = anim.repeatCount
        if (currentRepeatCount != ObjectAnimator.INFINITE) {
            anim.repeatCount = currentRepeatCount - 1
        }
    }

    fun endAnimSmoothly() {
        drawableShardItems.forEach {
            detachAnim(it)
            attachAnim(it, false)
        }
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