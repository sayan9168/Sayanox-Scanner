package com.sayanox.scanner.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sayanox.scanner.domain.PortProfile
import com.sayanox.scanner.domain.PortResult
import com.sayanox.scanner.ui.ScannerViewModel
import com.sayanox.scanner.ui.components.NeonCard
import com.sayanox.scanner.ui.components.NeonProgressBar
import com.sayanox.scanner.ui.components.SectionHeader
import com.sayanox.scanner.ui.components.StatChip
import com.sayanox.scanner.ui.theme.DarkBackground
import com.sayanox.scanner.ui.theme.NeonCyan
import com.sayanox.scanner.ui.theme.NeonGreen
import com.sayanox.scanner.ui.theme.NeonMagenta
import com.sayanox.scanner.ui.theme.TextMuted
import com.sayanox.scanner.ui.theme.TextPrimary
import com.sayanox.scanner.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel()
) {
    val session by viewModel.session.collectAsState()
    var customFrom by remember { mutableStateOf("1") }
    var customTo by remember { mutableStateOf("1024") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SAYANOX // SCANNER",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.reset() }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Reset",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader("TARGET")
            OutlinedTextField(
                value = session.target,
                onValueChange = { viewModel.updateTarget(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("192.168.1.1 or hostname", color = TextMuted) },
                singleLine = true,
                enabled = !session.isRunning,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.4f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(20.dp))

            SectionHeader("SCAN PROFILE")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    PortProfile.COMMON,
                    PortProfile.WELL_KNOWN,
                    PortProfile.REGISTERED
                ).forEach { profile ->
                    FilterChip(
                        selected = session.profile == profile,
                        onClick = { viewModel.selectProfile(profile) },
                        label = { Text(profile.displayName, style = MaterialTheme.typography.labelMedium) },
                        enabled = !session.isRunning,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            containerColor = Color.Transparent,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = TextMuted.copy(alpha = 0.3f),
                            selectedBorderColor = NeonCyan,
                            enabled = true,
                            selected = session.profile == profile
                        )
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = session.profile == PortProfile.FULL,
                    onClick = { viewModel.selectProfile(PortProfile.FULL) },
                    label = { Text("Full 1-65535", style = MaterialTheme.typography.labelMedium) },
                    enabled = !session.isRunning,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonMagenta.copy(alpha = 0.2f),
                        selectedLabelColor = NeonMagenta
                    )
                )
                FilterChip(
                    selected = session.profile == PortProfile.CUSTOM,
                    onClick = { viewModel.selectProfile(PortProfile.CUSTOM) },
                    label = { Text("Custom", style = MaterialTheme.typography.labelMedium) },
                    enabled = !session.isRunning,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonGreen.copy(alpha = 0.2f),
                        selectedLabelColor = NeonGreen
                    )
                )
            }

            AnimatedVisibility(visible = session.profile == PortProfile.CUSTOM) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customFrom,
                        onValueChange = { customFrom = it.filter { c -> c.isDigit() }.take(5) },
                        label = { Text("From") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = TextMuted.copy(alpha = 0.4f)
                        )
                    )
                    Text("→", color = TextSecondary)
                    OutlinedTextField(
                        value = customTo,
                        onValueChange = { customTo = it.filter { c -> c.isDigit() }.take(5) },
                        label = { Text("To") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = TextMuted.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (session.profile == PortProfile.CUSTOM) {
                            val from = customFrom.toIntOrNull() ?: 1
                            val to = customTo.toIntOrNull() ?: 1024
                            viewModel.setCustomPorts(from, to)
                        }
                        viewModel.startScan()
                    },
                    enabled = !session.isRunning && session.target.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = DarkBackground,
                        disabledContainerColor = NeonCyan.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("START SCAN", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.stopScan() },
                    enabled = session.isRunning,
                    modifier = Modifier.weight(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonMagenta,
                        contentColor = Color.White,
                        disabledContainerColor = NeonMagenta.copy(alpha = 0.25f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("STOP")
                }
            }

            Spacer(Modifier.height(20.dp))

            if (session.isRunning || session.isCompleted) {
                SectionHeader("LIVE METRICS")
                NeonProgressBar(
                    progress = session.progress,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatChip(label = "OPEN", value = "${session.openCount}", accent = NeonGreen, modifier = Modifier.weight(1f))
                    StatChip(label = "CLOSED", value = "${session.closedPortsCount}", accent = TextSecondary, modifier = Modifier.weight(1f))
                    StatChip(label = "RATE", value = "%.0f/s".format(session.portsPerSecond), accent = NeonCyan, modifier = Modifier.weight(1f))
                    StatChip(label = "TIME", value = formatMs(session.elapsedMs), accent = NeonMagenta, modifier = Modifier.weight(1f))
                }

                Text(
                    text = "${session.scannedPorts} / ${session.totalPorts} ports",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (session.openPorts.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                SectionHeader("OPEN PORTS (${session.openCount})")

                session.openPorts.forEach { result ->
                    OpenPortRow(result)
                    Spacer(Modifier.height(6.dp))
                }
            }

            session.errorMessage?.let { msg ->
                Spacer(Modifier.height(16.dp))
                Text(text = "⚠ $msg", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OpenPortRow(result: PortResult) {
    NeonCard(
        borderColor = NeonGreen.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "PORT ${result.port}", style = MaterialTheme.typography.titleMedium, color = NeonGreen, fontWeight = FontWeight.Bold)
                Text(text = result.serviceHint, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            if (result.latencyMs >= 0) {
                Text(text = "${result.latencyMs} ms", style = MaterialTheme.typography.labelLarge, color = NeonCyan)
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val sec = ms / 1000
    return if (sec < 60) "${sec}s" else "${sec / 60}m ${sec % 60}s"
}
