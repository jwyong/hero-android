package com.ntu.hero.global.bg_blur

import android.graphics.Bitmap
import android.graphics.Canvas
import android.renderscript.*
import android.view.View
import androidx.annotation.NonNull


class BlurImg(private val rs: RenderScript) {
    private val MAX_RADIUS = 25

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(c)

        return bitmap
    }


    fun blur(@NonNull bitmap: Bitmap, radius: Float, repeat: Int): Bitmap? {
        var radius = radius

        if (radius > MAX_RADIUS) {
            radius = MAX_RADIUS.toFloat()
        }

        val width = bitmap.width
        val height = bitmap.height

        // Create allocation type
        val bitmapType = Type.Builder(rs, Element.RGBA_8888(rs))
            .setX(width)
            .setY(height)
            .setMipmaps(false) // We are using MipmapControl.MIPMAP_NONE
            .create()

        // Create allocation
        var allocation: Allocation? = Allocation.createTyped(rs, bitmapType)

        // Create blur script
        var blurScript: ScriptIntrinsicBlur? = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blurScript!!.setRadius(radius)

        // Copy data to allocation
        allocation!!.copyFrom(bitmap)

        // set blur script input
        blurScript.setInput(allocation)

        // invoke the script to blur
        blurScript.forEach(allocation)

        // Repeat the blur for extra effect
        for (i in 0 until repeat) {
            blurScript.forEach(allocation)
        }

        // copy data back to the bitmap
        allocation.copyTo(bitmap)

        // release memory
        allocation.destroy()
        blurScript.destroy()
        allocation = null
        blurScript = null

        return bitmap
    }

}