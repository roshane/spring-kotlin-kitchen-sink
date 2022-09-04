package com.example.skks.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.Scalars.*
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLOutputType
import graphql.schema.idl.SchemaPrinter
import org.slf4j.LoggerFactory
import java.util.*

class JsonSchemaToGraphql {

    companion object {
        private val objectMapper = jacksonObjectMapper()
        private const val schemaBasePath = "/json_schema"
        private const val PROPERTIES = "properties"
        private const val TYPE = "type"
        private val logger = LoggerFactory.getLogger(JsonSchemaToGraphql::class.java)
        private val schemaPrinter = SchemaPrinter()
    }

    data class JsonToGraphqlType(val fieldName: String, val graphQLType: GraphQLOutputType)

    fun parseJsonSchema(fileName: String) {
        val jsonSchema = String(Util.readClassPathResource("$schemaBasePath/$fileName"))
        val jsonNode = objectMapper.readTree(jsonSchema)
        val properties = jsonNode[PROPERTIES]
        logger.info("Properties: {}", properties)
        val result = mapToGraphqlType("Entity", properties)
        result.ifPresent {
            val schema = schemaPrinter.let { printer -> printer.print(it.graphQLType) }
            logger.info("Final Entity:\n{}", schema)
        }
    }

    private fun mapToGraphqlType(name: String, properties: JsonNode): Optional<JsonToGraphqlType> {
        val jsonPropertyNames = mutableListOf<String>()
        properties.fieldNames().forEach { jsonPropertyNames.add(it) }

        val graphqlTypes = jsonPropertyNames.map { propertyName ->
            val typeNode = properties[propertyName][TYPE]
            val graphqlTypeOptional = when (typeNode.nodeType) {
                JsonNodeType.STRING -> mapJsonPropertyTypeToGraphQLType(propertyName, properties[propertyName])
                JsonNodeType.ARRAY -> {
                    logger.warn("Type Array is under implementation")
                    Optional.empty()
                }

                JsonNodeType.NULL -> Optional.empty()
                else -> throw RuntimeException("Invalid type on property ${typeNode.toPrettyString()}")
            }
            graphqlTypeOptional
        }
        val graphqlType = newObject().name(name).let { newObject ->
            graphqlTypes
                .filter { it.isPresent }
                .map { it.get() }
                .forEach {
                    newObject.field(
                        newFieldDefinition()
                            .name(it.fieldName)
                            .type(it.graphQLType)
                    )
                }
            newObject.build()
        }
        logger.info("Schema:\n{}", schemaPrinter.print(graphqlType))
        return Optional.of(JsonToGraphqlType(name, graphqlType))
    }

    private fun mapJsonPropertyTypeToGraphQLType(name: String, property: JsonNode): Optional<JsonToGraphqlType> {
        return when (val jsonType = property[TYPE].asText()) {
            "object" -> {
                val typeName = name.replaceFirstChar { it.uppercase() }
                mapToGraphqlType(typeName, property[PROPERTIES]).map { JsonToGraphqlType(name, it.graphQLType) }
            }

            else -> Optional.of(JsonToGraphqlType(name, mapToGraphqlType(jsonType)))
        }
    }

    private fun mapToGraphqlType(jsonType: String): GraphQLOutputType {
        return when (jsonType) {
            "string" -> GraphQLString
            "number" -> GraphQLFloat
            "integer" -> GraphQLInt
            "object" -> GraphQLID
            "boolean" -> GraphQLBoolean
            "array" -> GraphQLList.list(GraphQLString)
            //TODO no graphql type for this "null" -> TODO()
            else -> throw RuntimeException("Unknown json type $jsonType")
        }
    }

}