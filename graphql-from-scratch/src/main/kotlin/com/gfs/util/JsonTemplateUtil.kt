package com.gfs.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.jknack.handlebars.Handlebars
import org.slf4j.LoggerFactory

class JsonTemplateUtil(
    private val objectMapper: ObjectMapper,
    private val hb: Handlebars
) {

    companion object {
        private val logger = LoggerFactory.getLogger(JsonTemplateUtil::class.java)
    }

    fun <T> extractValue(
        source: String,
        t: Class<*>,
        extractTemplate: String
    ): T {
        val template = hb.compileInline(extractTemplate)
        return when (t) {
            List::class.java -> {
                val listOfObjects = objectMapper.readValue<List<Any>>(source)
                listOfObjects.map {
                    objectMapper.readValue<Map<String, Any>>(template.apply(it))
                } as T
            }

            Map::class.java -> {
                objectMapper.readValue<Map<String, Any>>(template.apply(source)) as T
            }

            else -> TODO("invalid parameter")
        }
    }
}