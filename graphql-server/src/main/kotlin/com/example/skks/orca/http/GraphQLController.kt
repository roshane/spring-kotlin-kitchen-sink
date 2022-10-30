//package com.example.skks.orca.controller
//
//import com.example.skks.HttpModels.Comment
//import com.example.skks.HttpModels.Post
//import com.example.skks.HttpModels.User
//import com.example.skks.provider.CommentProvider
//import com.example.skks.provider.PostProvider
//import com.example.skks.provider.UserProvider
//import org.springframework.graphql.data.method.annotation.Argument
//import org.springframework.graphql.data.method.annotation.QueryMapping
//import org.springframework.stereotype.Controller
//
//@Controller
//class GraphQLController(
//    private val postProvider: PostProvider,
//    private val userProvider: UserProvider,
//    private val commentProvider: CommentProvider
//) {
//
//    @QueryMapping
//    fun users(): List<User> = userProvider.getAll()
//
//    @QueryMapping
//    fun userById(@Argument id: Int): User? = userProvider.getById(id)
//
//    @QueryMapping
//    fun posts(): List<Post> = postProvider.getAll()
//
//    @QueryMapping
//    fun postByUserId(@Argument userId: Int): List<Post> = postProvider.getAllByUser(userId)
//
//    @QueryMapping
//    fun comments(): List<Comment> = commentProvider.getAll()
//
//
//}