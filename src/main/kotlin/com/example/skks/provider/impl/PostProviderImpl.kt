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
        logger.info("get all posts")
        return cache
            .values
            .flatten()
    }

    override fun getAllByUser(userId: Int): List<Post> {
        logger.info("get all posts by userID: {}", userId)
        return cache[userId] ?: emptyList()
    }

    override fun getAllByUserIds(userIdList: List<Int>): Map<Int, List<Post>> {
        logger.info("get all posts by userIdList: {}", userIdList)
        val postsByUserId = cache
            .values
            .flatten()
            .groupBy { it.userId }
        return userIdList
            .associateWith { postsByUserId[it].orEmpty() }
    }
}