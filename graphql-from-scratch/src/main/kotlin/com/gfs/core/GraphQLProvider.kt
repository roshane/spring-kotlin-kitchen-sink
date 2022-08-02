package com.gfs.core

import com.gfs.datafetcher.DelegatedDataFetcher
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.language.FieldDefinition
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import org.slf4j.LoggerFactory
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.PostConstruct

class GraphQLProvider(
    private val schemaLocations: List<String>,
    private val delegatedDataFetcher: DelegatedDataFetcher<*>
) : RefreshableGraphQLProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(GraphQLProvider::class.java)
        private val resolver = PathMatchingResourcePatternResolver(Thread.currentThread().contextClassLoader)
        private val schemaParser = SchemaParser()
        private val schemaGenerator = SchemaGenerator()
        private val graphQLRef: AtomicReference<Optional<GraphQL>> = AtomicReference(Optional.empty())
    }

    override fun execute(input: ExecutionInput): ExecutionResult = graphQLRef.get()
        .map { it.execute(input.query) }
        .orElseThrow { RuntimeException("GraphQL Not Initialized") }

    @PostConstruct
    fun postConstruct() = refresh()

    override fun refresh() {
        logger.info("configured graphqlQL engine...")
        graphQLRef.set(
            Optional.of(
                GraphQL.newGraphQL(graphqlSchema()).build()
            )
        )
    }

    private fun graphqlSchema(): GraphQLSchema {
        val schema = loadSchema()
        val queryFields: List<String> = schema.getType("Query").map {
            it.children.map { item ->
                when (item) {
                    is FieldDefinition -> Optional.of(item.name)
                    else -> Optional.empty()
                }
            }.filter { i -> i.isPresent }
                .map { i -> i.get() }
        }.orElse(emptyList())
        val wiring = runtimeWiring(queryFields)
        return schemaGenerator.makeExecutableSchema(schema, wiring)
    }

    private fun runtimeWiring(queryFields: List<String>): RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query") { builder -> queryFields.map { qf -> builder.dataFetcher(qf, delegatedDataFetcher) }.first() }
        .build()

    private fun loadSchema(): TypeDefinitionRegistry = schemaParser.parse(
        schemaLocations.map {
            resolver.getResource(it)
        }.joinToString {
            it.file.readLines(Charsets.UTF_8).joinToString(separator = System.lineSeparator())
        }
    )

}