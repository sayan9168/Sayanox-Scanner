package com.sayanox.scanner.domain

/**
 * Predefined port scanning profiles.
 * All scans are performed using TCP connect (unprivileged).
 */
enum class PortProfile(
    val displayName: String,
    val description: String,
    val ports: List<Int>
) {
    COMMON(
        displayName = "Common",
        description = "Top 20 frequently used ports",
        ports = listOf(
            21, 22, 23, 25, 53, 80, 110, 111, 135, 139,
            143, 443, 445, 993, 995, 1723, 3306, 3389, 5900, 8080
        )
    ),
    WELL_KNOWN(
        displayName = "Well-Known",
        description = "Ports 1–1023 (system ports)",
        ports = (1..1023).toList()
    ),
    REGISTERED(
        displayName = "Registered",
        description = "Ports 1024–49151 (common registered)",
        ports = listOf(
            1433, 1521, 2049, 3306, 3389, 5432, 5900, 6379,
            8080, 8443, 9200, 27017, 50000, 11211
        ) + (1024..2000).toList()
    ),
    FULL(
        displayName = "Full",
        description = "All ports 1–65535 (very slow)",
        ports = (1..65535).toList()
    ),
    CUSTOM(
        displayName = "Custom",
        description = "User-defined range or list",
        ports = emptyList() // filled at runtime
    );

    companion object {
        fun fromName(name: String): PortProfile =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: COMMON
    }
}
