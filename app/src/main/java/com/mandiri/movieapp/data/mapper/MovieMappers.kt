package com.mandiri.movieapp.data.mapper

import com.mandiri.movieapp.BuildConfig
import com.mandiri.movieapp.data.model.GenreDto
import com.mandiri.movieapp.data.model.MovieDetailDto
import com.mandiri.movieapp.data.model.MovieDto
import com.mandiri.movieapp.data.model.ReviewDto
import com.mandiri.movieapp.data.model.VideoTrailerDto
import com.mandiri.movieapp.domain.model.Genre
import com.mandiri.movieapp.domain.model.Movie
import com.mandiri.movieapp.domain.model.MovieDetail
import com.mandiri.movieapp.domain.model.Review
import com.mandiri.movieapp.domain.model.VideoTrailer

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun MovieDto.toDomain(): Movie {
    val posterUrl = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}w500$it" }
    val backdropUrl = backdropPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}w780$it" }

    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        genreIds = genreIds
    )
}

fun MovieDetailDto.toDomain(): MovieDetail {
    val posterUrl = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}w500$it" }
    val backdropUrl = backdropPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}w780$it" }

    return MovieDetail(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        runtimeMinutes = runtime,
        tagline = tagline.orEmpty(),
        status = status.orEmpty(),
        genres = genres.map { it.toDomain() }
    )
}

fun VideoTrailerDto.toDomain(): VideoTrailer {
    return VideoTrailer(
        id = id,
        youtubeKey = key,
        title = name.orEmpty(),
        site = site.orEmpty(),
        type = type.orEmpty(),
        isOfficial = official ?: false
    )
}

fun ReviewDto.toDomain(): Review {
    val rawAvatar = authorDetails?.avatarPath
    val avatarUrl = when {
        rawAvatar == null -> null
        rawAvatar.startsWith("/http") -> rawAvatar.removePrefix("/")
        rawAvatar.startsWith("http") -> rawAvatar
        else -> "${BuildConfig.TMDB_IMAGE_BASE_URL}w185$rawAvatar"
    }

    return Review(
        id = id,
        author = author,
        content = content,
        createdAt = createdAt.orEmpty(),
        authorAvatarUrl = avatarUrl,
        rating = authorDetails?.rating
    )
}
