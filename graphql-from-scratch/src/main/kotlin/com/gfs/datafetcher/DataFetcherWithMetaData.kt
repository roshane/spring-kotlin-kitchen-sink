package com.gfs.datafetcher

import com.gfs.http.CreateDataFetcherRequest
import graphql.schema.DataFetcher

interface DataFetcherWithMetaData<T> : DataFetcher<T> {

    fun metaData(): CreateDataFetcherRequest //TODO create metadata class
}