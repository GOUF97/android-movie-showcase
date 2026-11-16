package com.mandiri.movieapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieVideosResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("results") val results: List<VideoTrailerDto> = emptyList()
)

@Serializable
data class VideoTrailerDto(
    @SerialName("id") val id: String,
    @SerialName("key") val key: String,
    @SerialName("name") val name: String? = null,
    @SerialName("site") val site: String? = null,
    @SerialName("size") val size: Int? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("official") val official: Boolean? = null,
    @SerialName("published_at") val publishedAt: String? = null
)
