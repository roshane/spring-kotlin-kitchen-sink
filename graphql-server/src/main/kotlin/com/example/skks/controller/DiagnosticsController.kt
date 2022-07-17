package com.example.skks.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
@Controller
@RequestMapping("/diagnostics")
class DiagnosticsController(private val appContext: ApplicationContext) {

    private val logger = LoggerFactory.getLogger(DiagnosticsController::class.java)

    open class XRunnable {
        private val timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())

        companion object {
            var instanceNumber = 0
        }

        init {
            instanceNumber += 1
            println("instance: $instanceNumber, time: $timestamp")
        }
    }

    @PostMapping("/refresh-app-context")
    @ResponseBody
    fun refreshAppContext(@Autowired @Qualifier("anonymousBean") bean: XRunnable): String {
        return "Ok"
    }

    @Bean("anonymousBean")
//    @Scope("prototype")
    @RefreshScope
    fun anonymousObj(): XRunnable = XRunnable()

}
