package com.gfs.handler

import com.gfs.core.RefreshableGraphQLProvider
import com.gfs.http.Http
import com.gfs.http.Http.json
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok
import org.springframework.web.servlet.function.ServerResponse.status

class GraphQLHandler(private val graphQLProvider: RefreshableGraphQLProvider) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GraphQLHandler::class.java)
    }

    fun execute(req: ServerRequest): ServerResponse {
        val graphQLRequest = req.body(Http.GraphQLRequest::class.java)
        LOGGER.info("query: {}", graphQLRequest)
        val result = graphQLProvider.execute<Any>(graphQLRequest.query)
        return ok().json().body(mapOf("data" to result))
    }

    fun refresh(req: ServerRequest): ServerResponse {
        LOGGER.info("refreshing graphql engine...")
        return try {
            graphQLProvider.refresh()
            ok().json().body(mapOf("status" to "success"))
        } catch (ex: Exception) {
            LOGGER.error("Error", ex)
            status(HttpStatus.INTERNAL_SERVER_ERROR)
                .json()
                .body(ex.message.orEmpty())
        }
    }

}