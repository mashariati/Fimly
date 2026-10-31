package com.fimly.app.data.repository
import com.fimly.app.data.network.NetworkDataSource
import com.fimly.app.domain.repository.Repository
import java.util.Collections.emptyList
import kotlin.Result
import kotlin.runCatching
class RepositoryImpl(private val networkDataSource: NetworkDataSource): Repository {
}