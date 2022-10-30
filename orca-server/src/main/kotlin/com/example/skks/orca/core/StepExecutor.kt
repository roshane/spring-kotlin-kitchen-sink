package com.example.skks.orca.core

import com.example.skks.orca.StepDefinition
import com.example.skks.orca.TGenericObject

interface StepExecutor {

    fun execute(
        stepDefinition: StepDefinition,
        inputArgs: Any? = null,
        context: TGenericObject = emptyMap()
    ): Pair<String, TGenericObject>
}