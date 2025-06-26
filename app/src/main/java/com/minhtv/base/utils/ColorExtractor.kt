package com.draw.animation.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View


object ColorExtractor {
    /**
     * Gets the color of a pixel at specific coordinates within a View's Bitmap.
     *
     * @param view The View to get the Bitmap from.
     * @param x    The x-coordinate of the pixel (relative to the View).
     * @param y    The y-coordinate of the pixel (relative to the View).
     * @return The color of the pixel as an integer (ARGB), or -1 if an error occurs.
     */
    fun getColorFromView(view: View, x: Int, y: Int): Int {
        // 1. Create a Bitmap from the View:
        val bitmap = getBitmapFromView(view)
            ?: return -1 // Or throw an exception, depending on your needs

        // 2. Check for null bitmap (view might not be rendered yet):

        // 3. Boundary checks (prevent out-of-bounds access):
        if (x < 0 || x >= bitmap.width || y < 0 || y >= bitmap.height) {
            bitmap.recycle() // Important to recycle if not using
            return -1 // Or throw an exception
        }

        // 4. Get the pixel color:
        try {
            val pixelColor = bitmap.getPixel(x, y)
            return pixelColor
        } finally {
            bitmap.recycle() // Recycle the bitmap to free memory! VERY IMPORTANT
        }
    }

    /**
     * Creates a Bitmap from a given View.  Handles layout and drawing.
     *
     * @param view The View to capture.
     * @return The Bitmap representation of the View, or null on failure.
     */
    private fun getBitmapFromView(view: View): Bitmap? {
        // Important: Ensure the view is laid out.
        if (view.width <= 0 || view.height <= 0) {
            // If the view hasn't been laid out yet, we need to force it.
            // The view must have a parent for this to work.
            if (view.measuredWidth <= 0 || view.measuredHeight <= 0) {
                view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }

            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            if (view.width <= 0 || view.height <= 0) {
                return null //View is still not ready.
            }
        }

        // Create the bitmap.
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)


        // Create a Canvas that draws to the Bitmap.
        val canvas = Canvas(bitmap)

        // Draw the view onto the canvas (and thus the bitmap).
        view.draw(canvas)
        return bitmap
    }


    //---Example Usage (in an Activity)---
    fun exampleUsage(yourView: View) {
        val xCoordinate = 100 // Example x-coordinate
        val yCoordinate = 50 // Example y-coordinate

        val color = getColorFromView(yourView, xCoordinate, yCoordinate)

        if (color != -1) {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            val alpha = Color.alpha(color)

            // Do something with the color components (red, green, blue, alpha)
            // For instance display color, Log, etc.
            // Log.d("Color", "Red: " + red + ", Green: " + green + ", Blue: " + blue + ", Alpha: " + alpha);
        } else {
            // Log.e("Color", "Could not retrieve color from view.");
        }
    }
}