package com.fimly.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class KtorNetworkDataSource(private val httpClient: HttpClient): NetworkDataSource {
}