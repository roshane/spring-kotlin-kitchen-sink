package com.example.skks.provider

import com.example.skks.HttpModels.Post

interface PostProvider {
    fun getAll(): List<Post>

    fun getAllByUser(userId: Int): List<Post>

    fun getAllByUserIds(userIdList:List<Int>):Map<Int,List<Post>>
}