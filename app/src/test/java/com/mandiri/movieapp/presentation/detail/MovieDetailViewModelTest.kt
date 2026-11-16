package com.mandiri.movieapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import app.cash.turbine.test
import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.Genre
import com.mandiri.movieapp.domain.model.MovieDetail
import com.mandiri.movieapp.domain.model.VideoTrailer
import com.mandiri.movieapp.domain.usecase.GetMovieDetailUseCase
import com.mandiri.movieapp.domain.usecase.GetMovieReviewsPagedUseCase
import com.mandiri.movieapp.domain.usecase.GetMovieTrailersUseCase
import com.mandiri.movieapp.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMovieDetailUseCase: GetMovieDetailUseCase = mockk()
    private val getMovieTrailersUseCase: GetMovieTrailersUseCase = mockk()
    private val getMovieReviewsPagedUseCase: GetMovieReviewsPagedUseCase = mockk()

    private val testMovieId = 550
    private val savedStateHandle = SavedStateHandle(mapOf("movieId" to testMovieId))

    private val sampleDetail = MovieDetail(
        id = testMovieId,
        title = "Fight Club",
        overview = "An insomniac office worker and a devil-may-care soap maker form an underground fight club.",
        posterUrl = "https://image.tmdb.org/t/p/w500/sample.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w780/sample_back.jpg",
        releaseDate = "1999-10-15",
        voteAverage = 8.4,
        voteCount = 26000,
        runtimeMinutes = 139,
        tagline = "Mischief. Mayhem. Soap.",
        status = "Released",
        genres = listOf(Genre(18, "Drama"))
    )

    private val sampleTrailers = listOf(
        VideoTrailer(
            id = "trailer_1",
            youtubeKey = "O19m8V-d1Zg",
            title = "Official Teaser Trailer",
            site = "YouTube",
            type = "Trailer",
            isOfficial = true
        ),
        VideoTrailer(
            id = "trailer_2",
            youtubeKey = "BdJKm16Co6M",
            title = "Theatrical Trailer",
            site = "YouTube",
            type = "Trailer",
            isOfficial = false
        )
    )

    @Before
    fun setup() {
        every { getMovieReviewsPagedUseCase(testMovieId) } returns flowOf(PagingData.empty())
    }

    @Test
    fun `init should fetch movie detail and trailers concurrently (Positive Case)`() = runTest {
        coEvery { getMovieDetailUseCase(testMovieId) } returns NetworkResult.Success(sampleDetail)
        coEvery { getMovieTrailersUseCase(testMovieId) } returns NetworkResult.Success(sampleTrailers)

        val viewModel = MovieDetailViewModel(
            savedStateHandle = savedStateHandle,
            getMovieDetailUseCase = getMovieDetailUseCase,
            getMovieTrailersUseCase = getMovieTrailersUseCase,
            getMovieReviewsPagedUseCase = getMovieReviewsPagedUseCase
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(sampleDetail, state.movieDetail)
            assertEquals(sampleTrailers, state.trailers)
            assertEquals(sampleTrailers.first(), state.selectedTrailer)
            assertNull(state.errorMessage)
        }

        coVerify(exactly = 1) { getMovieDetailUseCase(testMovieId) }
        coVerify(exactly = 1) { getMovieTrailersUseCase(testMovieId) }
    }

    @Test
    fun `init should show error state when getMovieDetail fails (Negative Case)`() = runTest {
        val errorMessage = "Movie not found"
        coEvery { getMovieDetailUseCase(testMovieId) } returns NetworkResult.Error(errorMessage)
        coEvery { getMovieTrailersUseCase(testMovieId) } returns NetworkResult.Success(emptyList())

        val viewModel = MovieDetailViewModel(
            savedStateHandle = savedStateHandle,
            getMovieDetailUseCase = getMovieDetailUseCase,
            getMovieTrailersUseCase = getMovieTrailersUseCase,
            getMovieReviewsPagedUseCase = getMovieReviewsPagedUseCase
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.movieDetail)
            assertEquals(errorMessage, state.errorMessage)
        }
    }

    @Test
    fun `init should handle empty trailers gracefully (Negative Edge Case)`() = runTest {
        coEvery { getMovieDetailUseCase(testMovieId) } returns NetworkResult.Success(sampleDetail)
        coEvery { getMovieTrailersUseCase(testMovieId) } returns NetworkResult.Success(emptyList())

        val viewModel = MovieDetailViewModel(
            savedStateHandle = savedStateHandle,
            getMovieDetailUseCase = getMovieDetailUseCase,
            getMovieTrailersUseCase = getMovieTrailersUseCase,
            getMovieReviewsPagedUseCase = getMovieReviewsPagedUseCase
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(sampleDetail, state.movieDetail)
            assertEquals(emptyList<VideoTrailer>(), state.trailers)
            assertNull(state.selectedTrailer)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `onIntent SelectTrailer should update selectedTrailer in state`() = runTest {
        coEvery { getMovieDetailUseCase(testMovieId) } returns NetworkResult.Success(sampleDetail)
        coEvery { getMovieTrailersUseCase(testMovieId) } returns NetworkResult.Success(sampleTrailers)

        val viewModel = MovieDetailViewModel(
            savedStateHandle = savedStateHandle,
            getMovieDetailUseCase = getMovieDetailUseCase,
            getMovieTrailersUseCase = getMovieTrailersUseCase,
            getMovieReviewsPagedUseCase = getMovieReviewsPagedUseCase
        )

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(sampleTrailers.first(), initialState.selectedTrailer)

            viewModel.onIntent(MovieDetailUiIntent.SelectTrailer(sampleTrailers[1]))
            val updatedState = awaitItem()
            assertEquals(sampleTrailers[1], updatedState.selectedTrailer)
        }
    }
}
