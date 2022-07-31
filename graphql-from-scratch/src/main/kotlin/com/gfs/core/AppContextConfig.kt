package com.gfs.core

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gfs.datafetcher.DelegatedDataFetcher
import com.gfs.handler.DataFetcherHandler
import com.gfs.handler.GraphQLHandler
import com.gfs.repository.DataFetcherRepository
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.support.beans
import org.springframework.web.client.RestTemplate

object AppContextConfig {

    private val location =
        "file:///D:/IdeaProjects/WINDOWS/KOTLIN/spring-kotlin-kitchen-sink/graphql-from-scratch/src/main/resources/schema/root.graphqls"

    val beans = beans {
        bean("schemaLocations") {
            listOf(location)
        }
        bean<RefreshableGraphQLProvider> {
            GraphQLProvider(ref("schemaLocations"), ref())
        }
        bean<GraphQLHandler>()
        bean<DataFetcherHandler>()
        bean<DataFetcherRepository>()
        bean<DelegatedDataFetcher<*>>()
        bean { jacksonObjectMapper() }
        bean<RestTemplate>() {
            val builder = ref<RestTemplateBuilder>()
            builder.build()
        }
    }
}