package com.stylingandroid.colourwheel

import android.graphics.Bitmap
import android.graphics.Color
import android.support.annotation.ColorInt
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BitmapGenerator(
        private val config: Bitmap.Config,
        private val observer: BitmapObserver
) : ReadWriteProperty<Any, Byte> {

    private val size = Size(0, 0)

    var brightness = Byte.MAX_VALUE

    private var generateProcess: Job? = null

    private var generated: Bitmap? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): Byte =
            brightness

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Byte) {
        brightness = value
        generate()
    }

    fun setSize(width: Int, height: Int) {
        size.takeIf { it.width != width || it.height != height }?.also {
            generated = null
        }
        size.width = width
        size.height = height
        generate()
    }

    private fun generate() {
        if (generateProcess?.isCompleted != false) {
            generateProcess = async(CommonPool) {
                generateBlocking()
            }
        }
    }

    private fun generateBlocking() {
        Bitmap.createBitmap(size.width, size.height, config).also {
            draw(it)
            observer.bitmapChanged(it)
        }
    }

    private fun draw(bitmap: Bitmap) {
        val centreX = bitmap.width / 2.0
        val centreY = bitmap.height / 2.0
        val radius = Math.min(centreX, centreY)
        var xOffset: Double
        var yOffset: Double
        var centreOffset: Double
        var rawAngle: Double
        var centreAngle: Double
        @ColorInt var colour: Int
        val hsv = floatArrayOf(0f, 0f, 0f)
        hsv[2] = brightness.toFloat() / Byte.MAX_VALUE.toFloat()
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                xOffset = x - centreX
                yOffset = y - centreY
                centreOffset = Math.sqrt((xOffset * xOffset) + (yOffset * yOffset))
                colour = if (centreOffset <= radius) {
                    rawAngle = Math.toDegrees(Math.atan2((yOffset), (xOffset)))
                    centreAngle = (rawAngle + 360.0) % 360.0
                    hsv[0] = centreAngle.toFloat()
                    hsv[1] = (centreOffset / radius).toFloat()
                    Color.HSVToColor(hsv)
                } else {
                    Color.TRANSPARENT
                }
                bitmap.setPixel(x, y, colour)
            }
        }
    }

    fun stop() {}

    interface BitmapObserver {
        fun bitmapChanged(bitmap: Bitmap)
    }

    private data class Size(var width: Int, var height: Int)

}
