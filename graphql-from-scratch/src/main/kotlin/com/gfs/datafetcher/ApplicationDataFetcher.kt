package com.gfs.datafetcher

import com.gfs.http.CreateDataFetcherRequest
import graphql.schema.DataFetcher

interface ApplicationDataFetcher<T> : DataFetcher<T> {

    fun metaData(): CreateDataFetcherRequest //TODO create metadata class
}