package com.example.skks.provider.impl

import com.example.skks.HttpModels.Comment
import com.example.skks.http.JsonPlaceholderClient
import com.example.skks.provider.CommentProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CommentProviderImpl(private val client: JsonPlaceholderClient) : CommentProvider {

    private val logger = LoggerFactory.getLogger(CommentProvider::class.java)
    private lateinit var cache: Map<Int, List<Comment>>

    @PostConstruct
    fun postConstruct() {
        cache = client
            .getAllComments()
            .groupBy { it.postId }
    }

    override fun getAll(): List<Comment> {
        logger.info("getAll")
        return cache.values.flatten()
    }

    override fun getAllByPostId(postId: Int): List<Comment> {
        logger.info("getAllByPostId({})", postId)
        return cache[postId] ?: emptyList()
    }

    override fun getAllByPosts(postIdList: List<Int>): List<Comment> {
        val postIdSet = postIdList.toSet()
        return cache
            .values
            .flatten()
            .filter { postIdSet.contains(it.postId) }
    }
}