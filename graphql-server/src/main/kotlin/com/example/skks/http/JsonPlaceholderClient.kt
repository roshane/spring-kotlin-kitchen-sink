package com.example.skks.http

import com.example.skks.HttpModels.Comment
import com.example.skks.HttpModels.Post
import com.example.skks.HttpModels.User
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class JsonPlaceholderClient(private val restTemplate: RestTemplate) {

    private val API_BASE_URL = "https://jsonplaceholder.typicode.com"

    fun getAllPosts(): Array<Post> =
        restTemplate
            .getForEntity("${API_BASE_URL}/posts", Array<Post>::class.java)
            .body ?: emptyArray()

    fun getAllUsers(): Array<User> =
        restTemplate
            .getForEntity("${API_BASE_URL}/users", Array<User>::class.java)
            .body ?: emptyArray()

    fun getAllComments(): Array<Comment> =
        restTemplate
            .getForEntity("${API_BASE_URL}/comments", Array<Comment>::class.java)
            .body ?: emptyArray()
}