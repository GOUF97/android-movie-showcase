package com.mandiri.movieapp.domain.usecase

import androidx.paging.PagingData
import com.mandiri.movieapp.domain.model.Review
import com.mandiri.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieReviewsPagedUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<PagingData<Review>> {
        return movieRepository.getMovieReviewsPaged(movieId)
    }
}
