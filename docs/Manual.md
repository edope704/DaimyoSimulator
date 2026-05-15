# DaimyoSimulator — Project Manual

## 1. High-level project description

DaimyoSimulator is a Java rule-based simulation engine inspired by SimCity Lite and set in an ancient Japanese village around year 1200. The player develops a village by placing buildings, managing resources, activating one strategy policy at a time, and advancing time through simulation ticks.

The project is organized around three responsibilities:

1. **Core Game Logic — pure Java**: village simulation, logical grid, buildings and placement rules, villagers and jobs, economy/resources, strategy policies, random events, save/load, and tick engine.
2. **Game World Renderer — libGDX**: visual rendering of the village map, tiles, buildings, forests, and animations using `SpriteBatch` and `OrthographicCamera`.
3. **Game UI / HUD — libGDX Scene2D UI**: build buttons, resources, population, village parameters, selected building panel, pause/speed/next-tick controls, policy buttons, menus, dashboard, and event/status log.

The core module must never import libGDX. The renderer and UI communicate with the core only through controllers, facades, DTOs, immutable snapshots, or view models.

---

## 2. Runtime and development requirements

| Requirement | Choice |
|---|---|
| Programming language | Java |
| Recommended Java version | Java 17 LTS |
| Build tool | Maven |
| Core testing | JUnit 5 |
| Graphics/UI framework | libGDX |
| Desktop backend | libGDX LWJGL3 backend |
| Persistence format | JSON |
| IDE | IntelliJ IDEA, Eclipse, or Visual Studio Code with Java/Maven support |

---

## 3. Maven project structure

```text
daimyosimulator/
├── pom.xml
├── daimyosimulator-core/
├── daimyosimulator-libgdx/
└── daimyosimulator-desktop/
```

Dependency direction:

```text
daimyosimulator-desktop
        ↓
daimyosimulator-libgdx
        ↓
daimyosimulator-core
```

Rules:

- `daimyosimulator-core` contains the pure Java domain model, simulation engine, persistence, application services, DTOs, snapshots, and JUnit tests.
- `daimyosimulator-libgdx` contains screens, renderer, HUD, input handling, asset loading, and adapters.
- `daimyosimulator-desktop` contains only the desktop launcher and libGDX desktop backend configuration.
- `daimyosimulator-core` must not import `com.badlogic.gdx.*`.

---

## 4. Main Maven commands

Run all tests:

```bash
mvn clean test
```

Build all modules:

```bash
mvn clean package
```

Build the desktop app and required modules:

```bash
mvn -pl daimyosimulator-desktop -am package
```

Run the desktop launcher if the Maven exec plugin is configured:

```bash
mvn -pl daimyosimulator-desktop -am exec:java
```

Alternative launch from IDE:

1. Import the repository as a Maven project.
2. Set the project SDK to Java 17.
3. Open `daimyosimulator-desktop`.
4. Run `it.unipd.daimyosimulator.desktop.DesktopLauncher`.

---

## 5. Installation instructions

1. Install Java 17 LTS.
2. Install Maven.
3. Clone the GitHub repository.
4. Open a terminal in the repository root.
5. Run:

```bash
mvn clean test
```

6. Confirm that the core JUnit tests pass.
7. Run the desktop application using the configured Maven launch command or the IDE launcher.

---

## 6. Launch instructions

Expected launch flow:

1. `DesktopLauncher` starts the LWJGL3 desktop backend.
2. `DaimyoSimulatorGame` initializes the libGDX application.
3. `LoadingScreen` loads texture atlases, UI skin files, icons, and placeholders.
4. `VillageScreen` creates the world renderer, HUD stage, camera, and input multiplexer.
5. The user can start or load a village and interact with it through the libGDX UI.

---

## 7. External libraries and APIs used

| Library / API | Purpose |
|---|---|
| Java Standard Library | Collections, paths, file I/O, core language features |
| Maven | Build lifecycle, dependency management, test execution |
| JUnit 5 | Unit tests for core classes and services |
| libGDX core | Game framework, rendering, input, asset management |
| libGDX LWJGL3 backend | Desktop execution |
| libGDX Scene2D UI | HUD, buttons, panels, menus, dashboard |
| Jackson or Gson | JSON save/load persistence, if selected during implementation |

No database is required. Persistence is file-based using JSON.

---

## 8. Asset folder description

Assets are created manually by the project team. The expected visual style is **2D pixelated Japanese-inspired graphics**.

Recommended asset folder:

```text
daimyosimulator-libgdx/src/main/resources/assets/
├── atlases/
│   ├── village.atlas
│   └── ui.atlas
├── textures/
│   ├── tiles/
│   ├── buildings/
│   ├── features/
│   ├── icons/
│   └── placeholders/
├── skins/
│   ├── daimyo-ui.json
│   └── daimyo-ui.atlas
└── mapping/
    └── sprite-map.json
```

Naming examples:

```text
tile_grass.png
feature_forest.png
building_dwelling.png
building_rice_farm.png
building_guard_post.png
icon_resource_rice.png
icon_policy_agricultural_expansion.png
missing_asset.png
```

During early development, individual PNG files can be loaded directly. Before final delivery, stable sprites should be packed into a `TextureAtlas`.

Missing assets should use `missing_asset.png` and log a readable warning.

---

## 9. Basic user instructions

1. Start the game.
2. Create a new village or load a saved one.
3. Use the camera controls to pan and zoom the village map.
4. Select a building from the build menu.
5. Click a valid cell to request placement.
6. Watch the HUD for resource, population, and village parameter changes.
7. Select existing buildings to inspect them in the selected building panel.
8. Press **Next Tick** to advance time by one tick.
9. Use pause/speed controls only for automatic tick playback.
10. Activate one strategy policy at a time from the policy panel.
11. Save or load the village through the menu.

---

## 10. AI tools used

AI tools were used as support for:

- refining user stories and acceptance criteria;
- proposing design diagrams and architecture alternatives;
- reviewing separation between core logic and presentation;
- drafting documentation sections;
- suggesting unit/system test coverage.

The team remains responsible for reviewing, understanding, validating, and explaining all generated content.
