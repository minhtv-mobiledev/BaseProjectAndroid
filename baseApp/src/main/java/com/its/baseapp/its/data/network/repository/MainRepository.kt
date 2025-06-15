package com.its.baseapp.its.data.network.repository

import com.its.baseapp.its.model.User
import com.its.baseapp.its.data.network.service.NetworkService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MainRepository(private val networkServiceMain: NetworkService) {
    fun getListUser(userId: Int): Flow<List<User>> {
        return flow {
            emit(networkServiceMain.getListUser(userId))
        }.map { it }
    }
}