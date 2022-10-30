package com.example.skks.orca.core

import com.example.skks.orca.StepDefinition
import com.example.skks.orca.TGenericObject
import com.example.skks.orca.Util.readClassPathResourceAsString
import com.fasterxml.jackson.databind.ObjectMapper
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.slf4j.LoggerFactory
import java.util.*

class JsStepExecutor(private val mapper: ObjectMapper) : StepExecutor {

    private val logger = LoggerFactory.getLogger(JsStepExecutor::class.java)

    override fun execute(
        stepDefinition: StepDefinition,
        inputArgs: Any?,
        context: TGenericObject
    ): Pair<String, TGenericObject> {
        logger.info(
            "Executing js\nstep: {}\ncontext: {}\ninputArgs: {}",
            stepDefinition,
            context,
            inputArgs
        )
        return Optional.ofNullable(inputArgs).map {
            if (it is String) {
                it
            } else {
                mapper.writeValueAsString(it)
            }
        }
            .orElse(mapper.writeValueAsString(emptyMap<String, String>()))
            .let { args ->
                val result = executeInternal(stepDefinition, args, context)
                Pair(result, context)//TODO pass proper context
            }
    }

    private fun executeInternal(
        stepDefinition: StepDefinition,
        inputArgs: Any,
        context: TGenericObject
    ): String = Context.create("js").let { ctx ->
        ctx.eval(Source.create("js", readClassPathResourceAsString(stepDefinition.scriptName!!)))
        ctx.getBindings("js")
            .getMember(stepDefinition.memberFunction)
            .execute(
                formatInputArgs(inputArgs),
                mapper.writeValueAsString(context)
            ).asString().let {
                ctx.close()
                it
            }
    }

    private fun formatInputArgs(inputArgs: Any): String = if (inputArgs is String) {
        inputArgs
    } else {
        mapper.writeValueAsString(inputArgs)
    }
}