package com.example.skks.orca

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MicsTest {

    @Test
    fun `test time measure`() {
        val a = Util.logExecutionTime({ Pair("name", "age") }, "test-01")
    }

    private val mapper = JsonMapper.builder().addModule(KotlinModule.Builder().build()).build()

    private val javascriptSourceCode = """
        const serialize = (input) => JSON.stringify(input);

        const deserialize = (json) => JSON.parse(json);
        
        const functions = {
          hello: () => {
            return {message:"hello world"};
          },
        };
        
         const __main__ = (_input, _context, callback) => {
          try {
            console.log('_input',_input,'_context',_context);
            const input = deserialize(_input);
            const context = deserialize(_context);
            const func = functions[callback];
            return serialize(func(input, context));
          } catch (error) {
            console.log(error);
            throw error;
          }
        };
    """.trimIndent()

    private fun executeJs(memberFunc: String): String {
        return Context.create("js").let { ctx ->
            ctx.eval(Source.create("js", javascriptSourceCode))
            ctx.getBindings("js")
                .getMember("__main__")
                .execute(
                    mapper.writeValueAsString(listOf(1, 2, 3, 4, 5)),
                    mapper.writeValueAsString(mapOf("context" to "context variables")),
                    memberFunc
                ).asString().let {
                    ctx.close()
                    it
                }
        }
    }

    @Test
    fun `01 - executeJs`() {
        val result = executeJs("hello")
        println("results: $result")
        assertEquals(
            mapper.writeValueAsString(mapOf("message" to "hello world")),
            result
        )
    }
}