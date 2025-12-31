package id.neotica.neotica.domain.model

import kotlin.js.Date

data class MemberData(
    val name: String,
    val joinDate: Date,
    val imageUrl: String = "",
    val buttonLinks: List<ButtonData> = emptyList(),
    val techStack: List<TechStacks> = emptyList(),
    val url: String = "",
)