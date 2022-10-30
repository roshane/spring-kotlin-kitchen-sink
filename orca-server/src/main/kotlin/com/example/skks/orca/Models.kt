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

data class Script(
    val memberFunction: String? = null,
    val src: String? = null,
    val name: String = "/js/index.js"
)

data class StepDefinition(
    val id: UUID = UUID.randomUUID(),
    val description: String,
    val isScript: Boolean = false,
    val script: Script = Script(),
    val endpoint: EndpointDefinition? = null,
    val context: Context = Context(name = "result")
)
