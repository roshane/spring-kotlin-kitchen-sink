package com.gfs.datafetcher

import com.gfs.repository.DataFetcherRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class DelegatedDataFetcher<R>(
    private val repository: DataFetcherRepository
) : DataFetcher<R> {

    override fun get(environment: DataFetchingEnvironment?): R {
        val fieldName = environment?.fieldDefinition?.name.orEmpty()
        val optionalDataFetcher = repository.find<R>(fieldName)
        return optionalDataFetcher
            .map { it.get(environment) }
            .orElseThrow { RuntimeException("No DataFetcher found to resolve query[$fieldName]") }
    }
}