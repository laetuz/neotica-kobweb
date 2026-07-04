package id.neotica.neotica.pages.auth

import kotlinx.browser.document
import kotlinx.browser.window

class AuthService {
    private fun getCookieAttributes(isDeletion: Boolean = false): String {
        val isLocalhost = window.location.hostname == "localhost" || window.location.hostname == "127.0.0.1"
        val isSecure = window.location.protocol == "https:"
        val targetDomain = ".neotica.id"

        return buildString {
            append("; Path=/")
            append("; SameSite=Lax")

            // If we are deleting, we inject the expiration date
            if (isDeletion) {
                append("; Expires=Thu, 01 Jan 1970 00:00:00 GMT")
            }

            // Only append the custom domain if we are NOT on localhost
            if (!isLocalhost) {
                append("; Domain=$targetDomain")
            }

            // Only require HTTPS if we are actually on HTTPS
            if (isSecure) {
                append("; Secure")
            }
        }
    }

    fun login(token: String, refreshToken: String) {
        window.localStorage.setItem("token", token)
        window.localStorage.setItem("refreshToken", refreshToken)

        document.cookie = "auth_token=$token${getCookieAttributes()}"
        document.cookie = "refresh_token=$refreshToken${getCookieAttributes()}"
    }

    fun logout() {
        window.localStorage.removeItem("token")
        window.localStorage.removeItem("refreshToken")

        document.cookie = "auth_token=${getCookieAttributes(true)}"
    }

    fun isLoggedIn(): Boolean {
        val allCookies = document.cookie

        return allCookies.split(";").any {
            it.trim().startsWith("auth_token=") && it.trim().length > 11 // "auth_token=" is 11 chars
        }
    }
}