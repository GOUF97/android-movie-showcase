package com.mandiri.movieapp.data.repository

import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.core.network.safeApiCall
import com.mandiri.movieapp.core.network.TmdbApiService
import com.mandiri.movieapp.core.util.IoDispatcher
import com.mandiri.movieapp.data.mapper.toDomain
import com.mandiri.movieapp.domain.model.Genre
import com.mandiri.movieapp.domain.repository.GenreRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GenreRepository {

    private val mutex = Mutex()
    private var cachedGenres: List<Genre>? = null

    override suspend fun getMovieGenres(): NetworkResult<List<Genre>> = withContext(ioDispatcher) {
        mutex.withLock {
            cachedGenres?.let {
                return@withContext NetworkResult.Success(it)
            }
        }

        val result = safeApiCall {
            apiService.getMovieGenres().genres.map { it.toDomain() }
        }

        if (result is NetworkResult.Success) {
            mutex.withLock {
                cachedGenres = result.data
            }
        }

        result
    }
}
