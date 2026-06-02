# DaimyoSimulator

Java 17 / Maven / libGDX rule-based simulation game set in an ancient Japanese village.

## Architecture

- `src/core` (Java packages under `core.*`): pure Java domain, simulation services, placement rules, policies, persistence (Jackson JSON), DTOs, snapshots/view models, and JUnit 5 tests. It must not import `com.badlogic.gdx.*`.
- `src/libgdx` (Java packages under `gdx.*`): rendering, input, Scene2D HUD, audio, PNG assets, and adapters from snapshots to render/UI models.
- `src/desktop` (package `desktop`): LWJGL3 desktop launcher only.

Dependency direction:

```text
desktop -> libgdx -> core
```

Maven module artifact IDs are `core`, `libgdx`, and `desktop` under the `it.unipd:daimyosimulator` parent (Java 17, libGDX 1.12.1, Jackson 2.17.1, JUnit 5.10.2).

## Build And Test

From repository root:

Test all modules:

```bash
mvn clean test
```

Package all modules:

```bash
mvn clean package
```

Package desktop and required modules only:

```bash
mvn -pl :desktop -am package
```

Run desktop launcher:

```bash
mvn clean package
mvn -pl :desktop exec:java
```

`mvn clean package` installs `core` and `libgdx` into your local Maven repository, so `exec:java` can run the `desktop` module on its own. Do **not** add `-am` here: `exec:java` is a direct goal that Maven would then run on every reactor module — including the parent `pom`, which has no `mainClass` and fails with `The parameters 'mainClass' ... are missing or invalid`.

On macOS the LWJGL3 backend must run on the first thread, so use the dedicated `run-mac` execution instead of `exec:java`:

```bash
mvn clean package
mvn -pl :desktop exec:exec@run-mac
```

The main class is `desktop.DesktopLauncher`.

Run one module test suite:

```bash
mvn -pl :core test
mvn -pl :libgdx -am test
```

`:libgdx` depends on `:core`, so it needs `-am` (also-make) unless `core` is already installed. `:core` has no upstream modules and runs on its own.

Use `:core`, `:libgdx`, and `:desktop` as Maven selectors. The old selectors `daimyosimulator-core`, `daimyosimulator-libgdx`, and `daimyosimulator-desktop` no longer exist.

## Save Files

The game uses **5 named save slots** under:

```text
%USERPROFILE%\.daimyosimulator\savegame_<slot>.json   (Windows)
~/.daimyosimulator/savegame_<slot>.json               (Linux / macOS)
```

`<slot>` is `1`–`5`; slot `1` is the default path. The Save/Load dialogs let you pick a slot and show which slots already contain a save.

The JSON save includes grid, buildings, natural features, villagers, roles, housing status, resources, parameters, tick number, policy state, cooldowns, birth/starvation progress, market cooldown, builds-this-tick counter, and event history.

## Controls & Commands

See [docs/COMMANDS.md](docs/COMMANDS.md) for the complete controls reference, balance constants, and building requirements.

Quick summary:
- **Left-click** a building button → enter build mode; **left-click** the grid to place.
- **Right-click** / `Escape` → cancel build or demolish mode.
- **Demolish** button (left panel) → click any building to remove it (no refund).
- **Market** cell → "Open Market" → exchange resources (single shared market, scales with Market count, 10-tick cooldown).
- Pan the map with **WASD / arrow keys**, zoom with the **mouse wheel**; **F3** toggles the debug grid overlay.
- **?** button → tutorial; **gear / sound** buttons → settings and audio volume.
- Resource numbers turn **yellow** when stock ≤ 30, **red** when ≤ 10.
- Building timber cost **scales** with how many of that type you already own (frozen from the 5th copy).
- Build limit: **2 buildings per tick**; resets every tick advance.

A brand-new village starts with free **starter buildings** (1 Woodcutter's Hut + 2 Dwellings).

## Notes

Runtime art is loaded from individual PNG sprites under `src/libgdx/main/resources/assets/textures/sprites`; `missing_asset.png` is shown (with a logged warning) for any missing sprite key. `docs/Textures.png` is kept only as a reference sprite sheet. AI-assisted code must be reviewed by the team before submission.
