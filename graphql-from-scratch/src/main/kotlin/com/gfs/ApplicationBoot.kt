package com.gfs

import com.gfs.core.GqlApplicationContextInitializer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ApplicationBoot

fun main(args: Array<String>) {
    with(SpringApplication(ApplicationBoot::class.java)) {
        addInitializers(GqlApplicationContextInitializer())
        run(*args)
    }
}