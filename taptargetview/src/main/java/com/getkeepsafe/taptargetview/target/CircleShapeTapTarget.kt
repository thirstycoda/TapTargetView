package com.getkeepsafe.taptargetview.target

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.getkeepsafe.taptargetview.dp
import com.getkeepsafe.taptargetview.getDelayLerp
import com.getkeepsafe.taptargetview.halfwayLerp
import kotlin.math.pow
import kotlin.math.roundToInt

class CircleShapeTapTarget: TapTargetShapeType() {

    private var initialRadius = 0
    private var initialPulseRadius = 0
    private var currentRadius = 0f
    private var currentPulseRadius = 0f

    override var targetPadding = 0

    override fun onReadyTarget(bounds: Rect?, padding: Int) {
        checkNotNull(bounds)
        targetPadding = padding
        initialRadius = (bounds.width().coerceAtLeast(bounds.height()) / 2) + padding
        initialPulseRadius = (initialRadius * 0.1f).roundToInt()
    }

    override fun expandContractChange(lerpTime: Float, isExpanding: Boolean) {
        if (isExpanding) {
            currentRadius = initialRadius * 1.0f.coerceAtMost(lerpTime * 1.5f)
        } else {
            currentRadius = initialRadius * lerpTime
            currentPulseRadius *= lerpTime
        }
    }

    override fun pulseAnimation(lerpTime: Float) {
        currentPulseRadius = (1.0f + lerpTime.getDelayLerp(0.5f)) * initialRadius
        currentRadius = initialRadius + lerpTime.halfwayLerp * initialPulseRadius
    }

    override fun dismissConfirmAnimation(lerpTime: Float) {
        currentRadius = (1.0f - lerpTime) * initialRadius
        currentPulseRadius = (1.0f + lerpTime) * initialRadius
    }

    override fun drawTarget(
        canvas: Canvas,
        targetBounds: Rect,
        paint: Paint
    ) {
        canvas.drawCircle(
            targetBounds.centerX().toFloat(),
            targetBounds.centerY().toFloat(),
            currentRadius,
            paint
        )
    }

    override fun drawPulse(
        canvas: Canvas,
        targetPulseAlpha: Float,
        targetBounds: Rect,
        paint: Paint
    ) {
        if (targetPulseAlpha < 0) return
        canvas.drawCircle(
            targetBounds.centerX().toFloat(),
            targetBounds.centerY().toFloat(),
            currentPulseRadius,
            paint
        )
    }

    override fun drawInformation(canvas: Canvas, targetBounds: Rect, paint: Paint) {
        canvas.drawCircle(
            targetBounds.centerX().toFloat(),
            targetBounds.centerY().toFloat(),
            initialRadius + 20.dp.toFloat(),
            paint
        )
    }

    override fun clickInTarget(targetBounds: Rect, lastTouchX: Int, lastTouchY: Int): Boolean {
        val xPow = (lastTouchX - targetBounds.centerX()).toDouble().pow(2.0)
        val yPow = (lastTouchY - targetBounds.centerY()).toDouble().pow(2.0)
        val sqrt = (xPow + yPow).pow(0.5)
        return sqrt <= currentRadius
    }
}