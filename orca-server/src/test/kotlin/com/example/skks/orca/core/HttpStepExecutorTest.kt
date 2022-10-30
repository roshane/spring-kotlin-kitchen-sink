package com.example.skks.orca.core

import com.example.skks.orca.EndpointDefinition
import com.example.skks.orca.StepDefinition
import com.example.skks.orca.queryString
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
internal class HttpStepExecutorTest(wmRuntimeInfo: WireMockRuntimeInfo) {

    private val baseUrl = wmRuntimeInfo.httpBaseUrl
    private val mapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()

    private fun createClassUnderTest() = HttpStepExecutor(
        mapper,
        RestTemplate()
    )

    @Test
    fun `01 - execute should GET the response from the given endpoint`() {
        val step = StepDefinition(
            description = "test",
            isScript = false,
            endpoint = EndpointDefinition(
                url = "$baseUrl/api/numbers",
                method = "GET"
            )
        )
        val fakeResponse = mapper.writeValueAsString(listOf(1, 2, 3, 4, 5))
        stubFor(
            get(urlEqualTo("/api/numbers"))
                .willReturn(okJson(fakeResponse))
        )
        val (actual, _) = createClassUnderTest().execute(step)
        assertEquals(fakeResponse, actual)
        verify(
            1,
            getRequestedFor(urlEqualTo("/api/numbers"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        )
    }

    @Test
    fun `02 - execute should GET the response from the given endpoint with provided queryString`() {
        val step = StepDefinition(
            description = "test",
            isScript = false,
            endpoint = EndpointDefinition(
                url = "$baseUrl/api/hello",
                method = "GET"
            )
        )
        val fakeResponse = "Hello User"
        stubFor(
            get(urlPathEqualTo("/api/hello"))
                .willReturn(okJson(fakeResponse))
        )
        val inputArgs = mapOf(queryString to "name=roshane&age=12")
        val (actual, _) = createClassUnderTest().execute(step, inputArgs)
        assertEquals(fakeResponse, actual)
        verify(
            1,
            getRequestedFor(urlPathEqualTo("/api/hello"))
                .withQueryParam("name", equalTo("roshane"))
                .withQueryParam("age", equalTo("12"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
        )
    }

}