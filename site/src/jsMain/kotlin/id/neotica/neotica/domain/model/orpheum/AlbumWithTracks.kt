package id.neotica.neotica.domain.model.orpheum

import kotlinx.serialization.Serializable

@Serializable
data class AlbumWithTracks(
    val album: AlbumRemoteModel,
    val tracks: List<TrackRemoteModel> = emptyList(),
)