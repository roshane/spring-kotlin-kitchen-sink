package com.example.skks.orca

import org.junit.jupiter.api.Test

class MicsTest {

    @Test
    fun `test time measure`() {
        val a = Util.logExecutionTime({ Pair("name", "age") }, "test-01")
    }
}