package com.mandiri.movieapp.domain.usecase

import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.core.network.map
import com.mandiri.movieapp.domain.model.VideoTrailer
import com.mandiri.movieapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieTrailersUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): NetworkResult<List<VideoTrailer>> {
        return movieRepository.getMovieTrailers(movieId).map { list ->
            // Prioritize official YouTube trailers first, followed by teasers or other clips
            list.filter { it.isYoutube }
                .sortedWith(
                    compareByDescending<VideoTrailer> { it.isOfficial }
                        .thenByDescending { it.isTrailer }
                )
        }
    }
}
