//package com.gfs.datafetcher
//
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.springframework.core.io.ClassPathResource
//import kotlin.reflect.KClass
//
//internal class DataFetcherFactoryTest {
//
//    private val dataFetchersFile = ClassPathResource("test-datafetcher.json")
//    private val objectMapper = jacksonObjectMapper()
//
//    private fun createClassUnderTest() = DataFetcherFactory(objectMapper)
//
//    @Test
//    fun `should successfully parse the valid json config file`() {
//        val json = String(dataFetchersFile.file.readBytes())
//        val actual = createClassUnderTest().parse(json)
//        assertEquals(1, actual.dataFetchers.size)
//
//        val df1 = actual.dataFetchers[0]
//        assertEquals("echo-data-fetcher", df1.id)
//        assertEquals("Query", df1.type)
//        assertEquals("echo", df1.fieldName)
//        assertEquals(1, df1.arguments.size)
//
//        val arg1 = df1.arguments[0]
//        assertEquals("name", arg1.name)
//        assertEquals("string", arg1.kType)
//        assertEquals("false", arg1.required)
//    }
//
//    @Test
//    fun `type derive test`() {
//        val kType = "kotlin.String"
//        val a: Any = "hello"
//        val r: String = cast(a)
//    }
//
//    private fun <T> cast(x: Any) = x as T
//
//    private fun getType(t: String): KClass<*> = when (t) {
//        "kotlin.String" -> String::class
//        "kotlin.Int" -> Int::class
//        else -> throw RuntimeException("Unknown kotlin Type $t")
//
//    }
//}