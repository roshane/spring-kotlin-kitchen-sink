package com.example.skks.dgql.core

import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.util.DigestUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PreDestroy

class DefaultSchemaStatusProvider(
    initialDelayMs: Long,
    periodMs: Long,
    private val schemaResourcesProvider: () -> Set<Resource>
) : SchemaStatusProvider {

    private val schemaFileChecksum = ConcurrentHashMap<String, String>()
    private val scheduledExecutor = Executors.newSingleThreadScheduledExecutor()
    private val shouldRefreshSchema = AtomicBoolean(false)

    init {
        logger.info("starting periodic schema check routine...")
        val schemaCheckRoutine = {
            try {
                val currentSchemaFileChecksum = getCurrentSchemaFileChecksum(schemaResourcesProvider)
                if (schemaFileChecksum.isEmpty()) {
                    schemaFileChecksum.putAll(currentSchemaFileChecksum)
                }
                val needRefresh = currentSchemaFileChecksum != schemaFileChecksum

                logger.info(
                    "existing md5Sum: {}, latestMd5Sum: {}, shouldRefresh: {}",
                    schemaFileChecksum,
                    currentSchemaFileChecksum,
                    needRefresh
                )

                if (needRefresh) {
                    shouldRefreshSchema.set(true)
                }
            } catch (ex: Exception) {
                logger.error("routine check error", ex)
            }
        }

        scheduledExecutor.scheduleAtFixedRate(schemaCheckRoutine, initialDelayMs, periodMs, TimeUnit.MILLISECONDS)
    }

    companion object {

        private val logger = LoggerFactory.getLogger(DefaultSchemaStatusProvider::class.java)

        fun getCurrentSchemaFileChecksum(schemaResourcesProvider: () -> Set<Resource>): Map<String, String> {
            val currentSchemaFiles = schemaResourcesProvider.invoke()
                .associateBy { r -> r.filename }

            val result = mutableMapOf<String, String>()

            currentSchemaFiles
                .forEach { entry ->
                    logger.debug("checking schema entry: {}", entry)
                    if (!entry.key.isNullOrEmpty()) {
                        val resource = currentSchemaFiles[entry.key]
                        val md5Sum = Optional.ofNullable(resource)
                            .map { r -> DigestUtils.md5DigestAsHex(r.file.readBytes()) }
                            .orElse("")
                        if (md5Sum.isNotEmpty()) {
                            result[entry.key!!] = md5Sum
                        }
                    }
                }
            return result
        }
    }

    override fun hasChanges() = shouldRefreshSchema.get()

    private fun notifyRefreshed() {
        val currentChecksumMap = getCurrentSchemaFileChecksum(schemaResourcesProvider)
        schemaFileChecksum.putAll(currentChecksumMap)
        shouldRefreshSchema.set(false)
    }

    @PreDestroy
    fun cleanUp() {
        logger.info("shutting down periodic schema check routing...")
        scheduledExecutor.shutdown()
    }
}
