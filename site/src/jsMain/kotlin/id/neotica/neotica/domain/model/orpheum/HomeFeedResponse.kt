package id.neotica.neotica.domain.model.orpheum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeFeedResponse(
    @SerialName("recent_tracks") val recentTracks: List<TrackRemoteModel> = emptyList(),
    @SerialName("recent_albums") val recentAlbums: List<AlbumRemoteModel> = emptyList(),
)

@Serializable
data class AlbumRemoteModel(
    val id: String,
    @SerialName("artist_id") val artistId: String,
    val title: String,
    @SerialName("release_year") val releaseYear: Int = 0,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("artist_name") val artistName: String = "",
)