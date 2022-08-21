package com.gfs.core

import com.gfs.datafetcher.ApplicationDataFetcher
import com.gfs.http.CreateDataFetcherRequest
import com.gfs.repository.DataFetcherRepository
import graphql.schema.DataFetchingEnvironment
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderRegistry
import org.dataloader.MappedBatchLoader
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.concurrent.CompletableFuture
import kotlin.reflect.safeCast

typealias GenericObjectType = Map<String,Any>
typealias GenericListOfObjectType = List<GenericObjectType>

class DataLoaderBootstrap(
    private val repository: DataFetcherRepository,
    private val restTemplate: RestTemplate,
    private val dataLoaderRegistry: DataLoaderRegistry
) : ApplicationRunner {

    companion object {
        private val LOGGER = LoggerFactory.getLogger("data-loaders")
    }

    private val userDataFetcher = object : ApplicationDataFetcher<GenericListOfObjectType> {
        override fun metaData(): CreateDataFetcherRequest {
            TODO("Not yet implemented")
        }

        override fun get(environment: DataFetchingEnvironment): GenericListOfObjectType {
            val url = "https://jsonplaceholder.typicode.com/users"
            val response = restTemplate.getForEntity(url, List::class.java)
            return when (response.statusCode) {
                HttpStatus.OK -> response.body as GenericListOfObjectType
                else -> throw RuntimeException("Error fetch data from [$url] - $response")
            }
        }
    }

//    private val postsByUserDataFetcher = object: ApplicationDataFetcher<GenericListOfObjectType>{
//        override fun metaData(): CreateDataFetcherRequest {
//            TODO("Not yet implemented")
//        }
//
//        override fun get(environment: DataFetchingEnvironment): GenericListOfObjectType {
//            val user = environment.getSource<GenericObjectType>()
//        }
//    }

    private val postsBatchLoader = MappedBatchLoader<Map<String, Any>, Map<String, Any>> {
        val userIdList = it.map { user -> String::class.safeCast(user["id"]) }.toList()
        LOGGER.info("fetch users for ids: {}", userIdList)
        CompletableFuture.supplyAsync { emptyMap() }
    }

    override fun run(args: ApplicationArguments?) {
        repository.add("users", userDataFetcher)
        dataLoaderRegistry.register(POST_BY_USER_DATA_LOADER, DataLoaderFactory.newMappedDataLoader(postsBatchLoader))
    }
}
