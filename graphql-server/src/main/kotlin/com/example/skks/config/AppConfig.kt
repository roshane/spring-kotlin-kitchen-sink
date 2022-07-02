package com.example.skks.config

import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig(private val restTemplateBuilder: RestTemplateBuilder) {

    private val logger = LoggerFactory.getLogger("outbound-http-client")

    @Bean
    fun outboundClientHttpRequestInterceptor(): ClientHttpRequestInterceptor {
        return ClientHttpRequestInterceptor { request, body, execution ->
            val startMilli = System.currentTimeMillis()
            val response = execution.execute(request, body)
            logger.info(
                "{} {} took {} ms",
                request.method,
                request.uri.toString(),
                (System.currentTimeMillis() - startMilli)
            )
            response
        }
    }

    @Bean
    fun restTemplate(interceptor: ClientHttpRequestInterceptor): RestTemplate = restTemplateBuilder
        .additionalInterceptors(interceptor)
        .build()
}