package com.example.skks.orca.core

import com.example.skks.orca.EndpointDefinition
import com.example.skks.orca.StepDefinition
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@WireMockTest
internal class FlowExecutorIntegrationTest(wmRuntimeInfo: WireMockRuntimeInfo) {

    private val objectMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()
    private val baseUrl = wmRuntimeInfo.httpBaseUrl
    private val jsStepExecutor = JsStepExecutor(objectMapper)
    private val httpStepExecutor = HttpStepExecutor(objectMapper, RestTemplate())

    private fun createClassUnderTest() = FlowExecutor(
        jsStepExecutor,
        httpStepExecutor,
        objectMapper
    )

    @Test
    fun `01 - execute should complete single js step definition successfully`() {
        val step = StepDefinition(
            description = "integration-test",
            isScript = true,
            scriptName = "/js/index-test.js",
            memberFunction = "sayHello"
        )
        val result = createClassUnderTest().execute(listOf(step))
        val expected = objectMapper.writeValueAsString(mapOf("message" to "hello"))
        assertEquals(expected, result)
    }

    @Test
    fun `02 - execute should complete single http step definition successfully`() {
        val step = StepDefinition(
            description = "test",
            isScript = false,
            endpoint = EndpointDefinition(
                url = "$baseUrl/api/numbers",
                method = "GET"
            )
        )
        val fakeResponse = objectMapper.writeValueAsString(listOf(1, 2, 3, 4, 5))
        stubFor(
            get(urlEqualTo("/api/numbers"))
                .willReturn(okJson(fakeResponse))
        )
        val actual = createClassUnderTest().execute(listOf(step))
        assertEquals(fakeResponse, actual)
        verify(
            1,
            getRequestedFor(urlEqualTo("/api/numbers"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        )
    }

    @Test
    fun `03 - execute should complete two step flow successfully`() {
        val step01 = StepDefinition(
            description = "test",
            isScript = false,
            endpoint = EndpointDefinition(
                url = "$baseUrl/api/users",
                method = "GET"
            )
        )
        val step02 = StepDefinition(
            description = "integration-test",
            isScript = true,
            scriptName = "/js/index-test.js",
            memberFunction = "groupUsersByAge"
        )

        val fakeUsers = listOf(
            mapOf("age" to 10, "name" to "user-01"),
            mapOf("age" to 10, "name" to "user-010"),
            mapOf("age" to 21, "name" to "user-21"),
            mapOf("age" to 23, "name" to "user-23"),
        )
        val fakeResponse = objectMapper.writeValueAsString(fakeUsers)
        stubFor(
            get(urlEqualTo("/api/users"))
                .willReturn(okJson(fakeResponse))
        )
        val actual = createClassUnderTest().execute(listOf(step01, step02))
        val expected = fakeUsers.groupBy { it["age"] }
        assertEquals(objectMapper.writeValueAsString(expected), actual)

        verify(
            1,
            getRequestedFor(urlEqualTo("/api/users"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        )
    }
}