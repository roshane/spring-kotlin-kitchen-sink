package com.gfs.datafetcher

import com.gfs.repository.DataFetcherRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory

class DelegatedDataFetcher<R>(
    private val repository: DataFetcherRepository
) : DataFetcher<R> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DelegatedDataFetcher::class.java)
    }

    override fun get(environment: DataFetchingEnvironment?): R {
        val maybeProvider = repository.find<R>(environment?.fieldDefinition?.name.orEmpty())
        return maybeProvider
            .map { it.get(environment) }
            .orElseThrow { RuntimeException("No DataFetcher found to resolve query[echo]") }
    }
}