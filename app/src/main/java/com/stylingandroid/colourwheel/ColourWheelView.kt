package com.stylingandroid.colourwheel

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class ColourWheelView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attributeSet, defStyleAttr), BitmapGenerator.BitmapObserver {

    private val bitmapGenerator: BitmapGenerator by lazy(LazyThreadSafetyMode.NONE) {
        BitmapGenerator(Bitmap.Config.ARGB_8888, this)
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
