package id.neotica.neotica.domain.model.orpheum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackRemoteModel(
    val id: String,
    @SerialName("album_id") val albumId: String,
    val title: String,
    @SerialName("duration_seconds") val durationSeconds: Int,
    @SerialName("file_url") val fileUrl: String,
    @SerialName("track_number") val trackNumber: Int,
    @SerialName("artist_name") val artistName: String,
    @SerialName("album_cover") val coverUrl: String? = null
)