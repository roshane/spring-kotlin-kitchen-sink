package com.example.skks.orca.core

import com.example.skks.orca.Script
import com.example.skks.orca.StepDefinition
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class JsStepExecutorTest {
    private val mapper = JsonMapper.builder().addModule(KotlinModule.Builder().build()).build()
    private fun createClassUnderTest() = JsStepExecutor(mapper)

    @Test
    fun `01 - execute should invoke the member function and return the result`() {
        val step = StepDefinition(
            description = "test",
            isScript = true,
            script = Script(
                memberFunction = "sayHello",
                name = "/js/index-test.js"
            )
        )
        val (result, _) = createClassUnderTest().execute(step)
        val expected = mapper.writeValueAsString(mapOf("message" to "hello"))
        assertEquals(expected, result)
    }

    @Test
    fun `02 - execute should invoke the member function with provided inputArgs and context`() {
        val step = StepDefinition(
            description = "test",
            isScript = true,
            script = Script(
                memberFunction = "useInputAndContextArgs",
                name = "/js/index-test.js"
            )
        )
        val context = mapOf("numbers" to listOf(1, 2, 3))
        val inputArgs = listOf("a", "b")
        val (result, _) = createClassUnderTest().execute(step, inputArgs, context)
        val expected = mapper.writeValueAsString(listOf(1, 2, 3, inputArgs))
        assertEquals(expected, result)

        println(mapper.writeValueAsString(step))
    }
}