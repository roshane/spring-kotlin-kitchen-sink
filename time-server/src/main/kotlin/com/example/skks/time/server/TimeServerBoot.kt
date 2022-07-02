package com.example.skks.time.server

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

@SpringBootApplication
@EnableEurekaClient
class TimeServerBoot

fun main(args: Array<String>) {
    runApplication<TimeServerBoot>(*args)
}

@RestController
class TimeController {

    private val logger = LoggerFactory.getLogger(TimeController::class.java)

    @GetMapping("/time")
    fun time(): String {
        logger.info("someone requested time...")
        return ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
    }
}