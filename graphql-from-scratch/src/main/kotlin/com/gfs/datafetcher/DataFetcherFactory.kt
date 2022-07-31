//package com.gfs.datafetcher
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.gfs.datafetcher.DynamicConfig.DataFetcherConfig
//import com.gfs.datafetcher.DynamicConfig.DataFetcherJsonConfig
//import graphql.schema.DataFetcher
//
//class DataFetcherFactory(private val objectMapper: ObjectMapper) {
//
//    fun parse(schema: String): DataFetcherJsonConfig  {
//        val parsedConfig = objectMapper
//            .readValue(schema, DataFetcherJsonConfig::class.java)
//        return parsedConfig
//    }
//
//    fun <T> build(config: DataFetcherConfig): DataFetcher<T> {
//        val result = DataFetcher {
//            config.arguments.map { arg ->
//                val isRequired = arg.required.toBoolean()
//                if(isRequired){
//                    it.getArgument(arg.name)
//                }
//            }
//        }
//    }
//
//}