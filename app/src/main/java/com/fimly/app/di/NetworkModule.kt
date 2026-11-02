package com.fimly.app.di

import com.fimly.app.BuildConfig
import com.fimly.app.data.network.KtorNetworkDataSource
import com.fimly.app.data.network.NetworkDataSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private const val NETWORK_TIMEOUT = 15_000

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {

    single {
        HttpClient(Android) {
            engine {
                connectTimeout = NETWORK_TIMEOUT
                socketTimeout = NETWORK_TIMEOUT
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BuildConfig.TMDB_BASE_URL
                }
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.TMDB_BEARER_TOKEN}")
                header(HttpHeaders.Accept, "application/json")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    explicitNulls = false
                    coerceInputValues = true
                    isLenient = true
                })
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            }
        }
    }

    single<NetworkDataSource> { KtorNetworkDataSource(get()) }
}