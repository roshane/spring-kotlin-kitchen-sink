package com.example.skks.provider

import com.example.skks.HttpModels.Comment

interface CommentProvider {

    fun getAll(): List<Comment>

    fun getAllByPostId(postId: Int): List<Comment>
}