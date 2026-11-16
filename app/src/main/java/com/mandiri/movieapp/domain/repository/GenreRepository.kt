package com.mandiri.movieapp.domain.repository

import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.Genre

interface GenreRepository {
    suspend fun getMovieGenres(): NetworkResult<List<Genre>>
}
