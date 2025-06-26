package com.minhtv.base.utils

import android.content.Context
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Region
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt


object Maths {
    fun isPointOnTransformedPath(
        path: Path,
        x: Float, y: Float,
        transformationMatrix: Matrix
    ): Boolean {
        // Create an inverse matrix to reverse transformations
        val inverseMatrix = Matrix()
        if (!transformationMatrix.invert(inverseMatrix)) {
            return false // If matrix inversion fails, return false
        }

        // Transform touch point back to original space
        val points = floatArrayOf(x, y)
        inverseMatrix.mapPoints(points) // Reverse transformation

        // Check if the transformed point is inside the original path
        val region = Region()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        region.setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))

        return region.contains(points[0].toInt(), points[1].toInt())
    }

    fun isPointOnRotatedPath(path: Path, x: Float, y: Float, rotationAngle: Float, pivotX: Float, pivotY: Float): Boolean {
        val matrix = Matrix()
        matrix.setRotate(-rotationAngle, pivotX, pivotY) // Inverse rotation

        val points = floatArrayOf(x, y)
        matrix.mapPoints(points) // Rotate the touch point back

        // Check if the transformed point is inside the original path
        val region = Region()
        val bounds = RectF()
        path.computeBounds(bounds, true)
        region.setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))

        return region.contains(points[0].toInt(), points[1].toInt())
    }


    //    * Checks if a tap (px, py) is near a given path.
    fun isTapOnPath(tapX: Float, tapY: Float, path: Path, threshold: Float = 20f): Boolean {
        val pathMeasure = PathMeasure(path, false)
        val length = pathMeasure.length
        val pos = FloatArray(2)
        val previousPos = FloatArray(2)

        if (length == 0f) return false // Skip empty paths

        var distance = 0f
        pathMeasure.getPosTan(distance, previousPos, null)

        while (distance < length) {
            pathMeasure.getPosTan(distance, pos, null)

            if (isPointNearLineSegment(tapX, tapY, previousPos[0], previousPos[1], pos[0], pos[1], threshold)) {
                return true // ✅ Tap is on path
            }
            previousPos[0] = pos[0]
            previousPos[1] = pos[1]
            distance += 10f // Sample every 10 pixels
        }
        return false
    }

    /**
     * Checks if a point (tapX, tapY) is near a line segment (x1, y1) - (x2, y2)
     */
    fun isPointNearLineSegment(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float, threshold: Float): Boolean {
        val lengthSquared = hypot(x2 - x1, y2 - y1)
        if (lengthSquared == 0f) return hypot(px - x1, py - y1) <= threshold

        // Projection of point onto the line segment
        val t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / (lengthSquared * lengthSquared)
        val tClamped = t.coerceIn(0f, 1f) // Clamp between 0 and 1

        val closestX = x1 + tClamped * (x2 - x1)
        val closestY = y1 + tClamped * (y2 - y1)

        // Compute distance from tap point to the closest point on line
        return hypot(px - closestX, py - closestY) <= threshold
    }

    fun calculateRotationAngle(x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val v1x = x1 - x0
        val v1y = y1 - y0
        val v2x = x2 - x0
        val v2y = y2 - y0

        // Compute cross product (determines direction)
        val cross = v1x * v2y - v1y * v2x

        // Compute dot product (determines magnitude)
        val dot = v1x * v2x + v1y * v2y

        // Compute the angle in radians and convert to degrees
        val angleRad = atan2(cross, dot)
        return Math.toDegrees(angleRad.toDouble()).toFloat()
    }
    fun distance(x: Float, y: Float, x1: Float, y1: Float): Float {
        val dx = x1 - x
        val dy = y1 - y
        return sqrt(dx * dx + dy * dy)
    }

}