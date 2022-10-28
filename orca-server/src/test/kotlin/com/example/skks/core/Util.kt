package com.example.skks.core

object Util {

    fun readFileAsString(path: String): String =
        String(Util::class.java.getResourceAsStream(path)!!.readAllBytes())
}