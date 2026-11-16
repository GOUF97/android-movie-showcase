package com.mandiri.movieapp.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val runtimeMinutes: Int?,
    val tagline: String,
    val status: String,
    val genres: List<Genre>
) {
    val formattedRuntime: String
        get() {
            val minutes = runtimeMinutes ?: return "N/A"
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            return if (hours > 0) "${hours}h ${remainingMinutes}m" else "${remainingMinutes}m"
        }

    val formattedVoteAverage: String
        get() = String.format(java.util.Locale.US, "%.1f", voteAverage)
}
