package dev.develsinthedetails.eatpoopyoucat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

// todo figure out how to allow dots
// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

class DrawView(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {

    // Holds the path you are currently drawing.
    private var path = Path()
    private val drawingPaths = ArrayList<Path>()
    private val undonePaths = ArrayList<Path>()

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private lateinit var canvas: Canvas
    private lateinit var bitmap: Bitmap

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
    }

    /**
     * Don't draw every single pixel.
     * If the finger has has moved less than this distance, don't draw. scaledTouchSlop, returns
     * the distance in pixels a touch can wander before we think the user is scrolling.
     */
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    /**
     * Called whenever the view changes size.
     * Since the view starts out with no size, this is also called after
     * the view has been inflated and has a valid size.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::bitmap.isInitialized) bitmap.recycle()
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)
        drawingPaths.add(path)
    }

    override fun onDraw(canvas: Canvas) {
        for (p in drawingPaths) {
            canvas.drawPath(p, paint)
        }
        canvas.drawPath(path, paint)
    }

    /**
     * No need to call and implement MyCanvasView#performClick, because MyCanvasView custom view
     * does not handle click actions.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    /**
     * The following methods factor out what happens for different touch events,
     * as determined by the onTouchEvent() when statement.
     * This keeps the when conditional block
     * concise and makes it easier to change what happens for each event.
     * No need to call invalidate because we are not drawing anything.
     */
    private fun touchStart() {
        undonePaths.clear()
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

        }
        canvas.drawPath(path, paint)
        invalidate()
    }

    private fun touchUp() {
        // draw a dot if no movement but touched.
        if (currentX == motionTouchEventX && currentY == motionTouchEventY) {
            path.lineTo(currentX, currentY)
        }

        drawingPaths.add(path)
        path = Path()
        invalidate()
    }

    fun getBitmap(): Bitmap {
        return this.bitmap
    }

    fun setErase() {
    }

    fun clearCanvas() {
        canvas.drawColor(backgroundColor)
        path.reset()
        drawingPaths.clear()
        invalidate()
    }

    fun setPen() {
    }

    fun undo() {
        if (drawingPaths.size > 0) {
            undonePaths.add(drawingPaths.removeAt(drawingPaths.size - 1))
            invalidate()
        }
    }

    fun redo() {
        if (undonePaths.size > 0) {
            drawingPaths.add(undonePaths.removeAt(undonePaths.size - 1))
            invalidate()
        }
    }
}
