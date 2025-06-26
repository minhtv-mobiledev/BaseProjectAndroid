package com.draw.animation.utils

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.draw.animation.views.SignatureView
import java.util.LinkedList

data class ZoomState(var zoomFactor: Float, val zoomCenterX: Float, val zoomCenterY: Float)

class ZoomController {
    var zoomEnabled = false
    var zoomCenter = PointF()
    var zoomFactor = 1f

    private val matrix = Matrix()
    private val inverseMatrix = Matrix()
    var oldCanvasPointF = PointF()
    val viewRect = RectF()
    lateinit var view : SignatureView
    var isZoomAt = false
    var posX = 0f
    var posY = 0f
    val zoomQueue = mutableListOf<ZoomState>() // using as Queue

    fun applyToCanvas(canvas: Canvas) {
        if (zoomEnabled) {
            canvas.translate(view.posX, view.posY)
            canvas.concat(matrix)
            return
        }
        if (isZoomAt) {
            canvas.translate(view.posX, view.posY)
//            val posX = -zoomCenter.x * (zoomFactor  - 1)
//            val posY = -zoomCenter.y * (zoomFactor - 1)
//
//            canvas.translate(posX, posY)
//            canvas.scale(zoomFactor, zoomFactor)
//            canvas.scale(zoomFactor, zoomFactor, zoomCenter.x, zoomCenter.y)
//            canvas.scale(zoomFactorAfterZoom / zoomFactor, zoomFactorAfterZoom / zoomFactor, canvasZoomAtX, canvasZoomAtY)
            canvas.concat(matrix)
            return
        }
        if (zoomEnabled) {
            canvas.translate(view.posX, view.posY)
//            val posX = -zoomCenter.x * (zoomFactor  - 1)
//            val posY = -zoomCenter.y * (zoomFactor - 1)
//
//            canvas.translate(posX, posY)
//            canvas.scale(zoomFactor, zoomFactor)
//            canvas.scale(zoomFactor, zoomFactor, zoomCenter.x, zoomCenter.y)
            canvas.concat(matrix)
//            val visibleRect = getVisibleRect(view)
//            canvas.clipRect(visibleRect)
        }
    }

    fun updateMatrix() {
        matrix.reset()
        if (zoomEnabled) {
//            matrix.postTranslate((view?.posX?.times(zoomFactor)) ?: 0f , (view?.posY?.times(zoomFactor)) ?: 0f )
            matrix.postScale(zoomFactor, zoomFactor, zoomCenter.x, zoomCenter.y)
            if (isZoomAt) {
                var lastZoomFactor = zoomFactor
                zoomQueue.forEachIndexed { index, zoomState ->
                    matrix.preScale(
                        zoomState.zoomFactor / lastZoomFactor,
                        zoomState.zoomFactor / lastZoomFactor,
                        zoomState.zoomCenterX,
                        zoomState.zoomCenterY
                    )
                    lastZoomFactor = zoomState.zoomFactor
                }
            }
        }
        matrix.invert(inverseMatrix)
    }
    fun cloneUpdateMatrixOnlyPostScale () {
        matrix.reset()
        if (zoomEnabled) {
            matrix.postScale(zoomFactor, zoomFactor, zoomCenter.x, zoomCenter.y)
        }
        matrix.invert(inverseMatrix)
    }

    fun toCanvasSpace(screenX: Float, screenY: Float): PointF {
        val pts = floatArrayOf(screenX, screenY)
        inverseMatrix.mapPoints(pts)
        return PointF(pts[0], pts[1])
    }

    fun toCanvasSpace(event: MotionEvent): PointF {
        return toCanvasSpace(event.x, event.y)
    }
    fun reset() {
        zoomEnabled = false
        zoomFactor = 1f

        zoomQueue.clear()

        zoomCenter.set(0f, 0f)
        isZoomAt = false
        updateMatrix()
    }
    fun enableZoom(x : Float, y: Float, currentScaleFactor : Float) {
        zoomEnabled = true
        isZoomAt = false
        zoomCenter.set(x, y)  // vị trí cần zoom
        zoomFactor = currentScaleFactor
        updateMatrix()
    }
    fun getCurrentZoomFactor(): Float {
        return if (zoomQueue.isNotEmpty()) {
            zoomQueue.last().zoomFactor
        } else {
            zoomFactor
        }
    }
    fun zoomAt(screenX: Float, screenY: Float, targetZoom: Float) {
//        if (isZoomAt) {
//            zoomFactorAfterZoom2 = targetZoom
//
//            val canvasPoint = toCanvasSpace(screenX, screenY)
//            // Update posX, posY để điểm này giữ đúng vị trí
//            canvasZoomAtX2 = canvasPoint.x
//            canvasZoomAtY2 = canvasPoint.y
//            return
//        }
        isZoomAt = true
        val oldZoom = zoomFactor

        val canvasPoint = toCanvasSpace(screenX, screenY)

        zoomQueue.add(ZoomState(targetZoom, canvasPoint.x - view!!.posX / getCurrentZoomFactor(), canvasPoint.y - view!!.posY / getCurrentZoomFactor()))

//
//        zoomFactorAfterZoom = targetZoom
//
//        // Update posX, posY để điểm này giữ đúng vị trí
//        canvasZoomAtX = canvasPoint.x
//        canvasZoomAtY = canvasPoint.y
        updateMatrix()
    }

    fun setNewZoomFactor(factor: Float) {
        if (!isZoomAt) {
            zoomFactor = factor
        } else {
            if (zoomQueue.isNotEmpty()) {
                val lastZoomState = zoomQueue.last()
                lastZoomState.zoomFactor = factor
            }
        }
        updateMatrix()
    }
}
