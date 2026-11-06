package com.sayanox.scanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayanox.scanner.domain.PortProfile
import com.sayanox.scanner.domain.PortScanner
import com.sayanox.scanner.domain.ScanSession
import com.sayanox.scanner.domain.ScanUpdate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel : ViewModel() {

    private val scanner = PortScanner(timeoutMs = 700, maxConcurrent = 48)

    private val _session = MutableStateFlow(ScanSession())
    val session: StateFlow<ScanSession> = _session.asStateFlow()

    private var scanJob: Job? = null

    fun updateTarget(target: String) {
        _session.update { it.copy(target = target.trim()) }
    }

    fun selectProfile(profile: PortProfile) {
        if (_session.value.isRunning) return
        _session.update { it.copy(profile = profile) }
    }

    fun setCustomPorts(from: Int, to: Int) {
        if (from < 1 || to > 65535 || from > to) return
        val ports = (from..to).toList()
        _session.update {
            it.copy(
                profile = PortProfile.CUSTOM,
            )
        }
        customPortRange = from to to
    }

    private var customPortRange: Pair<Int, Int>? = null

    fun startScan() {
        val current = _session.value
        if (current.isRunning || current.target.isBlank()) return

        val ports = when (current.profile) {
            PortProfile.CUSTOM -> {
                val range = customPortRange ?: (1 to 1024)
                (range.first..range.second).toList()
            }
            else -> current.profile.ports
        }

        if (ports.isEmpty()) {
            _session.update { it.copy(errorMessage = "No ports selected") }
            return
        }

        scanJob?.cancel()
        _session.update {
            ScanSession(
                target = current.target,
                profile = current.profile,
                isRunning = true,
                totalPorts = ports.size,
                startTimeMs = System.currentTimeMillis()
            )
        }

        scanJob = viewModelScope.launch {
            scanner.scan(current.target, ports).collect { update ->
                when (update) {
                    is ScanUpdate.Started -> {
                        _session.update {
                            it.copy(totalPorts = update.totalPorts, isRunning = true)
                        }
                    }
                    is ScanUpdate.Progress -> {
                        _session.update {
                            it.copy(
                                scannedPorts = update.scanned,
                                openPorts = update.openPorts,
                                closedPortsCount = update.closedCount,
                                portsPerSecond = update.rate,
                                elapsedMs = update.elapsedMs
                            )
                        }
                    }
                    is ScanUpdate.Completed -> {
                        _session.update {
                            it.copy(
                                isRunning = false,
                                isCompleted = true,
                                openPorts = update.openPorts,
                                closedPortsCount = update.closedCount,
                                scannedPorts = it.totalPorts,
                                portsPerSecond = update.rate,
                                elapsedMs = update.elapsedMs
                            )
                        }
                    }
                    is ScanUpdate.Error -> {
                        _session.update {
                            it.copy(
                                isRunning = false,
                                errorMessage = update.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        _session.update {
            it.copy(
                isRunning = false,
                isCompleted = true,
                elapsedMs = System.currentTimeMillis() - it.startTimeMs
            )
        }
    }

    fun reset() {
        scanJob?.cancel()
        _session.value = ScanSession(target = _session.value.target)
    }
}
