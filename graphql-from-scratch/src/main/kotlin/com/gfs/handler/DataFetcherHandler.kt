package com.gfs.handler

import com.gfs.datafetcher.DataFetcherType
import com.gfs.datafetcher.ApplicationDataFetcher
import com.gfs.http.CreateDataFetcherRequest
import com.gfs.http.Http
import com.gfs.http.Http.json
import com.gfs.repository.DataFetcherRepository
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok
import org.springframework.web.servlet.function.ServerResponse.status

class DataFetcherHandler(
    private val dataFetcherRepository: DataFetcherRepository,
    private val restTemplate: RestTemplate
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DataFetcherHandler::class.java)
    }

    fun createNew(req: ServerRequest): ServerResponse {
        try {
            when (DataFetcherType.valueOf(req.pathVariable("type"))) {
                DataFetcherType.DB -> createDataFetcher(req.body(Http.CreateDBDataFetcherRequest::class.java))
                DataFetcherType.HTTP -> createDataFetcher(req.body(Http.CreateHttpDataFetcherRequest::class.java))
                DataFetcherType.SIMPLE -> createDataFetcher(req.body(Http.CreateSimpleDataFetcherRequest::class.java))
            }
            return ok()
                .json()
                .body(mapOf("status" to "success"))
        } catch (ex: Exception) {
            LOGGER.error("Error", ex)
            return status(HttpStatus.INTERNAL_SERVER_ERROR).json().body(
                mapOf(
                    "status" to "failed",
                    "message" to ex.message
                )
            )
        }
    }

    fun findById(req: ServerRequest): ServerResponse {
        val id = req.pathVariable("id")
        val dataFetcher = dataFetcherRepository.find<Any>(id)
        return dataFetcher.map { it.metaData() }
            .map { ok().json().body(mapOf("meta-data" to it)) }
            .orElseGet { status(HttpStatus.NOT_FOUND).build() }
    }

    fun findAll(req: ServerRequest): ServerResponse = ok()
        .json()
        .body(dataFetcherRepository.findAll())

    private fun createDataFetcher(req: Http.CreateSimpleDataFetcherRequest) {
        LOGGER.info("new data-fetcher create request: {}", req)

        val dataFetcher = object : ApplicationDataFetcher<String> {

            override fun metaData(): CreateDataFetcherRequest = req


            override fun get(environment: DataFetchingEnvironment?): String {
                val name = environment?.getArgumentOrDefault("name", "").orEmpty()
                return "Hello $name"
            }
        }
        dataFetcherRepository.add(req.gqlField, dataFetcher)
    }

    private fun createDataFetcher(req: Http.CreateHttpDataFetcherRequest) {
        LOGGER.info("new data-fetcher create request: {}", req)

        val dataFetcher = object : ApplicationDataFetcher<Any> {
            override fun metaData(): CreateDataFetcherRequest = req

            override fun get(environment: DataFetchingEnvironment?): Any {
                //resolve http parameters body / query / path
                val response = restTemplate.getForEntity(req.resourceUrl, Any::class.java)
                LOGGER.info("fetch result {} : {}", req.resourceUrl, response.statusCode)
                return response.body ?: throw RuntimeException("Unable to fetch data")
            }


        }
        dataFetcherRepository.add(req.gqlField, dataFetcher)
    }

    private fun createDataFetcher(req: Http.CreateDBDataFetcherRequest) {
        TODO()
    }
}
