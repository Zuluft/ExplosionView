package com.zuluft.lib

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList


class ExplosionView :
        View, ViewTreeObserver.OnPreDrawListener {
    private val animationDuration: Long

    private val itemSize: Int

    private val shardItemsCount: Int
    private val minScale: Float

    private val maxScale: Float
    private val moveFactor: Float

    private val horizontalOffset: Int

    private val spreadDirection: SpreadDirection

    private val random = Random()

    private val bitmapDrawable: BitmapDrawable

    private val itemsToMove: ArrayList<BitmapShardItem> = ArrayList()

    private var anim: AnimatorSet? = null

    private var shouldStartAnim = false

    constructor(context: Context) : this(context, null)


    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        val typedValue = TypedValue()
        val defaultItemSize = resources.getDimensionPixelSize(R.dimen.defaultItemSize)
        val defaultShardItemsCount = resources.getInteger(R.integer.defaultShardItemsCount)
        resources.getValue(R.dimen.defaultMoveFactor, typedValue, true)
        val defaultMoveFactor = typedValue.float
        val defaultAnimDuration = resources.getInteger(R.integer.defaultAnimDuration)
        resources.getValue(R.dimen.defaultMinScale, typedValue, true)
        val defaultMinScale = typedValue.float
        resources.getValue(R.dimen.defaultMaxScale, typedValue, true)
        val defaultMaxScale = typedValue.float
        val defaultHorizontalOffset = resources
                .getDimensionPixelSize(R.dimen.defaultHorizontalOffset)
        val defaultSpreadDirection = resources
                .getInteger(R.integer.defaultSpreadDirection)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExplosionView)
        itemSize = typedArray.getDimensionPixelSize(
                R.styleable.ExplosionView_itemSize,
                defaultItemSize)
        shardItemsCount = typedArray.getInteger(
                R.styleable.ExplosionView_shardItemsCount,
                defaultShardItemsCount)
        moveFactor = typedArray.getFloat(R.styleable.ExplosionView_moveFactor, defaultMoveFactor)
        animationDuration = typedArray.getInt(
                R.styleable.ExplosionView_animDuration,
                defaultAnimDuration).toLong()
        minScale = typedArray.getFloat(R.styleable.ExplosionView_minScale, defaultMinScale)
        maxScale = typedArray.getFloat(R.styleable.ExplosionView_maxScale, defaultMaxScale)
        horizontalOffset = typedArray.getDimensionPixelSize(
                R.styleable.ExplosionView_horizontalOffset,
                defaultHorizontalOffset)
        spreadDirection = getSpreadDirection(
                if (typedArray.hasValue(R.styleable.ExplosionView_spreadDirection)) {
                    typedArray.getInt(R.styleable.ExplosionView_spreadDirection,
                            defaultSpreadDirection)
                } else {
                    defaultSpreadDirection
                }
        )
        bitmapDrawable = (if (typedArray.hasValue(R.styleable.ExplosionView_shardDrawable)) {
            typedArray.getDrawable(R.styleable.ExplosionView_shardDrawable)
        } else {
            ContextCompat.getDrawable(context, R.drawable.default_shard_drawable)
        }) as? BitmapDrawable
                ?: throw IllegalStateException("ShardDrawable should be BitmapDrawable")
        if (minScale > maxScale) {
            throw IllegalStateException("minScale should be less then maxScale")
        }
        if (moveFactor <= 0f) {
            throw IllegalStateException("moveFactor should be positive float")
        }
        typedArray.recycle()
        viewTreeObserver.addOnPreDrawListener(this)
    }

    override fun onPreDraw(): Boolean {
        for (i in 0 until shardItemsCount) {
            itemsToMove.add(createBitmapShardItem())
        }
        val animators = itemsToMove.map {
            getItemAnimator(it)
        }
        anim = AnimatorSet()
        anim!!.playTogether(animators)
        if (shouldStartAnim) {
            anim!!.start()
        }
        viewTreeObserver.removeOnPreDrawListener(this)
        return false
    }

    private fun createBitmapShardItem(): BitmapShardItem {
        return BitmapShardItem(BitmapDrawable(resources, bitmapDrawable.bitmap)
                .apply {
                    setBounds(0, 0, itemSize, itemSize)
                })
                .apply {
                    setScale(getRandomScale())
                    setX(getRandomX())
                    setY(getInitialY())
                }
    }

    private fun getInitialY(): Int {
        return when (spreadDirection) {
            Top -> height + itemSize
            else -> -itemSize
        }
    }

    private fun getRandomScale(): Float {
        return random.nextFloat() * (maxScale - minScale) + minScale
    }

    private fun getRandomX(): Int {
        return (random.nextFloat() * (width - horizontalOffset - itemSize) + horizontalOffset).toInt()
    }

    private fun createItemPath(): Path {
        return Path().apply {
            var startY = getInitialY()
            var startX = getRandomX()
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
        return when (spreadDirection) {
            Top -> value >= 0
            else -> value <= height
        }
    }

    private fun getMoveDistance(): Int {
        return (height * moveFactor * when (spreadDirection) {
            Top -> -1
            else -> 1
        }).toInt()
    }

    private fun getItemAnimator(shardItem: BitmapShardItem): Animator {
        val path = createItemPath()
        val animator = ObjectAnimator
                .ofInt(shardItem, BitmapShardItem.X, BitmapShardItem.Y, path)
                .apply {
                    duration = animationDuration
                    interpolator = LinearInterpolator()
                    startDelay = (random.nextFloat() * animationDuration).toLong()
                    repeatMode = ObjectAnimator.RESTART
                    repeatCount = ObjectAnimator.INFINITE
                }
        animator.addUpdateListener {
            invalidate()
        }
        return animator
    }

    fun explode() {
        if (anim == null) {
            shouldStartAnim = true
        } else if (!anim!!.isRunning) {
            anim!!.start()
        }
    }

    fun collapse() {
        if (anim != null && anim!!.isRunning) {
            anim!!.removeAllListeners()
            anim!!.end()
            shouldStartAnim = false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (anim != null) {
            anim!!.cancel()
            anim = null
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAnimRunning = anim != null && anim!!.isRunning
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            shouldStartAnim = state.isAnimRunning
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
        var isAnimRunning: Boolean = false

        constructor(source: Parcel) : super(source) {
            isAnimRunning = source.readByte().toInt() != 0
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte((if (isAnimRunning) 1 else 0).toByte())
        }

        @Suppress("unused")
        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        itemsToMove.forEach {
            it.draw(canvas)
        }
    }

}
