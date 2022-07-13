package com.example.skks.jtog.util

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.Scalars

private val userJson = """
    {
   "id":1,
   "name":"Leanne Graham",
   "username":"Bret",
   "married": false,
   "email":"Sincere@april.biz",
   "address":{
      "street":"Kulas Light",
      "suite":"Apt. 556",
      "city":"Gwenborough",
      "zipcode":"92998-3874",
      "geo":{
         "lat":"-37.3159",
         "lng":"81.1496"
      }
   },
   "numbers":[1,2,3,4,5,6],
   "phone":"1-770-736-8031 x56442",
   "website":"hildegard.org",
   "company":{
      "name":"Romaguera-Crona",
      "catchPhrase":"Multi-layered client-server neural-net",
      "bs":"harness real-time e-markets"
   }
}
""".trimIndent()

object JsonToGql {

    fun transformPrimitive(input: Any) = when (input) {
        is Int, is Short -> Scalars.GraphQLInt
        is Long, is Float -> Scalars.GraphQLFloat
        is String -> Scalars.GraphQLString
        is Boolean -> Scalars.GraphQLBoolean
        else -> throw java.lang.RuntimeException("Unknown primitive type $input")
    }
}

fun main(args: Array<String>) {
    val objectMapper = ObjectMapper()
    val parsed = objectMapper.readValue(userJson, Any::class.java)

    println(parsed::class.java)

//    parsed.keys.forEach {
//        val value = parsed[it]
//        if (value != null)
//            println("key: $it: valueType: ${JsonToGql.transformPrimitive(value)} => $value")
//    }
}