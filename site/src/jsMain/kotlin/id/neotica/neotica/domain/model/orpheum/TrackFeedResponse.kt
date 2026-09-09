package id.neotica.neotica.domain.model.orpheum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackFeedResponse(
    val data: List<TrackRemoteModel>,
    val page: Int,
    val limit: Int,
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int
)