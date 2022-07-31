package com.gfs.http

import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse

object Http {

    fun ServerResponse.BodyBuilder.json() = contentType(MediaType.APPLICATION_JSON)

    data class GraphQLRequest(
        val query: String,
        val operationName: String?,
        val variables: Map<String, String> = emptyMap()
    )

    data class CreateHttpDataFetcherRequest(
        val gqlField: String,
        val resourceUrl: String,
        val requestBodyParameters: Map<String, String> = emptyMap(),
        val requestQueryParameters: Map<String, String> = emptyMap(),
    ) : CreateDataFetcherRequest

    data class CreateSimpleDataFetcherRequest(
        val gqlField: String,
        val parameters: Map<String, String> = emptyMap()
    ): CreateDataFetcherRequest

    data class CreateDBDataFetcherRequest(
        val gqlField: String,
        val dbQuery: String
    ) : CreateDataFetcherRequest
}