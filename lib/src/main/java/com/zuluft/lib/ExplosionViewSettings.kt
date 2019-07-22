package com.zuluft.lib

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat

class ExplosionViewSettings private constructor(builder: Builder) {

    val context: Context
    val itemSize: Int
    val itemCount: Int
    val animDuration: Long
    val minScale: Float
    val maxScale: Float
    val minAlpha: Float
    val maxAlpha: Float
    val moveFactor: Float
    val horizontalOffset: Int
    val spreadDirection: SpreadDirection
    val drawable: BitmapDrawable

    companion object {
        fun builder(context: Context): Builder {
            return Builder(context)
        }
        fun builder(context: Context, attrs: AttributeSet?): Builder {
            return Builder(context, attrs)
        }
    }

    init {
        context = builder.context
        itemSize = builder.itemSize
        itemCount = builder.itemCount
        animDuration = builder.animDuration
        minScale = builder.minScale
        maxScale = builder.maxScale
        minAlpha = builder.minAlpha
        maxAlpha = builder.maxAlpha
        moveFactor = builder.moveFactor
        horizontalOffset = builder.horizontalOffset
        spreadDirection = builder.spreadDirection
        drawable = builder.drawable
        if (minScale > maxScale) {
            throw IllegalStateException("minScale should be less then maxScale")
        }
        if (minAlpha > maxAlpha) {
            throw IllegalStateException("minAlpha should be less then maxAlpha")
        }
        if (moveFactor <= 0f) {
            throw IllegalStateException("moveFactor should be positive float")
        }
    }


    class Builder {
        val context: Context
        var itemSize: Int
            private set
        var itemCount: Int
            private set
        var animDuration: Long
            private set
        var minScale: Float
            private set
        var maxScale: Float
            private set
        var minAlpha: Float
            private set
        var maxAlpha: Float
            private set
        var moveFactor: Float
            private set
        var horizontalOffset: Int
            private set
        var spreadDirection: SpreadDirection
            private set
        var drawable: BitmapDrawable
            private set

        constructor(context: Context) : this(context, null)

        constructor(context: Context, attrs: AttributeSet?) {
            this.context = context
            val resources = context.resources
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
            resources.getValue(R.dimen.defaultMinAlpha, typedValue, true)
            val defaultMinAlpha = typedValue.float
            resources.getValue(R.dimen.defaultMaxAlpha, typedValue, true)
            val defaultMaxAlpha = typedValue.float
            val defaultHorizontalOffset = resources
                .getDimensionPixelSize(R.dimen.defaultHorizontalOffset)
            val defaultSpreadDirection = resources
                .getInteger(R.integer.defaultSpreadDirection)
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExplosionView)
            itemSize = typedArray.getDimensionPixelSize(
                R.styleable.ExplosionView_itemSize,
                defaultItemSize
            )
            itemCount = typedArray.getInteger(
                R.styleable.ExplosionView_shardItemsCount,
                defaultShardItemsCount
            )
            moveFactor = typedArray.getFloat(R.styleable.ExplosionView_moveFactor, defaultMoveFactor)
            animDuration = typedArray.getInt(
                R.styleable.ExplosionView_animDuration,
                defaultAnimDuration
            ).toLong()
            minScale = typedArray.getFloat(R.styleable.ExplosionView_minScale, defaultMinScale)
            maxScale = typedArray.getFloat(R.styleable.ExplosionView_maxScale, defaultMaxScale)
            minAlpha = typedArray.getFloat(R.styleable.ExplosionView_minAlpha, defaultMinAlpha)
            maxAlpha = typedArray.getFloat(R.styleable.ExplosionView_maxAlpha, defaultMaxAlpha)
            horizontalOffset = typedArray.getDimensionPixelSize(
                R.styleable.ExplosionView_horizontalOffset,
                defaultHorizontalOffset
            )
            spreadDirection = getSpreadDirection(
                if (typedArray.hasValue(R.styleable.ExplosionView_spreadDirection)) {
                    typedArray.getInt(
                        R.styleable.ExplosionView_spreadDirection,
                        defaultSpreadDirection
                    )
                } else {
                    defaultSpreadDirection
                }
            )
            drawable = (if (typedArray.hasValue(R.styleable.ExplosionView_shardDrawable)) {
                typedArray.getDrawable(R.styleable.ExplosionView_shardDrawable)
            } else {
                ContextCompat.getDrawable(context, R.drawable.default_shard_drawable)
            }) as? BitmapDrawable?: throw IllegalStateException("ShardDrawable should be BitmapDrawable")
            typedArray.recycle()
        }

        fun itemSize(itemSize: Int) = apply {
            this.itemSize = itemSize
        }

        fun itemCount(itemCount: Int) = apply {
            this.itemCount = itemCount
        }

        fun animDuration(animDuration: Long) = apply {
            this.animDuration = animDuration
        }

        fun minScale(minScale: Float) = apply {
            this.minScale = minScale
        }

        fun maxScale(maxScale: Float) = apply {
            this.maxScale = maxScale
        }

        fun minAlpha(minAlpha: Float) = apply {
            this.minAlpha = minAlpha
        }

        fun maxAlpha(maxAlpha: Float) = apply {
            this.maxAlpha = maxAlpha
        }

        fun moveFactor(moveFactor: Float) = apply {
            this.moveFactor = moveFactor
        }

        fun horizontalOffset(horizontalOffset: Int) = apply {
            this.horizontalOffset = horizontalOffset
        }

        fun spreadDirection(spreadDirection: SpreadDirection) = apply {
            this.spreadDirection = spreadDirection
        }

        fun drawable(drawable: BitmapDrawable) = apply {
            this.drawable = drawable
        }

        fun build(): ExplosionViewSettings {
            return ExplosionViewSettings(this)
        }

    }
}