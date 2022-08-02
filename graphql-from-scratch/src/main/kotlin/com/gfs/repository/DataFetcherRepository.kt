package com.gfs.repository

import com.gfs.datafetcher.ApplicationDataFetcher
import com.gfs.http.CreateDataFetcherRequest
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DataFetcherRepository {

    companion object {
        private val dataFetcherMap = ConcurrentHashMap<String, ApplicationDataFetcher<*>>()
    }

    fun <R> add(id: String, fetcher: ApplicationDataFetcher<R>) {
        dataFetcherMap[id] = fetcher
    }

    fun findAll(): List<CreateDataFetcherRequest> = dataFetcherMap.values
        .toList()
        .map { it.metaData() }

    fun <R> find(id: String): Optional<ApplicationDataFetcher<R>> =
        Optional.ofNullable(dataFetcherMap[id])
            .map { it as ApplicationDataFetcher<R> }

}