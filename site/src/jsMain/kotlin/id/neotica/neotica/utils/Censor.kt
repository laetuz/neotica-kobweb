package id.neotica.neotica.utils

import kotlin.random.Random

/**
 * Masks words from a censor list (whole-word, case-insensitive).
 *
 * Star count by word length:
 * - 4 letters or fewer -> 1 star
 * - 5 to 8 letters     -> 2 stars
 * - 9+ letters         -> 3 stars
 *
 * Positions are deterministic (seeded by the word), so re-renders are stable.
 * Reusable from any page: `Censor.apply(text)`.
 */
object Censor {
    /** Lowercase words to mask. Extend or trim freely. */
    val WORDS: Set<String> = setOf(
        "fuck", "shit", "bitch", "bastard", "asshole", "cunt", "dick", "piss",
        "slut", "whore", "fag", "faggot", "retard", "nigger", "nigga",
        "hitler", "nazi"
    )

    fun apply(text: String): String {
        if (text.isEmpty() || WORDS.isEmpty()) return text
        return Regex("[A-Za-z]+").replace(text) { match ->
            val word = match.value
            if (word.lowercase() in WORDS) maskWord(word) else word
        }
    }

    private fun maskWord(word: String): String {
        val stars = when (word.length) {
            in 0..4 -> 1
            in 5..8 -> 2
            else -> 3
        }.coerceAtMost(word.length)

        val random = Random(word.lowercase().hashCode())
        val picks = mutableSetOf<Int>()
        while (picks.size < stars) {
            picks += random.nextInt(word.length)
        }

        return word.mapIndexed { index, char ->
            if (index in picks) '*' else char
        }.joinToString("")
    }
}