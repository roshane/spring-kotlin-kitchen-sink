package com.example.skks.config

import com.example.skks.core.JsonPlaceholderService
import com.example.skks.core.TransformerService
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.tools.generic.JsonTool
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {

    @Bean
    fun velocityEngine(): VelocityEngine = VelocityEngine().apply {
        addProperty("resource.loaders", "class")
        addProperty(
            "resource.loader.class.class",
            " org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
        )
    }

    @Bean
    fun jsonTool(): JsonTool =
        JsonTool()

    @Bean
    fun transformerService(
        velocityEngine: VelocityEngine,
        jsonTool: JsonTool
    ): TransformerService = TransformerService(velocityEngine, jsonTool)

    @Bean
    fun restTemplate(builder: RestTemplateBuilder) =
        builder.build()

    @Bean
    fun jsonPlaceholderService(restTemplate: RestTemplate) =
        JsonPlaceholderService(restTemplate)
}