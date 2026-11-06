package com.sayanox.scanner.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.system.measureTimeMillis

/**
 * High-performance, unprivileged TCP connect port scanner.
 *
 * Uses pure Java sockets — no root required.
 * Concurrent scanning with configurable parallelism and timeout.
 */
class PortScanner(
    private val timeoutMs: Int = 800,
    private val maxConcurrent: Int = 64
) {

    /**
     * Scans the given ports against [host] and emits progressive updates.
     */
    fun scan(
        host: String,
        ports: List<Int>
    ): Flow<ScanUpdate> = channelFlow {
        if (ports.isEmpty()) {
            send(ScanUpdate.Error("No ports to scan"))
            return@channelFlow
        }

        val total = ports.size
        var scanned = 0
        var openCount = 0
        var closedCount = 0
        val openResults = mutableListOf<PortResult>()
        val startTime = System.currentTimeMillis()

        send(ScanUpdate.Started(total))

        val semaphore = Semaphore(maxConcurrent)

        coroutineScope {
            val jobs = ports.map { port ->
                async(Dispatchers.IO) {
                    if (!isActive) return@async null

                    semaphore.withPermit {
                        val result = probePort(host, port)
                        scanned++
                        if (result.isOpen) {
                            openCount++
                            synchronized(openResults) {
                                openResults.add(result)
                            }
                        } else {
                            closedCount++
                        }

                        val elapsed = System.currentTimeMillis() - startTime
                        val rate = if (elapsed > 0) scanned * 1000f / elapsed else 0f

                        send(
                            ScanUpdate.Progress(
                                scanned = scanned,
                                total = total,
                                openPorts = openResults.sortedBy { it.port }.toList(),
                                closedCount = closedCount,
                                rate = rate,
                                elapsedMs = elapsed
                            )
                        )
                        result
                    }
                }
            }
            jobs.awaitAll()
        }

        val elapsed = System.currentTimeMillis() - startTime
        send(
            ScanUpdate.Completed(
                openPorts = openResults.sortedBy { it.port },
                closedCount = closedCount,
                elapsedMs = elapsed,
                rate = if (elapsed > 0) total * 1000f / elapsed else 0f
            )
        )
    }

    private suspend fun probePort(host: String, port: Int): PortResult = withContext(Dispatchers.IO) {
        var latency = -1L
        val isOpen = try {
            latency = measureTimeMillis {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, port), timeoutMs)
                }
            }
            true
        } catch (_: SocketTimeoutException) {
            false
        } catch (_: Exception) {
            false
        }
        PortResult(port = port, isOpen = isOpen, latencyMs = if (isOpen) latency else -1)
    }
}

sealed class ScanUpdate {
    data class Started(val totalPorts: Int) : ScanUpdate()
    data class Progress(
        val scanned: Int,
        val total: Int,
        val openPorts: List<PortResult>,
        val closedCount: Int,
        val rate: Float,
        val elapsedMs: Long
    ) : ScanUpdate()
    data class Completed(
        val openPorts: List<PortResult>,
        val closedCount: Int,
        val elapsedMs: Long,
        val rate: Float
    ) : ScanUpdate()
    data class Error(val message: String) : ScanUpdate()
}
