package com.gfs.core

interface RefreshableGraphQLProvider {

    fun refresh()

    fun <T> execute(query: String): T
}