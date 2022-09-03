package com.gfs.core

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

class RemoteResourceLoader(
    private val restTemplate: RestTemplate = RestTemplateBuilder().build()
) {

    fun getResource(location: String): String {
        try {
            return restTemplate.getForObject(location)
        } catch (e: Exception) {
            throw RuntimeException("Error loading resource from $location", e)
        }
    }

}