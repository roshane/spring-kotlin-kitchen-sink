package com.example.skks.provider.impl

import com.example.skks.HttpModels.User
import com.example.skks.http.JsonPlaceholderClient
import com.example.skks.provider.UserProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class UserProviderImpl(private val client: JsonPlaceholderClient) : UserProvider {

    private val logger = LoggerFactory.getLogger(UserProvider::class.java)
    private lateinit var cache: Map<Int, List<User>>

    @PostConstruct
    fun postConstruct() {
        cache = client
            .getAllUsers()
            .groupBy { it.id }
    }

    override fun getAll(): List<User> {
        logger.info("get all users")
        return cache
            .values
            .toList()
            .flatten()
    }

    override fun getById(userId: Int): User? {
        logger.info("get user by userID: {}", userId)
        return cache[userId]?.get(0)
    }
}