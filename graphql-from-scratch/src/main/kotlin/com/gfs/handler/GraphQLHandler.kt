package com.gfs.handler

import com.gfs.core.RefreshableGraphQLProvider
import com.gfs.http.Http
import com.gfs.http.Http.json
import graphql.ExecutionInput
import graphql.execution.ExecutionId
import org.dataloader.DataLoaderRegistry
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok
import org.springframework.web.servlet.function.ServerResponse.status

class GraphQLHandler(
    private val graphQLProvider: RefreshableGraphQLProvider,
    private val dataLoaderRegistry: DataLoaderRegistry
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(GraphQLHandler::class.java)
    }

    fun execute(req: ServerRequest): ServerResponse {
        val graphQLRequest = req.body(Http.GraphQLRequest::class.java)
        val input = ExecutionInput.Builder()
            .query(graphQLRequest.query)
            .operationName(graphQLRequest.operationName)
            .variables(graphQLRequest.variables)
            .dataLoaderRegistry(dataLoaderRegistry)
            .executionId(ExecutionId.generate())
            .build()
        return ok().json()
            .body(graphQLProvider.execute(input).toSpecification())
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