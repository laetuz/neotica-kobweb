package id.neotica.neotica.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class YoutubeVideo(
    val title: String,
    val description: String = "",
    val dateUploaded: Long,
    val url: String,
    val videoId: String,
)
