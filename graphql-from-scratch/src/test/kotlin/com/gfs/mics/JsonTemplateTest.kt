package com.gfs.mics

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class JsonTemplateTest {

    companion object {
        private val objectMapper = jacksonObjectMapper()
        private val configBuilder =  SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)
        private val config = configBuilder.build()
        private val generator =  SchemaGenerator(config)
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
}