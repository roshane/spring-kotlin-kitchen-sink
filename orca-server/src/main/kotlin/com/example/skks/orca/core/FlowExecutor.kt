package com.example.skks.orca.core

import com.example.skks.orca.StepDefinition
import com.example.skks.orca.TGenericObject
import com.example.skks.orca.Util.logExecutionTime
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class FlowExecutor(
    private val jsStepExecutor: JsStepExecutor,
    private val httpStepExecutor: HttpStepExecutor,
    private val objectMapper: ObjectMapper
) {

    fun execute(
        stepDefinitions: List<StepDefinition>,
        inputArgs: Any? = null
    ): String {
        if (stepDefinitions.isEmpty()) {
            throw RuntimeException("EmptyStepDefinition list provided")
        }
        return executeInternal(stepDefinitions, inputArgs, emptyMap());
    }

    private tailrec fun executeInternal(
        stepDefinitions: List<StepDefinition>,
        inputArgs: Any?,
        context: TGenericObject
    ): String {
        if (stepDefinitions.isEmpty()) {
            return inputArgs as String
        }

        val currentStep = stepDefinitions[0]
        val (result, currentContext) = when (currentStep.isScript) {
            true -> logExecutionTime({ jsStepExecutor.execute(currentStep, inputArgs, context) }, "jsExecutor")
            false -> logExecutionTime({ httpStepExecutor.execute(currentStep, inputArgs, context) }, "httpExecutor")
        }
        val remainingSteps = stepDefinitions.subList(1, stepDefinitions.size)
        if (currentStep.context.storeResultInContext) {
            val newContext = currentContext + mapOf(currentStep.context.name to objectMapper.readValue(result))
            return executeInternal(remainingSteps, result, newContext)
        }
        return executeInternal(remainingSteps, result, currentContext);
    }
}