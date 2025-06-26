package com.draw.animation.utils

import com.draw.animation.models.FlatBuffers.*
import com.draw.animation.models.flatModels.toPicture

object PictureHashMap {
    // Global variable to hold the picture cache (in-memory only)
    val pictureCache = mutableMapOf<Int, com.draw.animation.models.flatModels.Picture>()
    fun buildPictureCache(file: File) {
        pictureCache.clear() // Clear any existing data in the cache.

        for (i in 0 until file.picturesLength) {
            val fbPicture = file.pictures(i)
            fbPicture?.let {
                val picture = it.toPicture()
                pictureCache[picture.id] = picture // Add to the map. Key is the ID.
            }
        }
    }

    fun getPictureById(id: Int): com.draw.animation.models.flatModels.Picture? {
        return pictureCache[id] // Direct lookup by ID. Returns null if not found.
    }

    fun addOrUpdatePicture(picture: com.draw.animation.models.flatModels.Picture) {
        pictureCache[picture.id] = picture
    }

    fun removePicture(id: Int){
        pictureCache.remove(id)
    }
}