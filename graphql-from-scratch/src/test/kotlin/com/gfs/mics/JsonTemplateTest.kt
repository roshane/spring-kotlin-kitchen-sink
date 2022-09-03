package com.gfs.mics

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gfs.core.RemoteResourceLoader
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.io.DefaultResourceLoader

class JsonTemplateTest {

    companion object {
        private val objectMapper = jacksonObjectMapper()
        private val configBuilder = SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
        private val config = configBuilder.build()
        private val generator = SchemaGenerator(config)
    }

    @Test
    fun `should generate json schema`() {
        data class Customer(val id: Int, val name: String, val email: String)

        val schema = generator.generateSchema(Customer::class.java)
        println(schema.toPrettyString())
        Assertions.assertNotNull(schema.toPrettyString())
    }

    @Test
    fun `should fill the json template as map`() {

        val node = ArrayNode(JsonNodeFactory.instance)

    }

    private val location =
        "https://raw.githubusercontent.com/roshane/spring-kotlin-kitchen-sink/main/dynamic-gql/src/main/resources/application.properties"

    @Test
    fun `spring load http resource test`() {
        val rLoader = DefaultResourceLoader()
        val resource = rLoader.getResource(location)
        val actual = resource.file.readLines()
        val expected = """
            server.port=9000
        """.trimIndent()
        assertTrue(actual.contains(expected))
    }

    @Test
    fun remoteResourceLoaderShouldLoadTheFileContentAsString() {
        val loader = RemoteResourceLoader()
        val content = loader.getResource(location)
        assertNotNull(content)
        assertTrue(content.contains("server.port=9000"))
    }
}