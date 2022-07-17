package com.example.skks.dgql

import com.example.skks.dgql.core.DefaultSchemaStatusProvider
import com.example.skks.dgql.core.SchemaStatusProvider
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.internal.DefaultDgsQueryExecutor
import com.netflix.graphql.dgs.webmvc.autoconfigure.DgsWebMvcAutoConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@SpringBootApplication(
    exclude = [DgsAutoConfiguration::class, DgsWebMvcAutoConfiguration::class]
)
class DynamicGqlServiceBoot

fun main(args: Array<String>) {
    runApplication<DynamicGqlServiceBoot>(*args)
}

@RefreshScope
@RestController
class MessageController(private val schemaStatusProvider: SchemaStatusProvider) {

    @Value("\${message.greeting}")
    lateinit var greeting: String

    @GetMapping("/", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun index(): String = greeting

    @GetMapping("/refresh-schema")
    fun shouldRefresh(): String = schemaStatusProvider.shouldRefresh().toString()

    @PostMapping("/refresh-schema")
    fun schemaUpdated() = schemaStatusProvider.notifyRefreshed()
}

@Configuration
class AppConfig {

    @Value("\${application.schema.routine.check.delay}")
    lateinit var schemaRoutineCheckDelay: String

    @Value("\${application.schema.routine.check.period}")
    lateinit var schemaRoutineCheckPeriod: String

    @Value("\${schema.locations}")
    lateinit var csvSchemaLocations: String

    @Bean
    fun restTemplate(builder: RestTemplateBuilder) = builder.build()

    @Bean
    fun reloadSchemaIndicator(schemaStatusProvider: SchemaStatusProvider): DefaultDgsQueryExecutor.ReloadSchemaIndicator =
        DefaultDgsQueryExecutor.ReloadSchemaIndicator {
            schemaStatusProvider.shouldRefresh()
        }

    @Bean
    fun schemaStatusProvider(): SchemaStatusProvider {
        val resolver = PathMatchingResourcePatternResolver(Thread.currentThread().contextClassLoader)
        val schemas = csvSchemaLocations
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { resolver.getResource(it) }
            .toSet()
        val delay = schemaRoutineCheckDelay.toLong()
        val period = schemaRoutineCheckPeriod.toLong()
        return DefaultSchemaStatusProvider(delay, period) { schemas }
    }
}


@DgsComponent
class UserDataFetcher(private val restTemplate: RestTemplate) {

    @DgsQuery
    fun users(): List<HttpModels.User> {
        val users = restTemplate
            .getForEntity("https://jsonplaceholder.typicode.com/users", Array<HttpModels.User>::class.java)
        return users.body?.toList().orEmpty()
    }
}


object HttpModels {

    data class Todo(
        val id: Int,
        val userId: Int,
        val title: String,
        val completed: Boolean
    )

    data class User(
        val id: Int,
        val name: String,
        val username: String,
        val email: String,
        val address: Address,
        val phone: String,
        val website: String,
        val company: Company
    )

    data class Post(
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String
    )

    data class Comment(
        val id: Int,
        val postId: Int,
        val name: String,
        val email: String,
        val body: String
    )

    data class Address(
        val street: String,
        val suite: String,
        val city: String,
        val zipcode: String,
        val geo: Geo
    )

    data class Geo(
        val lat: String,
        val lng: String
    )

    data class Company(
        val name: String,
        val catchPhrase: String,
        val bs: String
    )
}