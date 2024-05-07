@file:Suppress("unused")
package com.getkeepsafe.taptargetview.target

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.getkeepsafe.taptargetview.dp

abstract class TapTargetShapeType {

    var textPadding = 40.dp

    var textSpacing = 8.dp

    var textMaxWidth = 360.dp

    var outerCirclePadding = 40.dp

    abstract var targetPadding: Int


    companion object {

        val Circle = CircleShapeTapTarget()

        val RectAngle = RectAngleShapeType()

        fun RectAngle(roundRadius: Int): RectAngleShapeType {
            val rectangleType = RectAngleShapeType()
            rectangleType.roundRadius = roundRadius
            return rectangleType
        }

    }

    abstract fun expandContractChange(lerpTime: Float, isExpanding: Boolean)

    open fun pulseAnimation(lerpTime: Float) {}

    abstract fun dismissConfirmAnimation(lerpTime: Float)

    abstract fun drawTarget(
        canvas: Canvas,
        targetBounds: Rect,
        paint: Paint
    )

    open fun drawPulse(
        canvas: Canvas,
        targetPulseAlpha: Float,
        targetBounds: Rect,
        paint: Paint
    ) {}

    open fun drawInformation(canvas: Canvas, targetBounds: Rect, paint: Paint) {}

    abstract fun clickInTarget(targetBounds: Rect, lastTouchX: Int, lastTouchY: Int): Boolean

    open fun onReadyTarget(bounds: Rect?, padding: Int) {}

    open fun getTextBounds(
        totalTextHeight: Int,
        totalTextWidth: Int,
        targetBounds: Rect,
        topBoundary: Int,
        bottomBoundary: Int,
        viewWidth: Int
    ): Rect {
        val verticalLocation = getTextVertical(targetBounds, totalTextHeight, topBoundary, bottomBoundary)
        val horizontalLocation = getTextHorizontal(targetBounds, totalTextWidth, viewWidth)
        return Rect(
            horizontalLocation.first,
            verticalLocation.first,
            horizontalLocation.second,
            verticalLocation.second
        )
    }

    open fun getTextVertical(
        targetBounds: Rect,
        totalTextHeight: Int,
        topBoundary: Int,
        bottomBoundary: Int
    ): Pair<Int, Int> {
        val top =  if (targetBounds.centerY() - topBoundary > bottomBoundary - targetBounds.centerY()) {
            // There is more space above the target than below so place the text there
            targetBounds.top - targetPadding - textPadding - totalTextHeight
        } else {
            targetBounds.bottom + targetPadding + textPadding
        }
        return top to top + totalTextHeight
    }

    open fun getTextHorizontal(
        targetBounds: Rect,
        totalTextWidth: Int,
        viewWidth: Int
    ): Pair<Int, Int> {
        val left: Int
        val right: Int

        val maxWidth = totalTextWidth.coerceAtMost(textMaxWidth)

        if (viewWidth / 2 < targetBounds.centerX()) {
            left = targetBounds.right - maxWidth
            right = targetBounds.right
        } else {
            left = targetBounds.left
            right = targetBounds.left + maxWidth
        }

        return (left to right)
            .coerceAtLeast(textPadding)
            .coerceAtMost(viewWidth - textPadding)
    }
}

fun Pair<Int, Int>.coerceAtLeast(value: Int): Pair<Int, Int> {
    val (first, second) = this
    val maxDelta = maxOf(value - first, value - second, 0)

    val newFirst = first + maxDelta
    val newSecond = second + maxDelta

    return Pair(newFirst, newSecond)
}

fun Pair<Int, Int>.coerceAtMost(value: Int): Pair<Int, Int> {
    val maxPairValue = maxOf(first, second)
    return if (maxPairValue > value) {
        val delta = maxPairValue - value
        Pair(first - delta, second - delta)
    } else {
        this
    }
}

