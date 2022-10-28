package com.example.skks.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.tools.generic.JsonTool
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class TransformerServiceTest {

    private val ve = VelocityEngine().apply {
        addProperty("resource.loaders", "class")
        addProperty(
            "resource.loader.class.class",
            " org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
        )
    }

    private val jsonTool = JsonTool()
    private val fixtureBase = "/fixtures"
    private fun createClassUnderTest() = TransformerService(ve, jsonTool)


    @Test
    fun testTransformShouldExtractTheUserIds() {
        val cut = createClassUnderTest()
        val jsonString = Util.readFileAsString("$fixtureBase/users.json")
        val result = cut.transform(jsonString, "extract-user-id.vm")
        assertFalse(result.isEmpty())
        println(result)
    }

    @Test
    fun testTransformShouldGroupThePostsByUserId() {
        val cut = createClassUnderTest()
        val jsonString = Util.readFileAsString("$fixtureBase/posts.json")
        val result = cut.transform(jsonString, "group-posts-by-user-id.vm")
        assertFalse(result.isEmpty())
        println(result)
    }

    @Test
    fun jsonSerializerTest(){
        val result= ObjectMapper().let{
            val data = listOf(
                mapOf("name" to "Roshane"),
                mapOf("age" to 12)
            )
            it.writeValueAsString(data)
        }
        println(result)
    }
}