@file:Suppress("unused")
package com.getkeepsafe.taptargetview.target

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.getkeepsafe.taptargetview.dp
import com.getkeepsafe.taptargetview.getDelayLerp
import com.getkeepsafe.taptargetview.halfwayLerp

class RectAngleShapeType : TapTargetShapeType() {

    private var initialWidth = 0
    private var initialHeight = 0

    private var currentWidth = 0f
    private var currentHeight = 0f

    private var currentPulseWidth= 0f
    private var currentPulseHeight = 0f

    private var pulseLength = 4.dp
    internal var roundRadius = 8.dp

    override var targetPadding = 0

    override fun onReadyTarget(bounds: Rect?, padding: Int) {
        checkNotNull(bounds)
        this.targetPadding = padding
        this.initialWidth = bounds.width() + padding
        this.initialHeight = bounds.height() + padding
    }

    override fun expandContractChange(lerpTime: Float, isExpanding: Boolean) {
        if (isExpanding) {
            currentHeight = initialHeight * 1.0f.coerceAtMost(lerpTime * 1.5f)
            currentWidth = initialWidth * 1.0f.coerceAtMost(lerpTime * 1.5f)
        } else {
            currentHeight = initialHeight * lerpTime
            currentWidth = initialWidth * lerpTime
            currentPulseWidth *= lerpTime
            currentPulseHeight *= lerpTime
        }
    }

    override fun dismissConfirmAnimation(lerpTime: Float) {
        currentHeight = initialHeight * (1.0f - lerpTime)
        currentWidth = initialWidth * (1.0f - lerpTime)
        currentPulseWidth = (1.0f + lerpTime) * initialWidth
        currentPulseHeight = (1.0f + lerpTime) * initialHeight
    }

    override fun pulseAnimation(lerpTime: Float) {
        currentWidth = initialWidth + lerpTime.halfwayLerp * pulseLength
        currentHeight = initialHeight + lerpTime.halfwayLerp * pulseLength
        currentPulseHeight = (1.0f + lerpTime.getDelayLerp(0.5f)) * initialHeight
        currentPulseWidth = (1.0f + lerpTime.getDelayLerp(0.5f)) * initialWidth
    }

    override fun drawTarget(canvas: Canvas, targetBounds: Rect, paint: Paint) {
        canvas.drawRoundRect(
            targetBounds.toTargetRectF(
                currentWidth, currentHeight
            ),
            roundRadius.toFloat(),
            roundRadius.toFloat(),
            paint
        )
    }

    private fun Rect.toTargetRectF(
        width: Float,
        height: Float
    ): RectF {
        val centerX = centerX()
        val centerY = centerY()
        val right = width * 0.5f
        val bottom = height * 0.5f
        return RectF(
            centerX - right,
            centerY - bottom,
            centerX + right,
            centerY + bottom
        )
    }

    override fun drawPulse(
        canvas: Canvas,
        targetPulseAlpha: Float,
        targetBounds: Rect,
        paint: Paint
    ) {
        if (targetPulseAlpha < 0) return
        canvas.drawRoundRect(
            targetBounds.toTargetRectF(
                currentPulseWidth, currentPulseHeight
            ),
            roundRadius.toFloat(),
            roundRadius.toFloat(),
            paint
        )
    }

    override fun clickInTarget(targetBounds: Rect, lastTouchX: Int, lastTouchY: Int): Boolean {
        return targetBounds.contains(lastTouchX, lastTouchY)
    }
}