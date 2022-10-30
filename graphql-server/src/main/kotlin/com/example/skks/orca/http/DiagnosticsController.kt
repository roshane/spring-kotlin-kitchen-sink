package com.example.skks.orca.http

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.graphql.execution.GraphQlSource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
@Controller
@RequestMapping("/diagnostics")
class DiagnosticsController(
    private val appContext: ApplicationContext,
    private val source: GraphQlSource
) {

    private val logger = LoggerFactory.getLogger(DiagnosticsController::class.java)

    open class XRunnable {
        private val timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())

        companion object {
            var instanceNumber = 0
        }

        init {
            instanceNumber += 1
            println("instance: $instanceNumber, time: $timestamp")
        }
    }

//    @PostMapping("/datafetcher")
//    @ResponseBody
//    fun refreshAppContext(@Autowired @Qualifier("anonymousBean") bean: XRunnable): String {
//        val dfString = doSomething()
//        return "Ok"
//    }

//    fun doSomething():String {
//        val fieldCoordinate = FieldCoordinates.coordinates("Query", "posts")
//        val fieldDefinition = GraphQLFieldDefinition.Builder()
//            .name("posts")
//            .type(GraphQLObjectType.newObject().field())
//            .build()
//        val df = source.schema().codeRegistry.getDataFetcher(fieldCoordinate, fieldDefinition)
//        return df.toString()
//    }

    @Bean("anonymousBean")
    @Scope("prototype")
    fun anonymousObj(): XRunnable = XRunnable()

}
