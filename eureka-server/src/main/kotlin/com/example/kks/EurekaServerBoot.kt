package com.example.kks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EurekaServerBoot

fun main(args: Array<String>) {
    runApplication<EurekaServerBoot>(*args)
}