package com.example.skks.orca

import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

object Util {

    fun readClassPathResourceAsString(path: String): String =
        Optional.ofNullable(Util::class.java.getResourceAsStream(path))
            .map { String(it.readAllBytes()) }
            .orElseThrow { RuntimeException("Resource not found $path") }

    private val logger = LoggerFactory.getLogger("instrumentation")

    @OptIn(ExperimentalTime::class)
    fun <R> logExecutionTime(f: () -> R, tag: String): R {
        return measureTimedValue(f).let {
            logger.info("{} took {}ms", tag, it.duration.inWholeMilliseconds)
            it.value
        }
    }
}