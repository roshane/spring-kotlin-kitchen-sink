package com.example.skks.dgql.core

interface SchemaStatusProvider {

    fun shouldRefresh(): Boolean

    fun notifyRefreshed()
}