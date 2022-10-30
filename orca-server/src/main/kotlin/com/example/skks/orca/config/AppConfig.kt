package com.example.skks.orca.config

import com.example.skks.orca.core.FlowExecutor
import com.example.skks.orca.core.HttpStepExecutor
import com.example.skks.orca.core.IntegrationFlowRepository
import com.example.skks.orca.core.JsStepExecutor
import com.example.skks.orca.http.ApiRoutes
import com.example.skks.orca.http.HomeHandler
import com.example.skks.orca.http.IntegrationHandler
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.client.MongoClients
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.web.client.RestTemplate
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

class AppConfig : ApplicationContextInitializer<GenericApplicationContext> {

    override fun initialize(applicationContext: GenericApplicationContext) {
        appBeans().initialize(applicationContext)
    }

    private fun appBeans() = beans {

        bean {
            JsonMapper.builder()
                .addModule(KotlinModule.Builder().build())
                .build()
        }
        bean {
            HomeHandler()
        }
        bean {
            ApiRoutes(ref(), ref())
        }
        bean {
            ref<ApiRoutes>().appRoutes()
        }
        bean {
            RestTemplate()
        }
        bean {
            IntegrationHandler(ref(), ref(), ref())
        }
        bean {
            IntegrationFlowRepository(ref(), ref())
        }
        bean {
            FlowExecutor(ref(), ref(), ref())
        }
        bean {
            JsStepExecutor(ref())
        }
        bean {
            HttpStepExecutor(ref(), ref())
        }
        bean {
            MongoClients.create("mongodb://localhost:27017")
        }
        bean {
            val config = CorsConfiguration()
            config.addAllowedOrigin("http://localhost:8080")
            config.addAllowedHeader("*")
            config.addAllowedMethod("*")
            val source = UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration("/api/**", config)
            CorsFilter(source)
        }
    }
}