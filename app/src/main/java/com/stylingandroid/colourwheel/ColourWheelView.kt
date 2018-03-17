package com.stylingandroid.colourwheel

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.ImageView

class ColourWheelView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attributeSet, defStyleAttr), BitmapGenerator.BitmapObserver {
    private val bitmapGenerator: BitmapGenerator by lazy(LazyThreadSafetyMode.NONE) {
        BitmapGenerator(context, Bitmap.Config.ARGB_8888, this)
    }

    var brightness: Byte by bitmapGenerator

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            bitmapGenerator.setSize(w, h)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bitmapGenerator.brightness = Byte.MAX_VALUE
    }

    override fun bitmapChanged(bitmap: Bitmap) {
        setImageBitmap(bitmap)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bitmapGenerator.stop()
    }
}
