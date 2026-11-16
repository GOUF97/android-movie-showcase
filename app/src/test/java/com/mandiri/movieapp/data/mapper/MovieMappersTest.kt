package com.mandiri.movieapp.data.mapper

import com.mandiri.movieapp.data.model.AuthorDetailsDto
import com.mandiri.movieapp.data.model.GenreDto
import com.mandiri.movieapp.data.model.MovieDetailDto
import com.mandiri.movieapp.data.model.MovieDto
import com.mandiri.movieapp.data.model.ReviewDto
import com.mandiri.movieapp.data.model.VideoTrailerDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieMappersTest {

    @Test
    fun `GenreDto toDomain maps correctly`() {
        val dto = GenreDto(id = 28, name = "Action")
        val domain = dto.toDomain()

        assertEquals(28, domain.id)
        assertEquals("Action", domain.name)
    }

    @Test
    fun `MovieDto toDomain handles null fields safely`() {
        val dto = MovieDto(
            id = 101,
            title = null,
            overview = null,
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = null,
            voteCount = null,
            genreIds = emptyList()
        )
        val domain = dto.toDomain()

        assertEquals(101, domain.id)
        assertEquals("", domain.title)
        assertEquals("", domain.overview)
        assertNull(domain.posterUrl)
        assertNull(domain.backdropUrl)
        assertEquals(0.0, domain.voteAverage, 0.001)
        assertEquals(0, domain.voteCount)
    }

    @Test
    fun `MovieDetailDto toDomain calculates formatted runtime and rating correctly`() {
        val dto = MovieDetailDto(
            id = 200,
            title = "Inception",
            runtime = 148,
            voteAverage = 8.357,
            genres = listOf(GenreDto(id = 878, name = "Science Fiction"))
        )
        val domain = dto.toDomain()

        assertEquals(200, domain.id)
        assertEquals("2h 28m", domain.formattedRuntime)
        assertEquals("8.4", domain.formattedVoteAverage)
        assertEquals(1, domain.genres.size)
        assertEquals("Science Fiction", domain.genres[0].name)
    }

    @Test
    fun `VideoTrailerDto toDomain evaluates isYoutube and isTrailer correctly`() {
        val dto = VideoTrailerDto(
            id = "vid_1",
            key = "dQw4w9WgXcQ",
            name = "Official Trailer",
            site = "YouTube",
            type = "Trailer",
            official = true
        )
        val domain = dto.toDomain()

        assertTrue(domain.isYoutube)
        assertTrue(domain.isTrailer)
        assertTrue(domain.isOfficial)
        assertEquals("dQw4w9WgXcQ", domain.youtubeKey)
    }

    @Test
    fun `ReviewDto toDomain parses Gravatar and standard avatar URLs properly`() {
        val standardDto = ReviewDto(
            id = "rev_1",
            author = "Reviewer A",
            content = "Great movie!",
            authorDetails = AuthorDetailsDto(
                username = "rev_a",
                avatarPath = "/user_avatar.jpg",
                rating = 9.0
            )
        )
        val gravatarDto = ReviewDto(
            id = "rev_2",
            author = "Reviewer B",
            content = "Awesome!",
            authorDetails = AuthorDetailsDto(
                username = "rev_b",
                avatarPath = "/https://secure.gravatar.com/avatar/sample.jpg",
                rating = 8.5
            )
        )

        val standardDomain = standardDto.toDomain()
        val gravatarDomain = gravatarDto.toDomain()

        assertTrue(standardDomain.authorAvatarUrl?.contains("w185/user_avatar.jpg") == true)
        assertEquals("https://secure.gravatar.com/avatar/sample.jpg", gravatarDomain.authorAvatarUrl)
    }
}
