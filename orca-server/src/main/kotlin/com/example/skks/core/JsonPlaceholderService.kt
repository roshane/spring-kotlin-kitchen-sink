package com.example.skks.core

import org.springframework.web.client.RestTemplate

class JsonPlaceholderService(private val restTemplate: RestTemplate) {

    private val apiBaseUrl = "https://jsonplaceholder.typicode.com"

    fun users(): String =
        restTemplate.getForObject("$apiBaseUrl/users", String::class.java).orEmpty()

    fun posts(): String =
        restTemplate.getForObject("$apiBaseUrl/posts", String::class.java).orEmpty()
}