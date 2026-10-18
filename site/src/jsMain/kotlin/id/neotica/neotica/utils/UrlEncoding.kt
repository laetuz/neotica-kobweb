package id.neotica.neotica.utils

/** URL-encode a path segment or query value using the browser's `encodeURIComponent`. */
@Suppress("UnsafeCastFromDynamic")
fun encodeQueryComponent(value: String): String {
    val encoder: dynamic = js("encodeURIComponent")
    return encoder(value) as String
}

/** Decode a percent-encoded query value using the browser's `decodeURIComponent`. */
@Suppress("UnsafeCastFromDynamic")
fun decodeQueryComponent(value: String): String {
    val decoder: dynamic = js("decodeURIComponent")
    return decoder(value) as String
}

/** Read a raw (decoded) query parameter from a URL search string like `?q=x&page=2`. */
fun queryParam(search: String, name: String): String? {
    val raw = search.removePrefix("?")
    if (raw.isEmpty()) return null
    val value = raw.split("&")
        .firstOrNull { it.substringBefore("=") == name }
        ?.substringAfter("=", "")
        ?: return null
    return try {
        decodeQueryComponent(value)
    } catch (_: Throwable) {
        value
    }
}