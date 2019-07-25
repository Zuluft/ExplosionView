package com.zuluft.lib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import java.lang.ref.WeakReference


class ExplosionView :
    View,
    ViewTreeObserver.OnPreDrawListener {


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

    init {
        viewTreeObserver.addOnPreDrawListener(this)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
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
                    drawableShardItemsHolder?.attachAnim(selectedShardItem!!)
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

    override fun onPreDraw(): Boolean {
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
        viewTreeObserver.removeOnPreDrawListener(this)
        return false
    }


    fun explode() {
        if (drawableShardItemsHolder == null) {
            shouldStartAnim = true
        } else if (!drawableShardItemsHolder!!.isAnimRunning()) {
            drawableShardItemsHolder!!.startAnim()
        }
    }

    fun collapse() {
        if (drawableShardItemsHolder != null && drawableShardItemsHolder!!.isAnimRunning()) {
            drawableShardItemsHolder!!.endAnim()
            shouldStartAnim = false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (drawableShardItemsHolder != null) {
            drawableShardItemsHolder!!.endAnim()
            drawableShardItemsHolder = null
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isAnimRunning = drawableShardItemsHolder != null && drawableShardItemsHolder!!.isAnimRunning()
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
        drawableShardItemsHolder?.draw(canvas)
    }

}
