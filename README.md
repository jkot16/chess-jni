## Checkmate.JNI

[![Java 21](https://img.shields.io/badge/Java-21-red?logo=java)](https://www.oracle.com/java/) [![C%2B%2B 20](https://img.shields.io/badge/C%2B%2B-20-blue?logo=c%2B%2B)](https://isocpp.org/) [![Tests](https://img.shields.io/badge/JUnit–5-passing-brightgreen.svg)](https://junit.org/)

![chess](https://github.com/user-attachments/assets/3aa5ff42-2d27-48e5-954b-04ce5e397afb)
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

## 🎥 Demo

See **Checkmate.JNI** in action:  
▶️ [Watch on YouTube](https://www.youtube.com/watch?v=nk1ByrFHavY)

[![Checkmate.JNI Demo](https://img.youtube.com/vi/nk1ByrFHavY/0.jpg)](https://www.youtube.com/watch?v=nk1ByrFHavY)
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
| JNI Bridge        | Native methods (C++ `ChessBoard` class)       |
| Build System      | CMake (`libChess.dll`)        |
| Formatting Lib    | fmtlib                                    |


---

## 4. Architecture & Structure

- **`cpp/`** — C++ backend (engine logic + JNI)
  - `ChessBoard.cpp` / `ChessBoard.h`
  - `CMakeLists.txt` → builds native library (`libChess.dll` / `.so`)

- **`java/src/`** — Java frontend (UI + logic + tests)
  - `ChessBoard.java` — native bridge (JNI)
  - `ChessBoardGUI.java` — Swing-based interface
  - `ChessBoardTest.java` — unit tests (JUnit 5)

- **`figures/`** — chess piece icons (SVG/PNG)  
- **`sounds/`** — move & capture audio (WAV)


---

## 5. Build & Run

Follow these steps from your repository root:

### 5.1 🧩 Build the C++ Engine

```bash
cd cpp
mkdir -p build && cd build
cmake ..
cmake --build .
```
This will produce:

- cpp/build/Chess.dll on Windows
- cpp/build/libChess.so on Linux/macOS

### 5.2 📦 Install the Native Library
Copy the freshly built native library into your Java lib/ folder:

```bash
# Windows
cp build/Chess.dll ../java/lib/

# Linux/macOS
cp build/libChess.so ../java/lib/
```

### 5.3 🖥️ Compile & Run the Java GUI

```bash
cd ../java/src

# 1. Compile Java sources and generate JNI header in ../lib
javac -d ../out -h ../lib ChessBoard.java ChessBoardGUI.java ChessBoardTest.java

# 2. Run the GUI
java -cp ../out -Djava.library.path=../lib ChessBoardGUI
```

---

## 6. Running Tests

All core logic is covered with unit tests in `ChessBoardTest.java` using **JUnit 5**.

To run tests:

```bash
# Inside IntelliJ
Right-click -> Run ChessBoardTest
```

## 7. Roadmap

For more upcoming features and tracked improvements, see:  
👉 [GitHub Issues for Checkmate.JNI](https://github.com/jkot16/chess-jni/issues)


