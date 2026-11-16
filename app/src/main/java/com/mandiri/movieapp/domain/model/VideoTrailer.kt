package com.mandiri.movieapp.domain.model

data class VideoTrailer(
    val id: String,
    val youtubeKey: String,
    val title: String,
    val site: String,
    val type: String,
    val isOfficial: Boolean
) {
    val isYoutube: Boolean
        get() = site.equals("YouTube", ignoreCase = true)

    val isTrailer: Boolean
        get() = type.equals("Trailer", ignoreCase = true)
}
