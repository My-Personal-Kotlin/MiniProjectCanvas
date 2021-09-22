package com.minipaint.customcanvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.minipaint.R
import com.minipaint.someone.PaintView

private const val STROKE_WIDTH = 12f

class MyCanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //
    //              4 things required for drawing
    //
    //      1-> Bitmap
    //      2-> Canvas
    //      3-> Drawing Primitive (text,line,rect,circle)
    //      4-> Paint
    //

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private lateinit var frame: Rect

    // Path representing the drawing so far
    private val drawing = Path()
    // Path representing what's currently being drawn
    private val currentPath = Path()

    private val mBlur: MaskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
      //  maskFilter = mBlur
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(backgroundColor)

        // Draw the drawing so far
        canvas.drawPath(drawing, paint)

        // Draw any current squiggle
        canvas.drawPath(currentPath, paint)

        // Draw a frame around the canvas
        canvas.drawRect(frame, paint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        motionTouchEventX = event!!.x
        motionTouchEventY = event!!.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> touchStart() // touch down
            MotionEvent.ACTION_MOVE -> touchMove() // moving on screen
            MotionEvent.ACTION_UP -> touchUp() // releasing touch
        }
        return true
    }

    private fun touchStart() {
        currentPath.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX) // distanxe X that has been moved
        val dy = Math.abs(motionTouchEventY - currentY) // distanxe Y that has been moved
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            currentPath.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
          //  extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {
        // Add the current path to the drawing so far
        drawing.addPath(currentPath)

        // Reset the current path for the next touch
        currentPath.reset()
    }

    fun clear() {
        drawing.reset()
        invalidate()
    }

}