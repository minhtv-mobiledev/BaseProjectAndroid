package com.draw.animation.utils

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.draw.animation.models.PathData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Stack

class PathConverter {
    private val gson = Gson()

    fun pathsToJson(paths: List<Pair<Path, Paint>>): String {
        val pathDataList = paths.map { (p, paint) ->
            val points = mutableListOf<Pair<Float, Float>>()
            val pathMeasure = PathMeasure(p, false)
            val pos = FloatArray(2)
            var distance = 0f
            while (distance < pathMeasure.length) {
                pathMeasure.getPosTan(distance, pos, null)
                points.add(Pair(pos[0], pos[1]))
                distance += 5f
            }
            if (pathMeasure.length > 0) {
                pathMeasure.getPosTan(pathMeasure.length, pos, null)
                points.add(Pair(pos[0], pos[1]))
            }

            PathData(points, paint.xfermode != null, paint.strokeWidth)
        }
        return gson.toJson(pathDataList)
    }

    fun jsonToPaths(json: String): List<Pair<Path, Paint>> {
        val type = object : TypeToken<List<PathData>>() {}.type
        val pathDataList: List<PathData> = gson.fromJson(json, type)

        return pathDataList.map { data ->
            val newPath = Path()
            if (data.points.isNotEmpty()) {
                newPath.moveTo(data.points[0].first, data.points[0].second)
                for (point in data.points.drop(1)) {
                    newPath.lineTo(point.first, point.second)
                }
            }

            val newPaint = Paint().apply {
                style = Paint.Style.STROKE  // ✅ Ensures only strokes, no fills
                xfermode = if (data.isErase) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null
                strokeWidth = data.strokeWidth
            }

            Pair(newPath, newPaint)
        }
    }

    // Chuyển đổi Stack<List<Pair<Path, Paint>>> thành JSON
    fun stackToJson(stack: Stack<List<Pair<Path, Paint>>>): String {
        val stackList = stack.map { paths ->
            paths.map { (p, paint) ->
                val points = mutableListOf<Pair<Float, Float>>()
                val pathMeasure = PathMeasure(p, false)
                val pos = FloatArray(2)

                var distance = 0f
                while (distance < pathMeasure.length) {
                    pathMeasure.getPosTan(distance, pos, null)
                    points.add(Pair(pos[0], pos[1]))
                    distance += 5f
                }

                PathData(points, paint.xfermode != null, paint.strokeWidth)
            }
        }
        return gson.toJson(stackList)
    }

    // Chuyển đổi JSON thành Stack<List<Pair<Path, Paint>>>
    fun jsonToStack(json: String): Stack<List<Pair<Path, Paint>>> {
        val type = object : TypeToken<List<List<PathData>>>() {}.type
        val stackData: List<List<PathData>> = gson.fromJson(json, type)

        val stack = Stack<List<Pair<Path, Paint>>>()
        stackData.forEach { pathDataList ->
            val paths = pathDataList.map { data ->
                val newPath = Path()
                if (data.points.isNotEmpty()) {
                    newPath.moveTo(data.points[0].first, data.points[0].second)
                    for (point in data.points.drop(1)) {
                        newPath.lineTo(point.first, point.second)
                    }
                }

                val newPaint = Paint().apply {
                    style = Paint.Style.STROKE  // ✅ Ensures only strokes, no fills
                    xfermode = if (data.isErase) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null
                    strokeWidth = data.strokeWidth
                }

                Pair(newPath, newPaint)
            }
            stack.push(paths)
        }
        return stack
    }
}
