package com.example.skks.config

import com.example.skks.provider.CommentProvider
import com.example.skks.provider.PostProvider
import com.example.skks.provider.UserProvider
import graphql.schema.DataFetcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class GraphQLConfig(
    private val postProvider: PostProvider,
    private val userProvider: UserProvider,
    private val commentProvider: CommentProvider
) {

    @Bean
    fun runtimeWiringConfigure(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer {
            it
                .type("Query") { wiringBuilder -> wiringBuilder.dataFetcher("users", userDataFetcher()) }
                .type("Query") { wiringBuilder -> wiringBuilder.dataFetcher("comments", commentDataFetcher()) }
                .type("Query") { wiringBuilder -> wiringBuilder.dataFetcher("posts", postDataFetcher()) }

        }
    }

    private fun userDataFetcher() = DataFetcher { userProvider.getAll() }

    private fun commentDataFetcher() = DataFetcher { commentProvider.getAll() }

    private fun postDataFetcher() = DataFetcher { postProvider.getAll() }
}