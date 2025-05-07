## Checkmate.JNI

[![Java 21](https://img.shields.io/badge/Java-21-red?logo=java)](https://www.oracle.com/java/) [![C%2B%2B 20](https://img.shields.io/badge/C%2B%2B-20-blue?logo=c%2B%2B)](https://isocpp.org/) [![Tests](https://img.shields.io/badge/JUnit–5-passing-brightgreen.svg)](https://junit.org/)

## Table of Contents

1. [🔍 Project Overview](#1-project-overview)  
2. [🛠️ Features](#2-features)  
3. [⚙️ Tech Stack](#3-tech-stack)  
4. [🧱📁 Architecture & Structure](#4-architecture--structure)  
5. [🚀 Build & Run](#5-build--run)  
6. [🧪 Running Tests](#6-running-tests)  
7. [🗺️ Roadmap](#7-roadmap)


---

## 1. Project Overview

**Checkmate.JNI** is a fully-featured native chess engine with a **Java/Swing interface** and a **C++20 backend** accessed via **JNI**.  
Designed for precision, clarity, and performance — it brings real chess logic to life in a clean desktop experience.

> Whether you're analyzing or just playing — it feels like real chess.


---

## 2. Features

- **Full rules** — legal moves, captures, turns, promotion  
- **Endgame detection** — check, checkmate, stalemate  
- **Scoring** — live material count (Q=9, R=5, B/N=3, P=1)  
- **Controls** — mouse or keyboard (arrows + Enter)  
- **Highlights** — turn display & check indicators  
- **Sounds** — move and capture feedback  
- **Tests** — JUnit 5 coverage for all core logic


---
## 3. Tech Stack

### Frontend (Java)

| Feature           | Technology                                |
|-------------------|-------------------------------------------|
| Language          | Java 21                                   |
| UI Framework      | Swing (`JFrame`, `JButton`, `GridLayout`) |
| Input Handling    | Mouse clicks & key bindings               |
| Audio Playback    | `javax.sound.sampled.*`                   |
| Testing           | JUnit 5                                   |

### Backend (C++)

| Feature           | Technology                                |
|-------------------|-------------------------------------------|
| Language          | C++20                                     |
| JNI Bridge        | Native methods (C++ `ChessBoard ` class)       |
| Build System      | CMake (`libChess.dll`)        |
| Formatting Lib    | fmtlib                                    |


---

## 4. Architecture & Structure

- **`cpp/`** — C++ backend (engine logic + JNI)
  - `ChessBoard.cpp` / `ChessBoard.h`
  - `library.cpp`
  - `CMakeLists.txt` → builds native library (`libChess.dll` / `.so`)

- **`java/src/`** — Java frontend (UI + logic + tests)
  - `ChessBoard.java` — native bridge (JNI)
  - `ChessBoardGUI.java` — Swing-based interface
  - `ChessBoardTest.java` — unit tests (JUnit 5)

- **`figures/`** — chess piece icons (SVG/PNG)  
- **`sounds/`** — move & capture audio (WAV)


---

## 5. Build & Run

### 🧩 Native Library (C++)
1. Open `cpp/` folder in CLion (or any CMake IDE)
2. Build to generate `libChess.dll` (or `.so`)
3. Copy the resulting file to the `java/` directory

### 🖥️ Java GUI
1. Open `java/` in IntelliJ IDEA
2. Run `ChessBoardGUI.java`
3. If `libChess.dll` not found:
   - Ensure it's in project root
   - Add MinGW path to your system’s `PATH`

---

## 6. Running Tests

All core logic is covered with unit tests in `ChessBoardTest.java` using **JUnit 5**.

To run tests:

```bash
# Inside IntelliJ
Right-click -> Run ChessBoardTest
```

## 7. Roadmap

Planned improvements and upcoming features:

- **En passant** support *(currently missing)*
-  **Visual themes** – board color presets *(e.g. dark mode, blue)*
-  **Custom piece sets** – drag-and-drop alternative icons
-  **Save/load game state** – export/import from file
-  **Basic AI opponent** – using minimax or heuristic evaluation
-  **LAN multiplayer** *(stretch goal)*

