package id.neotica.neotica.domain.dummy

import id.neotica.neotica.components.icons.NeoIcons
import id.neotica.neotica.domain.model.ButtonData
import id.neotica.neotica.domain.model.MemberData
import id.neotica.neotica.domain.model.TechStacks
import kotlin.js.Date

class MemberList {
    val memberList = listOf(
        MemberData(
            name = "Ryo Martin Sopian",
            joinDate = Date(
                day = 4,
                month = 11,
                year = 2024,
            ),
            imageUrl = "https://avatars.githubusercontent.com/u/100233549",
            buttonLinks = listOf(
                ButtonData(
                    name = "Github",
                    url = "https://github.com/laetuz",
                    imageUrl = NeoIcons.GITHUB
                ),
                ButtonData(
                    name = "LinkedIn",
                    url = "https://linkedin.com/in/ryo-martin",
                    imageUrl = NeoIcons.LINKEDIN
                ),
            ),
            techStack = listOf(
                TechStacks(
                    "Kotlin", NeoIcons.KOTLIN
                ),
                TechStacks(
                    "Java", NeoIcons.JAVA
                ),
            ),
            url = "ryo-martin"
        ),
        MemberData(
            name = "Galih Putro Aji",
            joinDate = Date(
                day = 8,
                month = 7,
                year = 2025,
            ),
            url = "galih-putro-aji"
        ),
        MemberData(
            name = "Ayu Krisna",
            joinDate = Date(
                day = 27,
                month = 7,
                year = 2025,
            ),
            url = "ayu-krisna"
        ),
        MemberData(
            name = "Marshella Vindriani",
            joinDate = Date(
                day = 8,
                month = 7,
                year = 2025,
            ),
            url = "marshella-vindriani"
        ),
        MemberData(
            name = "Gilang Swandaru",
            joinDate = Date(
                day = 12,
                month = 6,
                year = 2025,
            ),
            url = "gilang-swandaru"
        ),
        MemberData(
            name = "Retsa Aghnita",
            joinDate = Date(
                day = 20,
                month = 10,
                year = 2025,
            ),
            url = "retsa-aghnita"
        ),
        MemberData(
            name = "Pasya Gerhardien",
            joinDate = Date(
                day = 9,
                month = 10,
                year = 2025,
            ),
            url = "pasya-gerhardien"
        ),
        MemberData(
            name = "Edwin Yosua",
            joinDate = Date(
                day = 12,
                month = 6,
                year = 2025,
            ),
            url = "edwin-yosua"
        )
    )
}