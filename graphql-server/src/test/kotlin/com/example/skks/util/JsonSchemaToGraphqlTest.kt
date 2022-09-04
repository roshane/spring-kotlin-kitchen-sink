package com.example.skks.util

import org.junit.jupiter.api.Test

internal class JsonSchemaToGraphqlTest {

    private val cut = JsonSchemaToGraphql()

    @Test
    fun `placeholder test`() {
        cut.parseJsonSchema("user.json")
    }
}
