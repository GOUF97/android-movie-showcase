package com.mandiri.movieapp.presentation.discover

import androidx.paging.PagingData
import app.cash.turbine.test
import com.mandiri.movieapp.core.network.NetworkResult
import com.mandiri.movieapp.domain.model.Genre
import com.mandiri.movieapp.domain.model.Movie
import com.mandiri.movieapp.domain.usecase.GetDiscoverMoviesPagedUseCase
import com.mandiri.movieapp.domain.usecase.GetMovieGenresUseCase
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
class DiscoverViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMovieGenresUseCase: GetMovieGenresUseCase = mockk()
    private val getDiscoverMoviesPagedUseCase: GetDiscoverMoviesPagedUseCase = mockk()

    private val sampleGenres = listOf(
        Genre(id = 28, name = "Action"),
        Genre(id = 12, name = "Adventure")
    )

    @Before
    fun setup() {
        every { getDiscoverMoviesPagedUseCase(any()) } returns flowOf(PagingData.empty())
    }

    @Test
    fun `init should load genres successfully (Positive Case)`() = runTest {
        coEvery { getMovieGenresUseCase() } returns NetworkResult.Success(sampleGenres)

        val viewModel = DiscoverViewModel(getMovieGenresUseCase, getDiscoverMoviesPagedUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(sampleGenres, state.genres)
            assertFalse(state.isLoadingGenres)
            assertNull(state.genreErrorMessage)
        }

        coVerify(exactly = 1) { getMovieGenresUseCase() }
    }

    @Test
    fun `init should update state with error when fetching genres fails (Negative Case)`() = runTest {
        val errorMessage = "Network timeout"
        coEvery { getMovieGenresUseCase() } returns NetworkResult.Error(errorMessage)

        val viewModel = DiscoverViewModel(getMovieGenresUseCase, getDiscoverMoviesPagedUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(emptyList<Genre>(), state.genres)
            assertFalse(state.isLoadingGenres)
            assertEquals(errorMessage, state.genreErrorMessage)
        }

        coVerify(exactly = 1) { getMovieGenresUseCase() }
    }

    @Test
    fun `onIntent SelectGenre should update selectedGenreId`() = runTest {
        coEvery { getMovieGenresUseCase() } returns NetworkResult.Success(sampleGenres)

        val viewModel = DiscoverViewModel(getMovieGenresUseCase, getDiscoverMoviesPagedUseCase)

        viewModel.uiState.test {
            assertEquals(null, awaitItem().selectedGenreId)

            viewModel.onIntent(DiscoverUiIntent.SelectGenre(28))
            assertEquals(28, awaitItem().selectedGenreId)

            viewModel.onIntent(DiscoverUiIntent.SelectGenre(null))
            assertEquals(null, awaitItem().selectedGenreId)
        }
    }

    @Test
    fun `onIntent RetryGenres should re-invoke getMovieGenresUseCase`() = runTest {
        coEvery { getMovieGenresUseCase() } returns NetworkResult.Error("Temporary error") andThen NetworkResult.Success(sampleGenres)

        val viewModel = DiscoverViewModel(getMovieGenresUseCase, getDiscoverMoviesPagedUseCase)

        viewModel.uiState.test {
            val initialErrorState = awaitItem()
            assertEquals("Temporary error", initialErrorState.genreErrorMessage)

            viewModel.onIntent(DiscoverUiIntent.RetryGenres)
            val successState = awaitItem()
            assertEquals(sampleGenres, successState.genres)
            assertNull(successState.genreErrorMessage)
        }

        coVerify(exactly = 2) { getMovieGenresUseCase() }
    }
}
