package com.gfs

import com.gfs.core.AppContextConfig.beans
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.support.GenericApplicationContext

@SpringBootApplication
class ApplicationBoot

fun main(args: Array<String>) {
    with(SpringApplication(ApplicationBoot::class.java)) {
        addInitializers({ ctx -> beans.initialize(ctx as GenericApplicationContext) })
        run(*args)
    }
}