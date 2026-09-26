package id.neotica.neotica.pages.holomarket

import id.neotica.neotica.utils.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicFeedItem(
    @SerialName("package_name") val packageName: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val categories: List<String> = emptyList(),
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("download_url") val downloadUrl: String = "",
)

@Serializable
data class PublicFeedResponse(
    val data: List<PublicFeedItem> = emptyList(),
    val page: Int = 1,
    val limit: Int = 12,
    @SerialName("total_items") val totalItems: Long = 0,
    @SerialName("total_pages") val totalPages: Int = 1,
)

@Serializable
data class PublicAppVersion(
    @SerialName("version_name") val versionName: String = "",
    @SerialName("version_code") val versionCode: Int = 0,
    @SerialName("download_url") val downloadUrl: String = "",
    val changelog: String = "",
    @SerialName("min_sdk") val minSdk: Int? = null,
    @SerialName("max_sdk") val maxSdk: Int? = null,
    @SerialName("created_at") val createdAt: Long = 0,
)

@Serializable
data class PublicAppDetail(
    @SerialName("package_name") val packageName: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val categories: List<String> = emptyList(),
    val developer: String = "",
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("created_at") val createdAt: Long = 0,
    val screenshots: List<String> = emptyList(),
    @SerialName("download_url") val downloadUrl: String = "",
    @SerialName("version_name") val versionName: String = "",
    @SerialName("version_code") val versionCode: Int = 0,
    val changelog: String = "",
    @SerialName("min_sdk") val minSdk: Int? = null,
    @SerialName("max_sdk") val maxSdk: Int? = null,
    @SerialName("average_rating") val averageRating: Double? = null,
    @SerialName("total_reviews") val totalReviews: Int = 0,
    val versions: List<PublicAppVersion> = emptyList(),
)

/**
 * Resolve a public feed/detail **media** URL (icon/screenshots). Relative paths are prefixed
 * with the public API host, and `http://` is upgraded to `https://` because the API emits plain
 * http which browsers block as mixed content inside `<img>` on an https page.
 */
fun resolvePublicUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    return when {
        url.startsWith("https://") -> url
        url.startsWith("http://") -> "https://" + url.removePrefix("http://")
        url.startsWith("/") -> Constants.PUBLIC_API_URL + url
        else -> url
    }
}

/** URL-encode a path segment / query value using the browser's `encodeURIComponent`. */
@Suppress("UnsafeCastFromDynamic")
fun encodeQueryComponent(value: String): String {
    val encoder: dynamic = js("encodeURIComponent")
    return encoder(value) as String
}