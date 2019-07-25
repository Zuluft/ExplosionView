package com.zuluft.lib

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import java.security.InvalidParameterException

class ExplosionViewSettings private constructor(builder: Builder) {

    val context: Context
    val itemWidth: Int
    val itemHeight: Int
    val itemCount: Int
    val minAnimDuration: Long
    val maxAnimDuration: Long
    val minAnimDelay: Long
    val maxAnimDelay: Long
    val minScale: Float
    val maxScale: Float
    val minAlpha: Float
    val maxAlpha: Float
    val minMoveFactor: Float
    val maxMoveFactor: Float
    val horizontalOffset: Int
    val spreadDirection: SpreadDirection
    val drawable: Drawable
    val spreadMode: SpreadMode

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
        drawable = builder.drawable

        var height = builder.itemHeight
        var width = builder.itemWidth

        if (height < 0 || width < 0) {
            throw InvalidParameterException("Item size may not be negative")
        } else if (height == 0 && width == 0) {
            width = context.resources.getDimensionPixelSize(R.dimen.defaultItemWidth)
        }
        val ratio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
        if (height == 0 && width != 0) {
            height = (width / ratio).toInt()
        }
        if (height != 0 && width == 0) {
            width = (ratio * height).toInt()
        }
        itemHeight = height
        itemWidth = width
        itemCount = builder.itemCount
        minAnimDuration = builder.minAnimDuration
        maxAnimDuration = builder.maxAnimDuration
        minAnimDelay = builder.minAnimDelay
        maxAnimDelay = builder.maxAnimDelay
        minScale = builder.minScale
        maxScale = builder.maxScale
        minAlpha = builder.minAlpha
        maxAlpha = builder.maxAlpha
        minMoveFactor = builder.minMoveFactor
        maxMoveFactor = builder.maxMoveFactor
        horizontalOffset = builder.horizontalOffset
        spreadDirection = builder.spreadDirection
        spreadMode = builder.spreadMode
        if (minAnimDuration > maxAnimDuration) {
            throw InvalidParameterException("minAnimDuration should be less then maxAnimDuration")
        }
        if (minAnimDelay > maxAnimDelay) {
            throw InvalidParameterException("minAnimDelay should be less then maxAnimDelay")
        }
        if (minScale > maxScale) {
            throw InvalidParameterException("minScale should be less then maxScale")
        }
        if (minAlpha > maxAlpha) {
            throw InvalidParameterException("minAlpha should be less then maxAlpha")
        }
        if (minMoveFactor > maxMoveFactor || minMoveFactor < 0 || maxMoveFactor < 0) {
            throw InvalidParameterException(
                "moveFactor should be positive number " +
                        "and minMoveFactor should be less then maxMoveFactor"
            )
        }
    }


    class Builder {
        val context: Context
        var itemWidth: Int
            private set
        var itemHeight: Int
            private set
        var itemCount: Int
            private set
        var minAnimDuration: Long
            private set
        var maxAnimDuration: Long
            private set
        var minAnimDelay: Long
            private set
        var maxAnimDelay: Long
            private set
        var minScale: Float
            private set
        var maxScale: Float
            private set
        var minAlpha: Float
            private set
        var maxAlpha: Float
            private set
        var minMoveFactor: Float
            private set
        var maxMoveFactor: Float
            private set
        var horizontalOffset: Int
            private set
        var spreadDirection: SpreadDirection
            private set
        var drawable: Drawable
            private set
        var spreadMode: SpreadMode
            private set

        constructor(context: Context) : this(context, null)

        constructor(context: Context, attrs: AttributeSet?) {
            this.context = context
            val resources = context.resources
            val typedValue = TypedValue()
            val defaultItemsCount = resources.getInteger(R.integer.defaultItemsCount)
            resources.getValue(R.dimen.defaultMinMoveFactor, typedValue, true)
            val defaultMinMoveFactor = typedValue.float
            resources.getValue(R.dimen.defaultMaxMoveFactor, typedValue, true)
            val defaultMaxMoveFactor = typedValue.float
            val defaultMinAnimDuration = resources.getInteger(R.integer.defaultMinAnimDuration)
            val defaultMaxAnimDuration = resources.getInteger(R.integer.defaultMaxAnimDuration)
            val defaultMinAnimDelay = resources.getInteger(R.integer.defaultMinAnimDelay)
            val defaultMaxAnimDelay = resources.getInteger(R.integer.defaultMaxAnimDelay)
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
            val defaultSpreadMode = resources.getInteger(R.integer.defaultSpreadMode)
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExplosionView)
            itemWidth = typedArray.getDimensionPixelSize(
                R.styleable.ExplosionView_itemWidth,
                0
            )
            itemHeight = typedArray.getDimensionPixelSize(
                R.styleable.ExplosionView_itemWidth,
                0
            )
            itemCount = typedArray.getInteger(
                R.styleable.ExplosionView_ItemsCount,
                defaultItemsCount
            )
            minMoveFactor = typedArray.getFloat(R.styleable.ExplosionView_minMoveFactor, defaultMinMoveFactor)
            maxMoveFactor = typedArray.getFloat(R.styleable.ExplosionView_maxMoveFactor, defaultMaxMoveFactor)
            minAnimDuration = typedArray.getInt(
                R.styleable.ExplosionView_minAnimDuration,
                defaultMinAnimDuration
            ).toLong()
            maxAnimDuration = typedArray.getInt(
                R.styleable.ExplosionView_maxAnimDuration,
                defaultMaxAnimDuration
            ).toLong()
            minAnimDelay = typedArray.getInt(
                R.styleable.ExplosionView_minAnimDelay,
                defaultMinAnimDelay
            ).toLong()
            maxAnimDelay = typedArray.getInt(
                R.styleable.ExplosionView_maxAnimDelay,
                defaultMaxAnimDelay
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
            spreadMode = getSpreadMode(
                if (typedArray.hasValue(R.styleable.ExplosionView_spreadMode)) {
                    typedArray.getInt(
                        R.styleable.ExplosionView_spreadMode,
                        defaultSpreadMode
                    )
                } else {
                    defaultSpreadMode
                }
            )
            drawable = (if (typedArray.hasValue(R.styleable.ExplosionView_drawable)) {
                typedArray.getDrawable(R.styleable.ExplosionView_drawable)
            } else {
                ContextCompat.getDrawable(context, R.drawable.default_shard_drawable)
            })!!
            typedArray.recycle()
        }

        fun itemWidth(itemWidth: Int) = apply {
            this.itemWidth = itemWidth
        }

        fun itemHeight(itemHeight: Int) = apply {
            this.itemHeight = itemHeight
        }

        fun itemCount(itemCount: Int) = apply {
            this.itemCount = itemCount
        }

        fun minAnimDuration(minAnimDuration: Long) = apply {
            this.minAnimDuration = minAnimDuration
        }

        fun maxAnimDuration(maxAnimDuration: Long) = apply {
            this.maxAnimDuration = maxAnimDuration
        }

        fun minAnimDelay(minAnimDelay: Long) = apply {
            this.minAnimDelay = minAnimDelay
        }

        fun maxAnimDelay(maxAnimDelay: Long) = apply {
            this.maxAnimDelay = maxAnimDelay
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

        fun minMoveFactor(minMoveFactor: Float) = apply {
            this.minMoveFactor = minMoveFactor
        }

        fun maxMoveFactor(maxMoveFactor: Float) = apply {
            this.maxMoveFactor = maxMoveFactor
        }

        fun horizontalOffset(horizontalOffset: Int) = apply {
            this.horizontalOffset = horizontalOffset
        }

        fun spreadDirection(spreadDirection: SpreadDirection) = apply {
            this.spreadDirection = spreadDirection
        }

        fun spreadDirection(spreadMode: SpreadMode) = apply {
            this.spreadMode = spreadMode
        }

        fun drawable(drawable: Drawable) = apply {
            this.drawable = drawable
        }

        fun build(): ExplosionViewSettings {
            return ExplosionViewSettings(this)
        }

    }
}