# DaimyoSimulator

DaimyoSimulator is a Java rule-based village management game set in an ancient
Japanese village. The player grows a settlement by placing buildings, managing
rice, timber, tools, and luxury goods, activating short-lived strategy policies,
and advancing the simulation through discrete ticks.

The project is built as a desktop libGDX game with a pure Java simulation core.
The core owns the village rules and is testable without graphics; the libGDX
layer renders snapshots, collects mouse/keyboard/HUD input, and sends commands
through `CoreGameFacade`.

## Project At A Glance

- Build and manage a 20 x 20 village grid with forests, dwellings, farms,
  paddies, workshops, markets, temples, guard posts, mines, smithies, and
  woodcutter huts.
- Balance four resources: Rice, Timber, Tools, and Luxury Goods.
- Track six village parameters: Happiness, Protection, Food, Faith, Housing, and
  Craftsmanship.
- Let villagers move into housing and take jobs automatically as buildings open
  job slots.
- Advance time by ticks; each tick processes policies, jobs, production,
  consumption, shortages, population changes, random events, and dashboard
  updates.
- Save and load five named village slots as JSON files.

## Gameplay Loop

1. Start a new village or load a save.
2. Place buildings on valid tiles while watching timber cost and build limits.
3. Advance ticks manually or with automatic playback.
4. Read the HUD: resources, population, parameters, active policy, event log, and
   selected cell details.
5. Trade through Markets, activate one policy at a time, and adapt to shortages
   and random events.

New villages start with free starter buildings: 1 Woodcutter's Hut and 2
Dwellings. Starter placement bypasses timber cost and the per-tick build limit.

## Run Locally

Required tools:

- Java 17 JDK
- Maven 3.8+ or 3.9+

From the repository root:

```bash
mvn clean package
mvn -pl :desktop exec:java
```

macOS uses the dedicated first-thread launcher:

```bash
mvn clean package
mvn -pl :desktop exec:exec@run-mac
```

Useful developer commands:

```bash
mvn clean test            # test every module
mvn clean package         # compile, test, and package every module
mvn -pl :core test        # test only the core module
mvn -pl :libgdx -am test  # test libgdx and required modules
```

Maven module selectors are `:core`, `:libgdx`, and `:desktop`.

## Architecture

The project has three Maven modules:

| Module | Responsibility |
|--------|----------------|
| `src/core` | Pure Java domain logic: village state, grid, buildings, resources, policies, simulation ticks, persistence, snapshots, and tests. It must not import `com.badlogic.gdx.*`. |
| `src/libgdx` | Rendering, input, Scene2D HUD, dialogs, audio, PNG assets, and adapters from core snapshots to UI/render models. |
| `src/desktop` | LWJGL3 desktop launcher. Entry point: `desktop.DesktopLauncher`. |

Dependency direction:

```text
desktop -> libgdx -> core
```

Main boundary:

```text
libGDX UI/input -> CoreGameFacade -> GameController -> simulation services
```

The presentation layer receives immutable snapshots and view models, so game
rules stay inside the core module.

## Game Commands

There is no text console. Commands come from libGDX mouse input, keyboard input,
and HUD actions, then call `CoreGameFacade`.

### Command Surface

| Command | How to run it | Current behavior |
|---------|---------------|------------------|
| Start new village | Main menu `New Game`, or gear icon -> `New Game` | Creates a 20 x 20 village and places starter buildings: 1 Woodcutter's Hut and 2 Dwellings. Starter placement is free and bypasses the per-tick build limit. |
| Save village | Gear icon -> `Save Game` -> slot 1-5 | Saves the current village into the selected slot. |
| Load village | Main menu `Load Game`, or gear icon -> `Load Game` -> slot 1-5 | Loads the selected non-empty slot. Empty load slots are disabled in the dialog. |
| Inspect cell | Left-click a playable grid tile while not in build or demolish mode | Shows tile coordinates, feature/building name, job slots, and activity state in the selection panel. A Market tile also shows `Open Market`. |
| Construct building | Click a building button, then left-click a valid grid tile | Places the selected building, consumes timber, increments this tick's build count, and leaves build mode on success. |
| Cancel mode | Right-click, or press `Escape` | Cancels current build or demolish mode. |
| Demolish/clear tile | Click `Demolish`, then left-click a building or forest tile | Removes a building for +5 timber, or clears a forest for +10 timber. Workers from the removed building become idle; housing is recalculated. |
| Advance tick | Click `Next` in the speed panel | Advances one simulation tick immediately and resets the build limit. |
| Auto tick playback | `Pause` / `Resume`, plus `1x` / `2x` / `4x` speed button | Toggles automatic tick advancement and cycles playback speed. |
| Activate policy | Click `Agriculture`, `Military`, or `Craftsmen` in the policy panel | Activates one policy if none is active and that policy is not on cooldown. |
| Trade resources | Inspect a Market tile -> `Open Market` -> choose give/receive resources and amount -> `Execute Trade` | Executes one shared market trade if a Market exists, the cooldown is zero, capacity is enough, and stock is enough. |
| Open settings | Gear icon | Opens `New Game`, `Save Game`, `Load Game`, and `Commands`. |
| Open audio settings | Sound icon | Opens music and SFX volume controls. |
| Open tutorial | `?` icon | Opens the in-game tutorial dialog. |
| Open commands dialog | Gear icon -> `Commands` | Opens the in-game commands and controls dialog. |
| Toggle debug grid | `F3` | Toggles the debug grid overlay. |

### Mouse

| Input | Result |
|-------|--------|
| Left-click building button | Enter build mode for that building type. |
| Left-click playable grid tile in build mode | Attempt to place the selected building. |
| Left-click playable grid tile in normal mode | Inspect that tile. |
| Left-click Market tile, then `Open Market` | Open the resource trade dialog. |
| Left-click building or forest in demolish mode | Remove the building or clear the forest. |
| Right-click anywhere | Cancel build or demolish mode. |
| Mouse wheel | Zoom camera in or out, clamped to valid zoom range. |

No middle-mouse drag command is implemented.

### Keyboard

| Key | Result |
|-----|--------|
| `W`, `A`, `S`, `D` | Pan camera. |
| Arrow keys | Pan camera. |
| `Escape` | Cancel build or demolish mode. |
| `F3` | Toggle debug grid overlay. |

### HUD Buttons

| Button | Location | Result |
|--------|----------|--------|
| `Next` | Speed panel | Advance one tick manually. |
| `Pause` / `Resume` | Speed panel | Stop or restart automatic tick playback. |
| `1x` / `2x` / `4x` | Speed panel | Cycle automatic tick speed. |
| Gear icon | Top bar | Open Settings. |
| Sound icon | Top bar | Open audio settings. |
| `?` icon | Top bar | Open tutorial. |
| `Demolish` | Build panel | Enter demolish mode. |
| `Agriculture` | Policy panel | Activate Agricultural Expansion. |
| `Military` | Policy panel | Activate Military Protection. |
| `Craftsmen` | Policy panel | Activate Craftsmen Production. |
| `Open Market` | Selection panel, only for Market tiles | Open market trade dialog. |
| `Execute Trade` | Market trade dialog | Submit selected trade. |

## Building Commands

Common validation for all construction:

- Position must be inside the playable grid.
- Target cell must be empty. Forest cells are occupied until cleared.
- Current timber stock must cover the scaled timber cost.
- Build count must be below `maxBuildsPerTick` for the current tick.

Default build limit: 2 buildings per tick.

| Build button | Building type | Base timber | Housing | Job slots | Extra placement rule |
|--------------|---------------|-------------|---------|-----------|----------------------|
| `Dwelling` | Dwelling | 15 | 4 | None | None |
| `Farm` | Rice Farm | 18 | None | Rice Farmer x3 | None |
| `Paddy` | Rice Paddy | 8 | None | None | None |
| `Woodcutter` | Woodcutter's Hut | 20 | None | Woodcutter x3 | Must be near a forest tile or the outer tree border. |
| `Mine` | Mine | 25 | None | None | None |
| `Smithy` | Smithy | 30 | None | Blacksmith x2 | None |
| `Workshop` | Workshop | 35 | None | Artisan x2 | None |
| `Market` | Market | 25 | None | Trader x2 | None |
| `Guard` | Guard Post | 25 | None | Samurai x2 | None |
| `Temple` | Temple | 30 | None | Monk x2 | None |

Timber cost scales with existing buildings of the same type:

```text
cost = baseCost + min(existingCount, 4) * delta
```

`delta` is +10 for Temple, Market, Guard Post, Smithy, Mine, and Workshop.
`delta` is +5 for Dwelling, Rice Farm, Rice Paddy, and Woodcutter's Hut. Cost
stops increasing after the fifth copy price.

Production requirements are separate from construction requirements. A building
can be placed even when its production requirement is missing, except
Woodcutter's Hut.

| Building | Production requirement |
|----------|------------------------|
| Woodcutter's Hut | Near a playable forest tile or the outer tree border, with at least one Woodcutter assigned. |
| Rice Paddy | Near a Rice Farm, with at least one Rice Farmer assigned. Output is 5 rice per eligible paddy, or 2 rice when Tools stock is 0. |
| Smithy | At least one Smithy near a Mine, with at least one Blacksmith assigned. |
| Workshop | At least one Workshop near a Mine, with at least one Artisan assigned, on workshop production ticks. |

Default adjacency range: 1 tile.

## Market Trades

Trading is one shared market system. Any placed Market tile opens the same trade
dialog.

- Requires at least one Market building.
- Capacity is `Market count * 10` units per trade.
- Amount must be a positive whole number and cannot exceed capacity.
- Source and target resources must differ.
- Source stock must cover the given amount.
- After a successful trade, all Market trades are locked for 10 ticks.
- Integer division is used for unfavorable rates, but every successful trade
  yields at least 1 unit.

| Give / receive | Rice | Timber | Tools | Luxury |
|----------------|------|--------|-------|--------|
| Rice | Not allowed | amount / 5 | amount / 15 | amount / 30 |
| Timber | amount * 5 | Not allowed | amount / 10 | amount / 30 |
| Tools | amount * 15 | amount * 10 | Not allowed | amount / 20 |
| Luxury | amount * 30 | amount * 30 | amount * 20 | Not allowed |

## Policies

One policy can be active at a time. Each policy lasts 5 ticks, then enters an
8-tick cooldown.

| Button | Policy | Effect | Extra consumption while active |
|--------|--------|--------|--------------------------------|
| `Agriculture` | Agricultural Expansion | Rice Paddy production x1.5 | Rice Farmer Tools consumption x1.5 |
| `Military` | Military Protection | Protection value x1.5 | Samurai Tools and Luxury consumption x1.5 |
| `Craftsmen` | Craftsmen Production | Timber, Tools, and Luxury production x1.5 | Blacksmith and Artisan Rice consumption x1.5 |

## Panels And Indicators

| Panel / indicator | Meaning |
|-------------------|---------|
| `Builds: x/y` | Buildings placed this tick / max buildings allowed this tick. |
| `[n]` next to each building button | Current count of that building type on the map. |
| Cost number next to each building button | Scaled timber cost for the next copy. |
| Resource bar white number | Current stock. |
| Resource bar yellow number | Stock is low (`<= 30`). |
| Resource bar red number | Stock is critical (`<= 10`). |
| Resource delta | Net resource gain/loss after the last tick. |
| Selection panel | Selected cell position, feature/building name, job slots, and Market action when applicable. |
| Policy panel | Active policy and remaining ticks. |
| Event log | Recent messages, including tick status, events, and errors. |

## Default Balance Values

| Value | Default |
|-------|---------|
| Grid size | 20 x 20 |
| Initial Rice / Timber | 100 / 100 |
| Initial Tools / Luxury | 20 / 10 |
| Initial villagers | 8 |
| Forest density | 0.10 |
| Adjacency range | 1 |
| Job assignment interval | 1 tick |
| Rice consumed per villager per tick | 2 |
| Birth Food / Housing / Happiness thresholds | 70 / 60 / 60 |
| Birth rate | 25 progress per eligible tick |
| Birth rice cost | 40 |
| Starvation death interval | 3 ticks |
| Policy duration / cooldown | 5 / 8 ticks |
| Workshop production interval | 1 tick |
| Random events enabled | true |
| Max builds per tick | 2 |

`tradeExchangeRate`, `baseTradeCapacity`, and `traderCapacityBonus` still exist in
`GameConfig`, but live market logic uses the rate table and `Market count * 10`
capacity described above.

## Save Files

The game has 5 save slots. Slot 1 is the default path used by
`CoreGameFacade.defaultSavePath()`.

```text
%USERPROFILE%\.daimyosimulator\savegame_<slot>.json   Windows
~/.daimyosimulator/savegame_<slot>.json               Linux / macOS
```

Each save stores the full village state: grid, buildings, natural features,
villagers, roles, housing, resources, parameters, tick number, policy state,
cooldowns, birth/starvation progress, market cooldown, builds-this-tick counter,
and event history.

## Documentation

- [docs/Manual.md](docs/Manual.md): full setup guide, project requirements,
  libraries, assets, and gameplay instructions.
- [docs/DesignDocument.md](docs/DesignDocument.md): domain model, architecture,
  diagrams, and internal sequences.
- [docs/UserStories.md](docs/UserStories.md): Jira-ready user stories and
  acceptance criteria.
- [docs/SystemTest.md](docs/SystemTest.md): system test plan.

Runtime art is loaded from individual PNG sprites under
`src/libgdx/main/resources/assets/textures/sprites`. `missing_asset.png` is shown
with a logged warning for any missing sprite key.

AI-assisted code and documentation must be reviewed by the team before submission.
