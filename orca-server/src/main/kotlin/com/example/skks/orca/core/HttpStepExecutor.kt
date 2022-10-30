package com.example.skks.orca.core

import com.example.skks.orca.StepDefinition
import com.example.skks.orca.TGenericObject
import com.example.skks.orca.queryString
import com.example.skks.orca.requestBody
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI
import java.util.*

class HttpStepExecutor(
    private val mapper: ObjectMapper,
    private val restTemplate: RestTemplate
) : StepExecutor {

    private val logger = LoggerFactory.getLogger(HttpStepExecutor::class.java)

    override fun execute(
        stepDefinition: StepDefinition,
        inputArgs: Any?,
        context: TGenericObject
    ): Pair<String, TGenericObject> {
        logger.info(
            "Executing http step\ndefinition: {}\ncontext: {}\ninputArgs: {}",
            stepDefinition,
            context,
            inputArgs
        )
        return restTemplate.exchange<String>(buildRequest(stepDefinition, inputArgs))
            .let { resp ->
                if (resp.statusCode.is2xxSuccessful) {
                    Pair(resp.body!!, context)
                } else {
                    throw RuntimeException("HttpClient exception ${resp.statusCode} ${resp.body}")
                }
            }
    }

    private fun buildRequest(
        stepDefinition: StepDefinition,
        inputArgs: Any?
    ): RequestEntity<*> {

        return if (Objects.isNull(inputArgs)) {
            return RequestEntity.method(
                HttpMethod.valueOf(stepDefinition.endpoint!!.method.uppercase()),
                URI.create(stepDefinition.endpoint.url)
            ).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .build()
        } else {
            when (inputArgs!!) {
                is String -> {
                    return RequestEntity.method(
                        HttpMethod.valueOf(stepDefinition.endpoint!!.method.uppercase()),
                        URI.create(stepDefinition.endpoint.url)
                    ).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(inputArgs)
                }

                is Map<*, *> -> when ((inputArgs as Map<*, *>).isEmpty()) {
                    true -> {
                        RequestEntity.method(
                            HttpMethod.valueOf(stepDefinition.endpoint!!.method.uppercase()),
                            URI.create(stepDefinition.endpoint.url)
                        ).accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .build()
                    }

                    false -> {
                        val queryString = inputArgs[queryString] as String
                        val requestBody = inputArgs[requestBody]
                        RequestEntity.method(
                            HttpMethod.valueOf(stepDefinition.endpoint!!.method.uppercase()),
                            URI.create(
                                Optional.ofNullable(queryString)
                                    .map { qs -> "${stepDefinition.endpoint.url}?$qs" }
                                    .orElse(stepDefinition.endpoint.url)
                            )
                        ).accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Optional.ofNullable(requestBody).orElse(emptyMap<String, Any>()))
                    }
                }

                else -> throw RuntimeException("Unsupported inputArgs $inputArgs")
            }
        }
    }
}