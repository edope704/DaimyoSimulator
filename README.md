# DaimyoSimulator

Java 17 / Maven / libGDX rule-based simulation game set in an ancient Japanese village.

## Architecture

- `src/core`: pure Java domain, simulation services, placement rules, policies, persistence, DTOs, snapshots, and JUnit 5 tests. It must not import `com.badlogic.gdx.*`.
- `src/libgdx`: rendering, input, Scene2D HUD, generated placeholder assets, and adapters from snapshots to render/UI models.
- `src/desktop`: LWJGL3 desktop launcher only.

Dependency direction:

```text
desktop -> libgdx -> core
```

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

Run one module test suite:

```bash
mvn -pl :core test
mvn -pl :libgdx -am test
```

`:libgdx` depends on `:core`, so it needs `-am` (also-make) unless `core` is already installed. `:core` has no upstream modules and runs on its own.

Use `:core`, `:libgdx`, and `:desktop` as Maven selectors. The old selectors `daimyosimulator-core`, `daimyosimulator-libgdx`, and `daimyosimulator-desktop` no longer exist.

## Save Files

Default UI save/load path:

```text
%USERPROFILE%\.daimyosimulator\savegame.json
```

The JSON save includes grid, buildings, natural features, villagers, roles, housing status, resources, parameters, tick number, policy state, cooldowns, birth progress, starvation timer, and event history.

## Controls & Commands

See [docs/COMMANDS.md](docs/COMMANDS.md) for the complete controls reference, balance constants, and building requirements.

Quick summary:
- **Left-click** a building button → enter build mode; **left-click** the grid to place.
- **Right-click** / `Escape` → cancel build or demolish mode.
- **Demolish** button (left panel) → click any building to remove it (no refund).
- **?** button (top bar) → opens in-game tutorial.
- Resource numbers turn **yellow** when stock ≤ 30, **red** when ≤ 10.
- Build limit: **2 buildings per tick**; resets every tick advance.

## Notes

Generated placeholder textures are used when individual PNG sprites are missing. The provided sprite sheet is included as a reference asset. AI-assisted code must be reviewed by the team before submission.
