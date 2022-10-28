package com.example.skks.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.Scalars.*
import graphql.schema.GraphQLFieldDefinition
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
        val result = mapJsonPropertyToGraphqlTypeDefinition("User", jsonNode)
        result.let {
            val schema = schemaPrinter.let { printer ->
                printer.print(it.type)
            }
            logger.info("Final Entity:\n{}", schema)
        }
    }


    private fun mapJsonPropertiesToGraphqlFieldDefinitions(properties: JsonNode): List<GraphQLFieldDefinition> {
        val jsonPropertyNames = mutableListOf<String>()
        properties.fieldNames().forEach { jsonPropertyNames.add(it) }

        val graphqlTypes = jsonPropertyNames.map { propertyName ->
            val typeNode = properties[propertyName][TYPE]
            val graphqlTypeOptional = when (typeNode.nodeType) {
                JsonNodeType.STRING -> Optional.of(
                    mapJsonPropertyToGraphqlTypeDefinition(
                        propertyName,
                        properties[propertyName]
                    )
                )

                JsonNodeType.ARRAY -> Optional.empty()//TODO()
                JsonNodeType.NULL -> Optional.empty()//TODO()
                else -> throw RuntimeException("Invalid/Unsupported/Todo type on property ${typeNode.toPrettyString()}")
            }
            graphqlTypeOptional
        }
        return graphqlTypes
            .filter { it.isPresent }
            .map { it.get() }
    }

    private fun mapJsonPropertyToGraphqlTypeDefinition(name: String, property: JsonNode): GraphQLFieldDefinition {
        return when (val jsonType = property[TYPE].asText()) {
            "object" -> {
                val result = newFieldDefinition()
                    .name(name)
                    .type(
                        newObject()
                            .name(name.replaceFirstChar { it.uppercase() })
                            .fields(mapJsonPropertiesToGraphqlFieldDefinitions(property[PROPERTIES]))
                            .build()
                    )
                    .build()
                logger.info("Type:\n{}", schemaPrinter.print(result.type))
                result
            }

            else -> newFieldDefinition()
                .name(name)
                .type(mapToGraphqlType(jsonType))
                .build()
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