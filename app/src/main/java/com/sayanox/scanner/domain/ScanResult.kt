package com.sayanox.scanner.domain

/**
 * Represents the result of a single port probe.
 */
data class PortResult(
    val port: Int,
    val isOpen: Boolean,
    val latencyMs: Long = -1,
    val serviceHint: String = guessService(port)
) {
    companion object {
        fun guessService(port: Int): String = when (port) {
            21 -> "FTP"
            22 -> "SSH"
            23 -> "Telnet"
            25 -> "SMTP"
            53 -> "DNS"
            80, 8080 -> "HTTP"
            110 -> "POP3"
            143 -> "IMAP"
            443, 8443 -> "HTTPS"
            445 -> "SMB"
            993 -> "IMAPS"
            995 -> "POP3S"
            1433 -> "MSSQL"
            1521 -> "Oracle"
            3306 -> "MySQL"
            3389 -> "RDP"
            5432 -> "PostgreSQL"
            5900 -> "VNC"
            6379 -> "Redis"
            9200 -> "Elasticsearch"
            27017 -> "MongoDB"
            else -> "Unknown"
        }
    }
}

/**
 * Aggregated scan session state.
 */
data class ScanSession(
    val target: String = "",
    val profile: PortProfile = PortProfile.COMMON,
    val isRunning: Boolean = false,
    val isCompleted: Boolean = false,
    val totalPorts: Int = 0,
    val scannedPorts: Int = 0,
    val openPorts: List<PortResult> = emptyList(),
    val closedPortsCount: Int = 0,
    val filteredPortsCount: Int = 0,
    val startTimeMs: Long = 0L,
    val elapsedMs: Long = 0L,
    val portsPerSecond: Float = 0f,
    val errorMessage: String? = null
) {
    val progress: Float
        get() = if (totalPorts == 0) 0f else scannedPorts.toFloat() / totalPorts

    val openCount: Int get() = openPorts.size
}
