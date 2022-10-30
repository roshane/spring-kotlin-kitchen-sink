package com.example.skks.orca.http

import com.example.skks.HttpModels.Comment
import com.example.skks.HttpModels.Post
import com.example.skks.HttpModels.User
import com.example.skks.provider.CommentProvider
import com.example.skks.provider.PostProvider
import com.example.skks.provider.UserProvider
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuxiliaryController(
    private val postProvider: PostProvider,
    private val userProvider: UserProvider,
    private val commentProvider: CommentProvider
) {

    private val logger = LoggerFactory.getLogger(AuxiliaryController::class.java)

    @GetMapping("/posts")
    fun getPosts(): List<Post> = postProvider.getAll()

    @GetMapping("/users")
    fun getUser(): List<User> = userProvider.getAll()

    @GetMapping("/comments")
    fun getComments(): List<Comment> = commentProvider.getAll()
}