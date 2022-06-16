package com.example.skks.provider

import com.example.skks.HttpModels.Comment
import com.example.skks.HttpModels.Post

interface CommentProvider {
    fun getAll(): List<Comment>

    fun getAllByPostId(postId: Int): List<Comment>

    fun getAllByPosts(postIdList: List<Int>): List<Comment>
}