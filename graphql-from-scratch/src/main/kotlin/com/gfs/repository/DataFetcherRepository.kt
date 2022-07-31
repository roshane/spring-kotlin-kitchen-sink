package com.gfs.repository

import com.gfs.datafetcher.DataFetcherWithMetaData
import com.gfs.http.CreateDataFetcherRequest
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DataFetcherRepository {

    companion object {
        private val dataFetcherMap = ConcurrentHashMap<String, DataFetcherWithMetaData<*>>()
    }

    fun <R> add(id: String, fetcher: DataFetcherWithMetaData<R>) {
        dataFetcherMap[id] = fetcher
    }

    fun allMetaData(): List<CreateDataFetcherRequest> = dataFetcherMap.values
        .toList()
        .map { it.metaData() }

    fun <R> find(id: String): Optional<DataFetcherWithMetaData<R>> =
        Optional.ofNullable(dataFetcherMap[id])
            .map { it as DataFetcherWithMetaData<R> }

}