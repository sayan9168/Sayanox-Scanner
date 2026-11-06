# Sayanox // Scanner

```
███████╗ █████╗ ██╗   ██╗ █████╗ ███╗   ██╗ ██████╗ ██╗  ██╗
██╔════╝██╔══██╗╚██╗ ██╔╝██╔══██╗████╗  ██║██╔═══██╗╚██╗██╔╝
███████╗███████║ ╚████╔╝ ███████║██╔██╗ ██║██║   ██║ ╚███╔╝ 
╚════██║██╔══██║  ╚██╔╝  ██╔══██║██║╚██╗██║██║   ██║ ██╔██╗ 
███████║██║  ██║   ██║   ██║  ██║██║ ╚████║╚██████╔╝██╔╝ ██╗
╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝
           // SCANNER  ·  Unprivileged Network Diagnostics
```

**High-performance, unprivileged local network diagnostics & TCP port scanner for Android.**

> Cyber-neon minimalist dark UI · Real-time rate metrics · Multi-profile scanning · Latency tracking

---

## Overview

**Sayanox // Scanner** is a clean, modern Android application designed for network administrators, security researchers, developers, and power users who need fast, reliable port scanning **without root privileges**.

It performs pure **TCP connect scans** using standard Java sockets. No Nmap binary, no root, no special permissions beyond normal network access.

### Key Features

| Feature | Description |
|---------|-------------|
| **Multi-Profile Scanning** | Common (Top 20), Well-Known (1–1023), Registered, Full (1–65535), Custom range |
| **Real-time Metrics** | Ports/sec rate, elapsed time, progress bar with neon glow |
| **Open Port Categorization** | Automatic service hints (SSH, HTTP, MySQL, RDP, Redis, etc.) |
| **Latency Tracking** | Millisecond response time for every open port |
| **Cyber-Neon UI** | Pure dark theme with cyan / magenta / green neon accents, monospace typography |
| **Concurrent Engine** | Configurable parallelism (default 48 concurrent sockets) + timeout control |
| **Stop / Resume friendly** | Cancel mid-scan safely |
| **Zero Root** | Works on any Android 8.0+ device without special privileges |

---

## Requirements

- **Android 8.0 (API 26)** or higher
- Internet / local network permission (requested at install)
- No root required

---

## Installation

### Option 1 – Pre-built APK (Recommended)

A ready-to-install APK will be available in **GitHub Releases**.

1. Go to the [Releases](https://github.com/sayan9168/Sayanox-Scanner/releases) page.
2. Download the latest `.apk`.
3. Enable “Install from unknown sources” and install.

### Option 2 – Build from Source

```bash
git clone https://github.com/sayan9168/Sayanox-Scanner.git
cd Sayanox-Scanner
```

Open the project in **Android Studio** (Hedgehog / Iguana or newer).

```bash
./gradlew assembleDebug
```

---

## Project Structure

```
Sayanox-Scanner/
├── app/
│   ├── src/main/
│   │   ├── java/com/sayanox/scanner/
│   │   │   ├── domain/               # Pure business logic
│   │   │   │   ├── PortProfile.kt
│   │   │   │   ├── PortScanner.kt    # Core concurrent TCP scanner
│   │   │   │   └── ScanResult.kt
│   │   │   ├── ui/
│   │   │   │   ├── components/       # NeonCard, Progress, Stats
│   │   │   │   ├── screens/
│   │   │   │   │   └── ScannerScreen.kt
│   │   │   │   ├── theme/            # Cyber-neon colors & typography
│   │   │   │   └── ScannerViewModel.kt
│   │   │   ├── MainActivity.kt
│   │   │   └── SayanoxApp.kt
│   │   ├── res/                      # Icons, themes, strings
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── .github/workflows/android.yml     # CI
├── LICENSE                           # MIT
├── README.md
└── ...
```

---

## Architecture

- **Clean modular design**
  - `domain/` → pure Kotlin scanning engine (no Android dependencies)
  - `ui/` → Jetpack Compose + ViewModel
- **Coroutines + Flow** for progressive real-time updates
- **Semaphore-limited concurrency** to avoid overwhelming the device or network
- **Material 3** + fully custom cyber-neon dark color scheme

### Scanning Engine Highlights

```kotlin
PortScanner(
    timeoutMs = 700,      // per-port connect timeout
    maxConcurrent = 48    // simultaneous sockets
)
```

- Uses `java.net.Socket` → works on every Android version without root
- Emits `ScanUpdate` sealed class (Started / Progress / Completed / Error)
- Automatically sorts open ports and attaches service name hints

---

## Ethical & Legal Notice

This tool is intended **only** for:

- Scanning devices and networks you own
- Scanning networks you have explicit written permission to test
- Educational and research purposes in controlled environments

**Unauthorized port scanning may be illegal in your jurisdiction.**  
The authors and contributors accept no liability for misuse.

Always obtain proper authorization before scanning any system that does not belong to you.

---

## Tech Stack

| Layer            | Technology                          |
|------------------|-------------------------------------|
| Language         | Kotlin 2.0                          |
| UI               | Jetpack Compose + Material 3        |
| Architecture     | MVVM + Domain layer                 |
| Async            | Kotlin Coroutines + Flow            |
| Min SDK          | 26 (Android 8.0)                    |
| Target SDK       | 35                                  |
| Build            | AGP 8.7 + Gradle 8.9                |

---

## License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

## Author

**Sayan Mahata** ([@sayan9168](https://github.com/sayan9168))

Part of the **Sayanox** ecosystem — AI-native tools, languages, and security research projects.

```
Made with ⚡ and neon lights
```
