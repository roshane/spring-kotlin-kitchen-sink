package com.example.skks.time.client

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.RequestEntity
import org.springframework.web.client.exchange
import java.net.URI
import java.util.*

class RestClientRunner {

    companion object {
        private val restTemplate = RestTemplateBuilder().build()
        private val logger = LoggerFactory.getLogger(RestClientRunner::class.java)
    }

    @Test
    fun `RestTemplate should download the file successfully`() {
        val httpTarget = "https://raw.githubusercontent.com/roshane/scheduler-service/master/Dockerfile"
        val requestEntity = RequestEntity.get(URI(httpTarget)).build()
        val responseEntity = restTemplate.exchange<ByteArray>(requestEntity)
        when (responseEntity.statusCode.is2xxSuccessful) {
            true -> {
                Optional.ofNullable(responseEntity.body)
                    .map {
                        logger.info("File content: \n{}\n", String(it))
                    }
            }

            false -> Assertions.fail()
        }
    }
}