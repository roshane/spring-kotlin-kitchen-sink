package com.gfs.handler

import com.gfs.core.RefreshableGraphQLProvider
import com.gfs.http.Http.text
import graphql.schema.idl.SchemaPrinter
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.ServerResponse.ok

class GraphQLSchemaHandler(
    private val schemaProvider: RefreshableGraphQLProvider
) {
    companion object {
        private val printer = SchemaPrinter()
    }

    fun schema(req: ServerRequest): ServerResponse =
        ok().text().body(printer.print(schemaProvider.schema()))
}