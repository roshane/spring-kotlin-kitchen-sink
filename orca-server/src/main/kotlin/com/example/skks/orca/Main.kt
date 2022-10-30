package com.example.skks.orca

import com.example.skks.orca.config.AppConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.CrossOrigin

@CrossOrigin
@SpringBootApplication
class Main

fun main(args: Array<String>) {
    with(SpringApplication(Main::class.java)) {
        addInitializers(AppConfig())
        run(*args)
    }
}