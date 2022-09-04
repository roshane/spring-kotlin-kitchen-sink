package com.example.skks.util

import java.util.*

object Util {

    fun readClassPathResource(path: String): ByteArray {
        return Optional.ofNullable(Util.javaClass.getResourceAsStream(path))
            .map {
                val result = it.readAllBytes()
                it.close()
                result
            }.orElseThrow()
    }
}