package com.fimly.app.di

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
import io.ktor.http.ContentType
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
                    // TODO: Configure base API host
                    host = ""
                }
                contentType(ContentType.Application.Json)
                // TODO: Inject API Key / Authorization header securely via BuildConfig
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
                level = LogLevel.ALL
            }
        }
    }

    single<NetworkDataSource> { KtorNetworkDataSource(get()) }
}