package com.gfs.template

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.gfs.util.JsonTemplateUtil
import com.github.jknack.handlebars.Handlebars
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class HandlebarTemplateTest {

    companion object {
        private val objectMapper = jacksonObjectMapper()
        private val logger = LoggerFactory.getLogger(HandlebarTemplateTest::class.java)
        private const val templateBasePath = "json_templates"
        private val hb = Handlebars()
    }

    private fun loadResourceAsString(fileName: String): String {
        val path = "$templateBasePath/$fileName"
        return HandlebarTemplateTest::class.java.classLoader.getResourceAsStream(path)?.let {
            val result = String(it.readAllBytes())
            it.close()
            result
        } ?: throw RuntimeException("File $fileName not found")
    }

    private fun createClassUnderTest(): JsonTemplateUtil = JsonTemplateUtil(objectMapper, hb)

    @Test
    fun `should extract user ids given list of json users`() {
        val usersJsonString = loadResourceAsString("users.json")
        val jsonList = objectMapper.readValue<List<Any>>(usersJsonString)
        val hbTemplate = """
            {
                "zipCode": "{{address.zipcode}}"
            }
        """.trimIndent()
        val template = hb.compileInline(hbTemplate)
        val zipCodes = jsonList.map {
            val str = template.apply(it)
            objectMapper.readValue<Map<String, Any>>(str)["zipCode"]
        }
        val expected = listOf(
            "92998-3874",
            "90566-7771",
            "59590-4157",
            "53919-4257",
            "33263",
            "23505-1337",
            "58804-1099",
            "45169",
            "76495-3109",
            "31428-2261"
        )
        assertEquals(expected, zipCodes)
    }

    @Test
    fun `extractValue should extract zipCodes successfully`() {
        val extractTemplate = """
            {
                "zipCode": "{{address.zipcode}}"
            }
        """.trimIndent()
        val expected = listOf(
            "92998-3874",
            "90566-7771",
            "59590-4157",
            "53919-4257",
            "33263",
            "23505-1337",
            "58804-1099",
            "45169",
            "76495-3109",
            "31428-2261"
        ).map { mapOf("zipCode" to it) }
        val usersJsonString = loadResourceAsString("users.json")
        val result = createClassUnderTest().extractValue<List<Map<String, Any>>>(
            usersJsonString,
            List::class.java,
            extractTemplate
        )
        assertEquals(expected, result)
        logger.info("JsonTemplate result: {}", result)
    }

    @Test
    fun `extractValue should extract userId, zipCode successfully`() {
        val extractTemplate = """
            {
                "userId": {{ id }},
                "zipCode": "{{address.zipcode}}"
            }
        """.trimIndent()
        val expected: List<Map<String, Any>> = listOf(
            mapOf("userId" to 1, "zipCode" to "92998-3874"),
            mapOf("userId" to 2, "zipCode" to "90566-7771"),
            mapOf("userId" to 3, "zipCode" to "59590-4157"),
            mapOf("userId" to 4, "zipCode" to "53919-4257"),
            mapOf("userId" to 5, "zipCode" to "33263"),
            mapOf("userId" to 6, "zipCode" to "23505-1337"),
            mapOf("userId" to 7, "zipCode" to "58804-1099"),
            mapOf("userId" to 8, "zipCode" to "45169"),
            mapOf("userId" to 9, "zipCode" to "76495-3109"),
            mapOf("userId" to 10, "zipCode" to "31428-2261")
        )
        val usersJson = loadResourceAsString("users.json")
        val result = createClassUnderTest()
            .extractValue<List<Map<String, Any>>>(usersJson, List::class.java, extractTemplate)
        assertEquals(expected, result)
    }
}