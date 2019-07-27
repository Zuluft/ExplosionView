package com.zuluft.lib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.ref.WeakReference


class ExplosionView :
        View {


    private var explosionViewSettings: ExplosionViewSettings
    private var drawableShardItemsHolder: DrawableShardItemsHolder? = null

    private var selectedShardItem: DrawableShardItem? = null

    private var shouldStartAnim = false

    constructor(explosionViewSettings: ExplosionViewSettings) : super(explosionViewSettings.context) {
        this.explosionViewSettings = explosionViewSettings
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        this.explosionViewSettings = ExplosionViewSettings.builder(context, attrs).build()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!explosionViewSettings.isDraggable) {
            return super.onTouchEvent(event)
        }
        return when {
            event.action == MotionEvent.ACTION_DOWN -> {
                selectedShardItem =
                        drawableShardItemsHolder!!
                                .findShardItemByLocation(event.x.toInt(), event.y.toInt())
                if (selectedShardItem != null) {
                    drawableShardItemsHolder?.detachAnim(selectedShardItem!!)
                    true
                } else {
                    super.onTouchEvent(event)
                }
            }
            event.action == MotionEvent.ACTION_MOVE -> {
                if (selectedShardItem != null) {
                    selectedShardItem?.setX(event.x.toInt())
                    selectedShardItem?.setY(event.y.toInt())
                    invalidate()
                    return true
                } else {
                    super.onTouchEvent(event)
                }
            }
            event.action == MotionEvent.ACTION_UP -> {
                if (selectedShardItem != null) {
                    drawableShardItemsHolder?.attachAnim(selectedShardItem!!, true)
                    selectedShardItem = null
                    true
                } else {
                    super.onTouchEvent(event)
                }
            }
            else ->
                super.onTouchEvent(event)
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        finish()
        setupDrawableShardItemsHolder(w, h)
    }

    private fun setupDrawableShardItemsHolder(width: Int, height: Int) {
        drawableShardItemsHolder =
                DrawableShardItemsHolder(
                        width,
                        height,
                        explosionViewSettings,
                        WeakReference(this)
                )
        if (shouldStartAnim) {
            drawableShardItemsHolder!!.startAnim()
        }
    }

    fun start() {
        if (drawableShardItemsHolder == null) {
            shouldStartAnim = true
        } else if (!drawableShardItemsHolder!!.isAnimRunning()) {
            drawableShardItemsHolder!!.startAnim()
        }
    }

    fun finish() {
        if (drawableShardItemsHolder != null && drawableShardItemsHolder!!.isAnimRunning()) {
            drawableShardItemsHolder!!.endAnim()
            shouldStartAnim = false
        }
    }

    fun finishSmoothly() {
        if (drawableShardItemsHolder != null && drawableShardItemsHolder!!.isAnimRunning()) {
            drawableShardItemsHolder!!.endAnimSmoothly()
        }
    }

    fun isAnimRunning(): Boolean {
        return drawableShardItemsHolder?.isAnimRunning() ?: false
    }

    fun getSettings(): ExplosionViewSettings {
        return explosionViewSettings
    }

    fun changeSettings(explosionViewSettings: ExplosionViewSettings) {
        finish()
        this.explosionViewSettings = explosionViewSettings
        shouldStartAnim = drawableShardItemsHolder?.isAnimRunning() ?: false
        if (width != 0 && height != 0) {
            setupDrawableShardItemsHolder(width, height)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (drawableShardItemsHolder != null) {
            drawableShardItemsHolder!!.endAnim()
            drawableShardItemsHolder = null
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawableShardItemsHolder?.draw(canvas)
    }

}
