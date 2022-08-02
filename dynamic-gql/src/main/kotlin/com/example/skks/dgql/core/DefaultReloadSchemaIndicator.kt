package com.example.skks.dgql.core

import com.netflix.graphql.dgs.internal.DefaultDgsQueryExecutor

class DefaultReloadSchemaIndicator(
    private val schemaStatusProvider: SchemaStatusProvider
) : DefaultDgsQueryExecutor.ReloadSchemaIndicator {

    override fun reloadSchema(): Boolean = schemaStatusProvider.hasChanges()
}
