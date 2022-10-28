package com.example.skks.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.velocity.tools.config.DefaultKey
import org.slf4j.LoggerFactory

@DefaultKey("mics")
class MicsTool {
    private val logger = LoggerFactory.getLogger(MicsTool::class.java)

    private val objectMapper = ObjectMapper()

    fun groupBy(jsonListOfObjects: String, key: String): String {
        val items: TListGenericObject = objectMapper
            .readValue(jsonListOfObjects, List::class.java) as TListGenericObject
        logger.info("groupBy: {}", key)
        return objectMapper.writeValueAsString(items.groupBy { it[key]!! })
    }

    fun stringify(obj: Any): String =
        objectMapper.writeValueAsString(obj)

}