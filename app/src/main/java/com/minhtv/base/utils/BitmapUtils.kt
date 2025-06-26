package com.draw.animation.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import java.io.IOException
import kotlin.math.roundToInt

object BitmapUtils {

    var croppedBitmap : Bitmap? = null

    fun loadBitmapFromInternalStorage(filePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Bên trong Activity hoặc Fragment, nơi bạn có quyền truy cập vào context và contentResolver
    fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            // Mở một InputStream từ Uri
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { // Đảm bảo InputStream được đóng sau khi sử dụng
                // Giải mã InputStream thành Bitmap
                BitmapFactory.decodeStream(it)
            }
        } catch (e: IOException) {
            // Xử lý lỗi (ví dụ: không tìm thấy file, lỗi I/O)
            AppLogger.e( "Error loading bitmap from URI: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun getBitmapAspectRatio(bitmap: Bitmap): Float {
        return bitmap.width.toFloat() / bitmap.height.toFloat()
    }

    fun isAspectRatio(bitmap: Bitmap, targetRatio: Float, tolerance: Float = 0.05f): Boolean {
        val aspectRatio = getBitmapAspectRatio(bitmap)
        return kotlin.math.abs(aspectRatio - targetRatio) < tolerance
    }

    fun flipBitmap(bitmap: Bitmap, isHorizontal: Boolean): Bitmap {
        val matrix = Matrix()
        val flipValue = if (isHorizontal) -1f else 1f
        matrix.preScale(flipValue, -flipValue)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle) // Xoay ảnh theo góc được chỉ định
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    /**
     * Scales a Bitmap to a specified ratio of the given dimensions, preserving aspect ratio.
     *
     * @param sourceBitmap The original Bitmap to scale.
     * @param containerWidth The width of the container (e.g., ImageView width in pixels).
     * @param containerHeight The height of the container (e.g., ImageView height in pixels).
     * @param ratio The scaling ratio (e.g., 0.5 for half size, 0.75 for 3/4 size, etc.).
     * @return The scaled Bitmap. Returns null if sourceBitmap is null.
     */
    fun scaleBitmapToRatio(sourceBitmap: Bitmap?, containerWidth: Int, containerHeight: Int, ratio: Double): Bitmap? {
        if (sourceBitmap == null) {
            return null
        }

        val targetWidth = (containerWidth * ratio).roundToInt()
        val targetHeight = (containerHeight * ratio).roundToInt()

        return Bitmap.createScaledBitmap(sourceBitmap, targetWidth, targetHeight, true)
    }
    /**
     * Sets the transparency of a Bitmap.
     *
     * @param sourceBitmap The original Bitmap.
     * @param alpha The alpha value (0 for fully transparent, 255 for fully opaque).
     * @return A new Bitmap with the applied transparency, or null if the input is null.
     */
    fun setTransparency(sourceBitmap: Bitmap?, alpha: Int): Bitmap? {
        if (sourceBitmap == null) {
            return null
        }

        // Create a mutable copy of the bitmap
        val transparentBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Create a Canvas to draw on the mutable bitmap
        val canvas = Canvas(transparentBitmap)

        // Create a Paint with the specified alpha value
        val alphaPaint = Paint().apply {
            this.alpha = alpha
        }

        // Draw the original bitmap onto the mutable bitmap with the alpha paint
        canvas.drawBitmap(sourceBitmap, 0f, 0f, alphaPaint)

        return transparentBitmap
    }
}