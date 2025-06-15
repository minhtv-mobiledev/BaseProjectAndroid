package com.its.baseapp.its.data.network.service

import com.its.baseapp.its.model.User
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("posts")
    suspend fun getListUser(@Query("userId") userId: Int): List<User>

}