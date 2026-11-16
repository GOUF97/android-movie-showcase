package com.mandiri.movieapp.domain.usecase

import androidx.paging.PagingData
import com.mandiri.movieapp.domain.model.Movie
import com.mandiri.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiscoverMoviesPagedUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(genreId: Int?): Flow<PagingData<Movie>> {
        return movieRepository.getDiscoverMoviesPaged(genreId)
    }
}
