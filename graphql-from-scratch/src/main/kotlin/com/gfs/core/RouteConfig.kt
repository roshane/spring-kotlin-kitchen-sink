package com.gfs.core

import com.gfs.handler.DataFetcherHandler
import com.gfs.handler.GraphQLHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.web.servlet.function.router

@Configuration
class RouteConfig {

    @Bean
    fun routes(
        graphqlHandler: GraphQLHandler,
        dataFetcherHandler: DataFetcherHandler
    ) = router {
        accept(TEXT_HTML).nest {
            GET("/graphiql") { ok().render("index") }
        }

        accept(APPLICATION_JSON).nest {
            POST("/graphql", graphqlHandler::execute)
            POST("/graphql/refresh", graphqlHandler::refresh)
            POST("/graphql/data-fetchers/{type}", dataFetcherHandler::createNew)
            GET("/graphql/data-fetchers", dataFetcherHandler::findAll)
            GET("/graphql/data-fetchers/{id}", dataFetcherHandler::findById)
        }

        resources("/**", ClassPathResource("static/**"))
    }
}