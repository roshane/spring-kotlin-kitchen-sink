package com.gfs.core

import graphql.ExecutionInput
import graphql.ExecutionResult

interface RefreshableGraphQLProvider {

    fun refresh()

    fun execute(input: ExecutionInput): ExecutionResult
}