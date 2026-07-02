package id.neotica.neotica.domain.model

data class AppProduct(
    val packageName: String,
    val title: String,
    val developer: String,
    val description: String,
    val iconUrl: String,
    val rating: Float,
    val category: String,
    val size: String,
    val installCount: String,
    val versionHistory: List<AppVersion>,
    val screenshots: List<String>,
)

data class AppVersion(
    val versionName: String,
    val versionCode: Int,
    val changelog: String,
    val minSdk: Int,
    val fileUrl: String,
)