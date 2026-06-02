# DaimyoSimulator — Project Manual

This manual is written so that **anyone can run the project, even with zero knowledge
of Java, Maven, or programming**. Sections 3–4 are a click-by-click setup guide; the
later sections document requirements, reused libraries, external APIs, and the AI
tools used during development.

---

## 1. High-level project description

DaimyoSimulator is a Java **rule-based simulation game** inspired by SimCity-style
city builders, set in an ancient Japanese village around the year 1200. The player
grows a village by placing buildings on a grid, managing resources and population,
activating one strategy policy at a time, reacting to random events, and advancing
time through discrete simulation **ticks**.

The software is split into three responsibilities, each in its own Maven module:

1. **Core game logic (pure Java)** — the village model, the logical grid, buildings
   and placement rules, villagers and jobs, the economy/resources, strategy policies,
   random events, save/load, and the tick engine. This module contains all the rules
   and is fully testable on its own, without any graphics.
2. **Game world renderer (libGDX)** — draws the village map, tiles, buildings,
   forests, and animations.
3. **Game UI / HUD (libGDX Scene2D)** — build buttons, resource/population/parameter
   panels, the selected-building panel, pause/speed/next-tick controls, policy
   buttons, menus, dashboard, and the event/status log.

The core module must never depend on the graphics library: the renderer and UI talk
to the core only through controllers, facades, DTOs, immutable snapshots, and view
models. This keeps the rules independent of how the game is displayed.

---

## 2. Runtime and development requirements

| Requirement | Choice / constraint |
|---|---|
| Programming language | Java |
| **Required Java version** | **Java 17 (JDK)** — the project is compiled with `release 17`. Java 8 or 11 will **not** build it. Newer JDKs (e.g. 21) can usually build it, but **Java 17 LTS is the supported/recommended version**. |
| JDK vs JRE | You need the **JDK** (Development Kit), not just the runtime (JRE). |
| Build tool | Apache Maven 3.8+ (3.9.x recommended) |
| Core testing | JUnit 5 (Jupiter) |
| Graphics / UI framework | libGDX 1.12.1 |
| Desktop backend | libGDX LWJGL3 backend |
| Persistence format | JSON (file-based, no database required) |
| Operating systems | Windows, Linux, macOS (macOS needs a special launch command — see §4) |
| Optional IDE | IntelliJ IDEA, Eclipse, or VS Code with Java + Maven support |

> **Why Java 17 specifically?** The build is configured with
> `<maven.compiler.release>17</maven.compiler.release>`. If Maven runs on an older
> JDK you will get a `release version 17 not supported` error. Check your version
> with `mvn -version` — the output must mention Java 17.

---

## 3. Installation (foolproof, one time only)

You only need **two free tools**: a Java 17 JDK and Maven. The repository's
[README](../README.md) contains the same instructions with extra screenshots-level
detail and per-OS package-manager options; the short version is below.

### Step 1 — Install Java 17 (JDK)

1. Open <https://adoptium.net/temurin/releases/?version=17>.
2. Download the **JDK 17** installer for your operating system and run it.
3. **On Windows**, during setup enable **"Set JAVA_HOME variable"** and
   **"Add to PATH"** (click the drive icon next to each → "Will be installed").
4. Open a **new** terminal and check:

   ```bash
   java -version
   ```

   The output must contain `17` (e.g. `openjdk version "17.0.11"`).

### Step 2 — Install Maven

- **Windows:** download the binary zip from
  <https://maven.apache.org/download.cgi>, unzip to e.g. `C:\maven`, and add the
  inner `...\bin` folder to your `Path` user environment variable. *(Or, with a
  package manager: `winget install Apache.Maven`.)*
- **macOS:** `brew install maven` (install [Homebrew](https://brew.sh/) first if
  needed).
- **Linux:** `sudo apt install maven` (Debian/Ubuntu) or `sudo dnf install maven`
  (Fedora).

Open a **new** terminal and check:

```bash
mvn -version
```

The output must show Maven's version **and** Java 17. If it shows a different Java
version, Maven is using the wrong JDK — make sure Java 17 is the default on your
PATH.

> **How to open a terminal:** Windows → Start, type `powershell`, Enter. macOS →
> `Cmd+Space`, type `Terminal`, Enter. Linux → open your Terminal app. Always open a
> **fresh** terminal after installing something.

### Step 3 — Get the project and open a terminal in it

Unzip the project (or `git clone` it), then move your terminal into the folder that
contains `pom.xml` and this manual's parent `README.md`:

```bash
cd path/to/DaimyoSimulator
```

> On Windows you can open the folder in File Explorer, type `powershell` in the
> address bar, and press Enter to get a terminal already in that folder.

The **first** Maven command downloads all dependencies from the internet, so be
online. This happens once and may take a few minutes.

---

## 4. Running the tests and the game

All commands are run from the project folder (the one containing `pom.xml`).

### Run the automated tests

```bash
mvn clean test
```

Success looks like **`BUILD SUCCESS`** with `Failures: 0, Errors: 0`. A
**`BUILD FAILURE`** is almost always the wrong Java version (see §2) — scroll up to
the first `ERROR` line.

### Run the game (Windows / Linux)

Run these **two** commands in order:

```bash
mvn clean package
mvn -pl :desktop exec:java
```

The first compiles, tests, and packages everything (wait for `BUILD SUCCESS`); the
second opens the game window. Close the window (or press `Ctrl+C` in the terminal)
to stop.

### Run the game (macOS)

macOS requires the graphics backend to start on the program's first thread, so use
the dedicated execution instead of `exec:java`:

```bash
mvn clean package
mvn -pl :desktop exec:exec@run-mac
```

> **Why two commands, and why no `-am` on the run command?** `mvn clean package`
> installs the internal `core` and `libgdx` pieces into your local Maven cache so the
> `desktop` launcher can resolve them. Do **not** add `-am` to the `exec:java`
> command — it would try to run every module, including the top-level one that has no
> program to start, and fail with `The parameters 'mainClass' ... are missing or
> invalid`. The entry-point class is `desktop.DesktopLauncher`.

### Other useful commands

```bash
mvn -pl :desktop -am package   # build desktop app + the modules it needs
mvn -pl :core test             # test only the core (rules) module
mvn -pl :libgdx -am test       # test the graphics module (and core)
```

Maven selectors are `:core`, `:libgdx`, and `:desktop`.

### Alternative: launch from an IDE

1. Open/import the repository as a **Maven project**.
2. Set the project SDK to **Java 17**.
3. Run the class `desktop.DesktopLauncher` in the `desktop` module.

### Expected launch flow

1. `DesktopLauncher` starts the LWJGL3 desktop backend.
2. `DaimyoSimulatorGame` initializes the libGDX application.
3. `LoadingScreen` loads sprites, the programmatic UI skin, icons, and placeholders.
4. `VillageScreen` builds the world renderer, HUD stage, camera, and input handling.
5. You can start a new village or load a saved one and play.

### Save files (5 slots)

```text
%USERPROFILE%\.daimyosimulator\savegame_<slot>.json   (Windows)
~/.daimyosimulator/savegame_<slot>.json               (Linux / macOS)
```

`<slot>` is `1`–`5`; slot `1` is the default. The Save/Load dialog lets you pick a
slot and shows which slots already contain a save.

---

## 5. Main functions reused from existing libraries

Trivial utilities (logging, `java.util` collections, basic `String`/`Math` helpers)
are excluded. The substantial functionality reused from third-party libraries is:

**libGDX 1.12.1** — the game framework provides the entire real-time game loop and
rendering, none of which is implemented by hand:

- `com.badlogic.gdx.ApplicationListener` / `Game` / `Screen` — the application
  lifecycle and screen switching (`LoadingScreen`, `MainMenuScreen`, `VillageScreen`).
- `graphics.g2d.SpriteBatch` — batched 2D sprite drawing for tiles, buildings,
  forests, and overlays (`WorldRenderer` and the per-layer renderers).
- `graphics.OrthographicCamera` + `utils.viewport.Viewport` — 2D camera, pan/zoom,
  and screen↔world coordinate conversion (`CameraController`, `ScreenToGridMapper`).
- `graphics.glutils.ShapeRenderer` — the debug grid overlay (`GridOverlayRenderer`).
- `scenes.scene2d.*` (`Stage`, `Table`, `Skin`, `TextButton`, `Label`, `Dialog`,
  `Window`, `ScrollPane`, `Slider`, …) — the whole HUD and all dialogs/panels
  (`DashboardHud`, `BuildMenu`, `ResourcePanel`, `MarketDialog`, `SaveLoadDialog`,
  etc.). The skin is built programmatically by `HudSkinFactory`.
- `InputProcessor` / `InputMultiplexer` — routing mouse/keyboard input between the
  HUD and the game world (`GameInputProcessor`, `InputCommandRouter`).
- `assets.AssetManager` + `graphics.Texture` (with `TextureFilter.Nearest`) —
  asynchronous loading and management of PNG sprites (`GameAssetManager`).
- `audio.Music` and `audio.Sound` — background music and click/build/demolish sound
  effects (`GameSoundManager`).
- `utils.Disposable` — deterministic release of native GPU/audio resources.

**LWJGL3 backend (libGDX)** — `Lwjgl3Application` / `Lwjgl3ApplicationConfiguration`
provide the actual desktop window, OpenGL context, and OS integration
(`DesktopLauncher`).

**Jackson Databind 2.17.1** — JSON persistence of the whole game state:

- `ObjectMapper.writeValue(...)` / `readValue(...)` — serialize/deserialize the
  village to and from JSON save files (`VillagePersistenceService`).
- `SerializationFeature.INDENT_OUTPUT` — human-readable, pretty-printed save files.
- Data-binding to/from dedicated DTO classes (`VillageDTO` and related), keeping the
  save format decoupled from the live domain objects.

**JUnit 5 (Jupiter)** — the test framework for all core/unit and system-level tests
(`@Test`, `@BeforeEach`, `Assertions.*`, parameterized tests).

**Java SE APIs (non-trivial)** — `java.nio.file.Files` / `Path` for creating the
save directory and reading/writing save files in a cross-platform way.

---

## 6. Main external APIs used

This is an **offline desktop game: it makes no network calls and uses no remote/web
services or cloud APIs.** "External APIs" here therefore means the public programming
interfaces of the third-party libraries the game is built on:

| External API | Used for |
|---|---|
| **libGDX core API** (`com.badlogic.gdx.*`) | Game loop, 2D rendering (`SpriteBatch`, `OrthographicCamera`), Scene2D UI/HUD, input handling, asset management, audio playback |
| **libGDX LWJGL3 backend API** | Creating the desktop window and OpenGL context |
| **Jackson Databind API** (`com.fasterxml.jackson.databind.*`) | Reading/writing JSON save files |
| **JUnit 5 API** (`org.junit.jupiter.api.*`) | Authoring and running automated tests |
| **Java SE API** (`java.nio.file`, `java.io`) | File and path operations for persistence |

No API keys, accounts, or internet connection are required to play (an internet
connection is only needed the first time you build, so Maven can download the
libraries).

---

## 7. Assets

Assets are created manually by the team; the style is **2D pixelated
Japanese-inspired graphics**. Art is loaded as flat PNG sprites plus audio. The UI
skin is generated programmatically by `HudSkinFactory` (no skin JSON), and
`atlases/`/`skins/` hold only `.keep` placeholders for possible future packing.

```text
src/libgdx/main/resources/assets/
├── atlases/        (.keep)
├── skins/          (.keep)
├── audio/          (music_bg.mp3, sfx_click/build/demolish.mp3)
└── textures/sprites/
    ├── building_*.png        (building sprites, e.g. building_dwelling.png)
    ├── tile_grass.png, tile_dirt.png, feature_forest.png
    ├── icon_resource_*.png, icon_parameter_*.png, icon_policy_*.png
    ├── icon_population.png, icon_event_alert.png
    ├── button_play|pause|fast.png, overlay_valid_blue|invalid_red.png
    ├── question_icon.png, settings_icon.png, sound_icon.png
    └── missing_asset.png
```

Sprites are loaded individually by `GameAssetManager` (keys declared in
`SpriteSheetRegionRegistry`) with `TextureFilter.Nearest`. A missing sprite key
falls back to `missing_asset.png` and logs a readable warning.

---

## 8. Basic gameplay instructions

1. Start the game, then create a new village or load a saved one. A new village
   begins with free starter buildings (1 Woodcutter's Hut + 2 Dwellings).
2. Pan the map with **WASD / arrow keys** and zoom with the **mouse wheel**.
3. **Left-click** a building button to enter build mode, then **left-click** a valid
   grid cell to place it. **Right-click** or **`Escape`** cancels.
4. Use **Demolish** to remove a building (no refund), or open a **Market** cell to
   trade resources.
5. Watch the HUD for resource, population, and village-parameter changes. Resource
   numbers turn **yellow** at ≤ 30 and **red** at ≤ 10.
6. Select an existing building to inspect it in the selected-building panel.
7. Press **Next Tick** to advance time by one tick (limit: 2 builds per tick).
8. Use pause / speed controls (1x / 2x / 4x) for automatic tick playback.
9. Activate **one** strategy policy at a time from the policy panel.
10. Save or load through the menu (5 slots); adjust audio/settings from the top-bar
    icons; press **?** for the in-game tutorial and **F3** to toggle the debug grid.

The full controls reference, balance constants, and building requirements are in
[COMMANDS.md](COMMANDS.md).

---

## 9. AI tools used

AI assistants (large-language-model coding/writing tools) were used **as support
only**, including:

- refining user stories and acceptance criteria;
- proposing design diagrams and architecture alternatives;
- reviewing the separation between core logic and presentation;
- drafting and editing documentation sections (including parts of this manual);
- suggesting unit and system test coverage.

All AI-assisted output was **reviewed by the team before submission**. The team
remains fully responsible for reviewing, understanding, validating, and explaining
every part of the generated content and code.
```
