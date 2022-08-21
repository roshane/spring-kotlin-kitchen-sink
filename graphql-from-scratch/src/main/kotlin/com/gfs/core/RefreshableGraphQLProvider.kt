package com.gfs.core

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.schema.GraphQLSchema

interface RefreshableGraphQLProvider {

    fun refresh()

    fun execute(input: ExecutionInput): ExecutionResult

    fun schema():GraphQLSchema
}