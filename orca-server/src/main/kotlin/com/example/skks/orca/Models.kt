package com.example.skks.orca

import java.util.*

data class EndpointDefinition(
    val url: String,
    val method: String
)

data class Context(
    val storeResultInContext: Boolean = false,
    val name: String
)

data class StepDefinition(
    val id: UUID = UUID.randomUUID(),
    val description: String,
    val isScript: Boolean = false,
    val scriptName: String? = null,
    val memberFunction: String? = null,
    val endpoint: EndpointDefinition? = null,
    val context: Context = Context(name = "result")
)
