package com.sayanox.scanner.util

import java.net.InetAddress
import java.util.regex.Pattern

object NetworkUtils {

    private val IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    )

    fun isValidIPv4(ip: String): Boolean = IPV4_PATTERN.matcher(ip).matches()

    fun isValidHostnameOrIp(input: String): Boolean {
        if (input.isBlank()) return false
        if (isValidIPv4(input)) return true
        // Basic hostname check
        return input.matches(Regex("^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*$"))
    }

    /**
     * Best-effort resolve. Returns original string on failure.
     */
    fun resolveHost(host: String): String {
        return try {
            InetAddress.getByName(host).hostAddress ?: host
        } catch (_: Exception) {
            host
        }
    }
}
