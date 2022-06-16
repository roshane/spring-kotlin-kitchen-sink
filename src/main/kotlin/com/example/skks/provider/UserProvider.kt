package com.example.skks.provider

import com.example.skks.HttpModels.User

interface UserProvider {

    fun getAll(): List<User>

    fun getById(userId: Int): User?
}