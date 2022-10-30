package com.example.skks.orca.http

import com.example.skks.orca.StepDefinition
import com.example.skks.orca.core.FlowExecutor
import com.example.skks.orca.core.IntegrationFlowRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.util.*

class IntegrationHandler(
    private val repository: IntegrationFlowRepository,
    private val flowExecutor: FlowExecutor,
    private val mapper: ObjectMapper
) {

    fun createNew(req: ServerRequest): ServerResponse = req.body(ByteArray::class.java).let {
        mapper.readValue<List<StepDefinition>>(it).let { stepDefinitions ->
            ServerResponse
                .ok()
                .body(repository.createNew(stepDefinitions))
        }
    }

    fun find(req: ServerRequest): ServerResponse = req.pathVariable("id").let {
        ServerResponse
            .ok()
            .body(mapper.writeValueAsString(repository.find(UUID.fromString(it))))
    }

    fun findAll(req: ServerRequest): ServerResponse = ServerResponse.ok()
        .body(mapper.writeValueAsString(repository.all()))

    fun executeFlow(req: ServerRequest): ServerResponse = req.pathVariable("id").let {
        repository.find(UUID.fromString(it)).let { flow ->
            flowExecutor.execute(flow).let { result ->
                ServerResponse
                    .ok()
                    .body(result)
            }
        }
    }
}