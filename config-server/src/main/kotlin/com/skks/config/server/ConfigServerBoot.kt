package com.skks.config.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@EnableConfigServer
@SpringBootApplication
class ConfigServerBoot

fun main(args: Array<String>) {
    runApplication<ConfigServerBoot>(*args)
}