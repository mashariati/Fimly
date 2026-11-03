package com.fimly.app.data.network

import com.fimly.app.domain.util.AppResult
import com.fimly.app.domain.util.DataError
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Safely executes a Ktor network request and maps exceptions or HTTP status codes to [DataError].
 */
suspend inline fun <reified T> safeApiCall(
    execute: () -> HttpResponse
): AppResult<T, DataError> {
    val response = try {
        execute()
    } catch (e: CancellationException) {
        // Always rethrow CancellationException to preserve structured concurrency
        throw e
    } catch (e: UnknownHostException) {
        return AppResult.Error(DataError.NO_INTERNET, "No internet connection")
    } catch (e: UnresolvedAddressException) {
        return AppResult.Error(DataError.NO_INTERNET, "No internet connection")
    } catch (e: ConnectException) {
        return AppResult.Error(DataError.NO_INTERNET, "Failed to connect to server")
    } catch (e: SocketTimeoutException) {
        return AppResult.Error(DataError.REQUEST_TIMEOUT, "Request timed out")
    } catch (e: HttpRequestTimeoutException) {
        return AppResult.Error(DataError.REQUEST_TIMEOUT, "Request timed out")
    } catch (e: SerializationException) {
        return AppResult.Error(DataError.SERIALIZATION_ERROR, "Unexpected response format from server")
    } catch (e: Exception) {
        return AppResult.Error(DataError.UNKNOWN, e.localizedMessage ?: "An unknown error occurred")
    }

    return when (response.status.value) {
        in 200..299 -> {
            try {
                AppResult.Success(response.body<T>())
            } catch (e: SerializationException) {
                AppResult.Error(DataError.SERIALIZATION_ERROR, "Failed to parse server response")
            } catch (e: Exception) {
                AppResult.Error(DataError.UNKNOWN, e.localizedMessage)
            }
        }
        401 -> AppResult.Error(
            error = DataError.UNAUTHORIZED,
            message = "Unauthorized: Please check your TMDB_BEARER_TOKEN in local.properties"
        )
        408 -> AppResult.Error(DataError.REQUEST_TIMEOUT, "Server request timed out")
        in 500..599 -> AppResult.Error(DataError.SERVER_ERROR, "Server error (${response.status.value})")
        else -> AppResult.Error(DataError.UNKNOWN, "HTTP error (${response.status.value})")
    }
}