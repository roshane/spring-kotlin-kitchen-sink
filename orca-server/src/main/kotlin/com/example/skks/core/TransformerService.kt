package com.example.skks.core

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.tools.generic.JsonTool
import java.io.StringWriter

class TransformerService(
    private val ve: VelocityEngine,
    private val jsonTool: JsonTool
) {

    private val templateBasePath = "/templates"

    fun transform(jsonData: String, templateName: String): String {
        val templatePath = "$templateBasePath/$templateName"
        return VelocityContext().let {
            it.put("data", jsonData)
            it.put("json", jsonTool)
            it.put("mics", MicsTool())
            StringWriter().let { w ->
                ve.mergeTemplate(templatePath, Charsets.UTF_8.name(), it, w)
                w.close()
                w.toString()
            }
        }
    }
}