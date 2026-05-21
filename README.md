# DaimyoSimulator

Java 17 / Maven / libGDX rule-based simulation game set in an ancient Japanese village.

## Architecture

- `daimyosimulator-core`: pure Java domain, simulation services, placement rules, policies, persistence, DTOs, snapshots, and JUnit 5 tests. It must not import `com.badlogic.gdx.*`.
- `daimyosimulator-libgdx`: rendering, input, Scene2D HUD, generated placeholder assets, and adapters from snapshots to render/UI models.
- `daimyosimulator-desktop`: LWJGL3 desktop launcher only.

Dependency direction:

```text
daimyosimulator-desktop -> daimyosimulator-libgdx -> daimyosimulator-core
```

## Build And Test

From repository root:

```bash
mvn clean test
mvn clean package
mvn -pl daimyosimulator-desktop -am package
```

Run desktop launcher:

```bash
mvn clean package
mvn -pl daimyosimulator-desktop -am exec:java
```

`package` also installs the core and libGDX module artifacts locally so the later direct `exec:java` invocation can resolve reactor module dependencies.

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
