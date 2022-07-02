package com.example.skks.time.client

import com.netflix.discovery.EurekaClient
import feign.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@Configuration
class TimeClientBoot {

    @Bean
    fun feignLoggerLevel(): Logger.Level = Logger.Level.BASIC
}

fun main(args: Array<String>) {
    runApplication<TimeClientBoot>(*args)
}


@FeignClient(value = "\${servers.time-server.name}", qualifier = "TimeServerFeignClient")
interface TimeServerFeignClient {

    @GetMapping("/time")
    fun timeNow(): String
}

@Component
class TimeKeeper(
    private val eurekaClient: EurekaClient,
    private val timeServerFeignClient: TimeServerFeignClient
) : ApplicationRunner {
    private val logger = LoggerFactory.getLogger(TimeKeeper::class.java)
    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    private val initialDelay = 500L
    private val period = 500L

    @Value("\${servers.time-server.name}")
    lateinit var timeServerName: String

    init {
        Runtime
            .getRuntime()
            .addShutdownHook(object : Thread() {
                override fun run() {
                    logger.info("Shutting down scheduler...")
                    scheduler.shutdown()
                }
            })
    }


    override fun run(args: ApplicationArguments?) {
        scheduler.scheduleAtFixedRate(
            {
                try {
                    val timeServers = eurekaClient.getApplication(timeServerName)
//                logger.info("TimeServers: {}", timeServers)
                    logger.info("FeignClient response: {}", timeServerFeignClient.timeNow())
                } catch (ex: Exception) {
                    logger.error("Error", ex)
                }
            },
            initialDelay,
            period,
            MILLISECONDS
        )
    }
}