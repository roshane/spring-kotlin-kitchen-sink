package com.example.skks.orca.core

import com.example.skks.orca.StepDefinition
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.MongoClientSettings.getDefaultCodecRegistry
import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import java.util.*
import java.util.stream.Collectors
import java.util.stream.StreamSupport

class IntegrationFlowRepository(
    mongoClient: MongoClient,
    private val mapper: ObjectMapper
) {

    private val store = mutableMapOf<UUID, List<StepDefinition>>()
    private val db = mongoClient.getDatabase("orca").apply {
        fromRegistries(
            getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )
    }
    private val collection = db.getCollection("flow")

    fun createNew(flow: List<StepDefinition>): UUID = UUID.randomUUID().apply {
        val entity = mapOf("_id" to toString(), "definition" to flow)
        collection.insertOne(Document.parse(mapper.writeValueAsString(entity)))
    }

    fun all(): List<List<StepDefinition>> = StreamSupport
        .stream(collection.find().spliterator(), false)
        .map { document -> document.toMap() }
        .map {
            println("found items: $it")
            it["definition"] as List<StepDefinition>
        }.collect(Collectors.toList())

    fun find(id: UUID): List<StepDefinition> = Optional.ofNullable(
        collection
            .find(Filters.eq("_id", id.toString()))
            .first()
    )
        .map { it.toMap()["definition"] as List<StepDefinition> }
        .orElseThrow { RuntimeException("Flow with id: $id not found`") }
}