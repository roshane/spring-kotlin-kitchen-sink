package com.example.skks.orca.http

import org.springframework.web.servlet.function.router

class ApiRoutes(
    private val homeHandler: HomeHandler,
    private val integrationHandler: IntegrationHandler
) {

    fun appRoutes() = router {
        "/api".nest {
            GET("", homeHandler::index)
            "/integrations".nest {
                GET("/",integrationHandler::findAll)
                GET("/{id}", integrationHandler::find)
                POST("/{id}", integrationHandler::executeFlow)
                POST("/", integrationHandler::createNew)
            }
        }
    }
}