package com.example.skks.controller

import com.example.skks.core.JsonPlaceholderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController(private val jsonPlaceholderService: JsonPlaceholderService) {

    @GetMapping("/")
    fun index() = mapOf("message" to "I'm fine")

    @GetMapping("/users")
    fun user() = jsonPlaceholderService.users()

    @GetMapping("/posts")
    fun posts() = jsonPlaceholderService.posts()
}