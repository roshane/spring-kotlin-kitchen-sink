package com.example.skks.provider.impl

import com.example.skks.HttpModels.Post
import com.example.skks.http.JsonPlaceholderClient
import com.example.skks.provider.PostProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PostProviderImpl(private val client: JsonPlaceholderClient) : PostProvider {

    private val logger = LoggerFactory.getLogger(PostProvider::class.java)
    private lateinit var cache: Map<Int, List<Post>>

    @PostConstruct
    fun postConstruct() {
        cache = client
            .getAllPosts()
            .groupBy { it.userId }
    }

    override fun getAll(): List<Post> {
        logger.info("getAll")
        return cache
            .values
            .flatten()
    }


    override fun getAllByUser(userId: Int): List<Post> {
        logger.info("getAllByUser({})", userId)
        return cache[userId] ?: emptyList()
    }

}