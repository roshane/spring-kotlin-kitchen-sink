package com.example.kks

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.stereotype.Controller
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.GetMapping

@SpringBootApplication
@EnableEurekaServer
@Controller
class EurekaServerBoot {

    @Value("\${app.password-file-path}")
    lateinit var passwordPath: String

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ok(mapOf("status" to "healthy"))
    }

    @GetMapping("/password")
    fun password(): ResponseEntity<String> {
        return ok(String(this.javaClass.getResourceAsStream(passwordPath)!!.readAllBytes()))
    }

}

fun main(args: Array<String>) {
    runApplication<EurekaServerBoot>(*args)
}


