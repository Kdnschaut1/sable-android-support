# SableBridge

Android compatibility layer for Sable (NeoForge)

---

## Overview:

SableBridge adds Android support for the Sable physics mod, allowing it to run on environments like Pojav Launcher.

Sable is designed for desktop JVM environments and relies on native libraries that are not compatible with Android by default.  
This mod bridges that gap by redirecting native loading and adapting runtime behavior for mobile systems.

---

## Features:

- Android environment detection
- Native library redirection (Android-compatible build)
- Runtime compatibility layer for Sable
- Fully client-side

---

## Requirements:

- NeoForge `1.21.1`
- Sable (required)
- Java runtime (Pojav Launcher for Android)

---

## Installation

1. Install Sable
2. Install SableBridge on Modrinth or Curseforge
3. Launch the game (Pojav or desktop)

No configuration required.

---

## How it works

SableBridge uses NeoForge's coremod system to modify behavior at runtime:

- Redirects `System.load()` inside Sable to load an Android-compatible native library
- Applies all changes in-memory (no files are modified)

---

## ⚠️ Disclaimer

- This project **does not include or redistribute any part of Sable!**
- Sable must be installed separately
- This mod only modifies runtime behavior for compatibility

---


## 📜 Licensing

- This project contains **no code from Sable**
- Native library is built from Rapier (Apache 2.0)
- This addon is independent and requires Sable as a dependency

---

## ❗ Known Issues

- Game Freezes with Some of Aeronautics Blocks

---

## Credits:

- Sable by RyanHCode  

---

## 💬 Support

Open an issue if you encounter bugs or compatibility problems.
