# DaimyoSimulator – Controls & Commands

## Mouse

| Action | Result |
|--------|--------|
| Left-click building button (left panel) | Enter build mode for that building |
| Left-click grid tile (build mode) | Place the selected building (costs timber) |
| Left-click grid tile (normal mode) | Inspect cell; shows info in bottom panel |
| Left-click Market cell → "Open Market" | Open resource trade dialog |
| Right-click anywhere | Cancel current build or demolish mode |

## Keyboard

| Key | Result |
|-----|--------|
| `Escape` | Cancel current build or demolish mode |
| `F3` | Toggle debug grid overlay |

## HUD Buttons (top bar)

| Button | Result |
|--------|--------|
| **New** | Start a fresh village (unsaved progress lost) |
| **Save** | Save to `~/.daimyosimulator/savegame.json` |
| **Load** | Load from `~/.daimyosimulator/savegame.json` |
| **Next** | Advance one tick manually |
| **Pause / Running** | Toggle auto-tick |
| **1× / 2× / 4×** | Cycle auto-tick speed |
| **?** | Open tutorial / help dialog |

## Left Panel (building menu)

| Element | Meaning |
|---------|---------|
| `Builds: x/y` header | Buildings placed / allowed this tick |
| `[n]` next to each building | How many of that type exist on the map |
| Timber cost `(n■)` | Timber required to place that building |
| **Demolish** button | Enter demolish mode – click a building to remove it (no refund) |
| Hover tooltip | Cost, production, requirements for that building |

## Bottom Panel

| Panel | Content |
|-------|---------|
| Policy | Active policy name, remaining ticks, cooldowns |
| Parameters | Happiness, Protection, Food, Faith, Housing, Craftsmanship (0–100) |
| Selection | Selected cell position, building name, job slots |
| Event log | Last 8 game messages (events, tick info, errors) |

## Resource Bar (top)

| Column | Meaning |
|--------|---------|
| Icon | Resource type (hover for tooltip) |
| White number | Current stock |
| Yellow number | Stock is low (≤ 30) |
| Red number | Stock is critical (≤ 10) |
| `±n` / `+n` / `-n` | Net delta after last tick (green=gain, red=loss) |

## Building Placement Rules

| Building | Special requirement |
|----------|---------------------|
| Woodcutter's Hut | Must be adjacent (distance 1) to a Forest tile |
| Smithy | Mine must exist anywhere on the map |
| Workshop | Mine must exist anywhere on the map |

## Balance Constants (GameConfig.defaults)

| Parameter | Value | Notes |
|-----------|-------|-------|
| `maxBuildsPerTick` | 2 | Builds allowed per tick |
| `ricePerVillagerPerTick` | 2 | Food consumed per villager/tick |
| `birthFoodThreshold` | 70 | Food parameter needed for birth |
| `birthRate` | 15 | Birth progress per eligible tick |
| `birthRiceCost` | 40 | Rice consumed per birth |
| `starvationDeathIntervalTicks` | 3 | Ticks between starvation deaths |
| `policyDurationTicks` | 5 | How long a policy lasts |
| `policyCooldownTicks` | 8 | Cooldown after a policy expires |
| `tradeExchangeRate` | 2 | Give 2 → receive 1 |
| `forestDensity` | 0.10 | ~10 % of cells start as forest |

## Save File Location

```
~/.daimyosimulator/savegame.json       (Linux / macOS)
%USERPROFILE%\.daimyosimulator\savegame.json  (Windows)
```
