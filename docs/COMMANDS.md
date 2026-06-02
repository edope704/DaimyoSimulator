# DaimyoSimulator – Controls & Commands

## Mouse

| Action | Result |
|--------|--------|
| Left-click building button (left panel) | Enter build mode for that building |
| Left-click grid tile (build mode) | Place the selected building (costs timber) |
| Left-click grid tile (normal mode) | Inspect cell; shows info in the selection panel |
| Left-click a Market cell → "Open Market" | Open the resource trade dialog |
| Left-click a building (demolish mode) | Remove that building (no refund) |
| Right-click anywhere | Cancel current build or demolish mode |
| Mouse wheel | Zoom the camera in/out (clamped) |

## Keyboard

| Key | Result |
|-----|--------|
| `W` `A` `S` `D` / arrow keys | Pan the camera |
| `Escape` | Cancel current build or demolish mode |
| `F3` | Toggle debug grid overlay |

## HUD Buttons

| Button | Location | Result |
|--------|----------|--------|
| **New** | Menu overlay | Start a fresh village (unsaved progress lost) |
| **Save** | Menu overlay | Open the save-slot dialog |
| **Load** | Menu overlay | Open the load-slot dialog |
| **Next** | Speed controls | Advance one tick manually |
| **Pause / Resume** | Speed controls | Toggle auto-tick |
| **1x / 2x / 4x** | Speed controls | Cycle auto-tick speed |
| Gear (settings) icon | Top bar | Open Settings (New Game / Save Game / Load Game / Commands) |
| Sound icon | Top bar | Open audio settings (music & SFX volume) |
| **?** (question) icon | Top bar | Open the tutorial dialog |

## Left Panel (build menu)

| Element | Meaning |
|---------|---------|
| `Builds: x/y` header | Buildings placed / allowed this tick |
| `[n]` next to each building | How many of that type exist on the map |
| Timber cost `(n■)` | Timber required to place the **next** copy (scales with how many you own) |
| **Demolish** button | Enter demolish mode – click a building to remove it (no refund) |
| Hover tooltip | Cost, production, requirements for that building |

## Selection / Dashboard Panels

| Panel | Content |
|-------|---------|
| Policy | Active policy name, remaining ticks, cooldowns |
| Parameters | Happiness, Protection, Food, Faith, Housing, Craftsmanship (0–100) |
| Population | Total, idle, unhoused, employed villagers and per-role counts |
| Selection | Selected cell position, building name, job slots, "Open Market" on a Market |
| Event log | Recent game messages (events, tick info, errors); keeps the last 10 |

## Resource Bar (top)

| Column | Meaning |
|--------|---------|
| Icon | Resource type (hover for tooltip) |
| White number | Current stock |
| Yellow number | Stock is low (≤ 30) |
| Red number | Stock is critical (≤ 10) |
| `±n` / `+n` / `-n` | Net delta after last tick (green = gain, red = loss) |

## Building Costs and Job Slots

Timber cost **scales** with how many of that type already exist:
`cost = baseCost + min(existingCount, 4) × delta`, where `delta` is **+10** for Temple, Market, Guard Post, Smithy, Mine, and Workshop, and **+5** for every other type. The price is frozen at the 5th-copy value from the 5th copy onward.

| Building | Base timber | Housing | Job slots |
|----------|-------------|---------|-----------|
| Dwelling | 15 | 4 | – |
| Rice Paddy | 8 | – | – (produces rice) |
| Rice Farm | 18 | – | Rice Farmer ×3 |
| Woodcutter's Hut | 20 | – | Woodcutter ×3 |
| Mine | 25 | – | – |
| Market | 25 | – | Trader ×2 |
| Guard Post | 25 | – | Samurai ×2 |
| Smithy | 30 | – | Blacksmith ×2 |
| Temple | 30 | – | Monk ×2 |
| Workshop | 35 | – | Artisan ×2 |

A brand-new village is seeded with free starter buildings (1 Woodcutter's Hut adjacent to a forest + 2 Dwellings), placed without cost or build-limit checks.

## Placement vs. Production Rules

Placement is validated by: cell inside grid, cell empty, and enough timber, plus any building-specific rule. **Mine adjacency for Smithy/Workshop is a production requirement, not a placement rule** — they can be built anywhere but only produce when a Mine is nearby.

| Building | Placement requirement | Production requirement |
|----------|-----------------------|------------------------|
| Woodcutter's Hut | Must be within range 1 of a Forest tile | Operates near a forest **or** the outer tree border, with a Woodcutter assigned |
| Rice Paddy | None | A Rice Farm within range **and** at least one Rice Farmer; halved output when Tools are at 0 |
| Smithy | None | A Mine within range **and** at least one Blacksmith |
| Workshop | None | A Mine within range, at least one Artisan, on workshop-production ticks |

(`adjacencyRange` defaults to **1**.)

## Resource Trade (Market)

Trading is a single shared market (any Market cell opens the same dialog), not one market per resource.

- A Market must exist; after any trade the market is locked for **10 ticks**.
- Trade volume capacity = `MarketCount × 10` units.
- Exchange rates are asymmetric (give cheap → get little, give valuable → get more):

| from \ to | Rice | Timber | Tools | Luxury |
|-----------|------|--------|-------|--------|
| Rice | – | ÷5 | ÷15 | ÷30 |
| Timber | ×5 | – | ÷10 | ÷30 |
| Tools | ×15 | ×10 | – | ÷20 |
| Luxury | ×30 | ×30 | ×20 | – |

(Every successful trade yields at least 1 unit.)

## Strategy Policies

| Policy | Production effect | Consumption / value effect |
|--------|-------------------|----------------------------|
| Agricultural Expansion | Rice Paddy rice ×1.5 | Rice Farmer Tools consumption ×1.5 |
| Military Protection | Samurai protection value ×1.5 | Samurai Tools & Luxury consumption ×1.5 |
| Craftsmen Production | Timber, Tools, Luxury production ×1.5 | Blacksmith & Artisan Rice consumption ×1.5 |

One policy at a time; lasts `policyDurationTicks` then enters `policyCooldownTicks`.

## Balance Constants (`GameConfig.defaults()`)

| Parameter | Value | Notes |
|-----------|-------|-------|
| `gridWidth` × `gridHeight` | 20 × 20 | Logical grid size |
| `initialRice` / `initialTimber` | 100 / 100 | Starting stock |
| `initialTools` / `initialLuxuryGoods` | 20 / 10 | Starting stock |
| `initialVillagers` | 8 | Starting population |
| `forestDensity` | 0.10 | ~10 % of cells start as forest |
| `adjacencyRange` | 1 | Range for placement/production proximity |
| `jobAssignmentIntervalTicks` | 1 | Ticks between job-assignment passes |
| `ricePerVillagerPerTick` | 2 | Food consumed per villager/tick |
| `birthFoodThreshold` | 70 | Food parameter needed for birth |
| `birthHousingThreshold` | 60 | Housing parameter needed for birth |
| `birthHappinessThreshold` | 60 | Happiness parameter needed for birth |
| `birthRate` | 25 | Birth progress per eligible tick |
| `birthRiceCost` | 40 | Rice consumed per birth |
| `starvationDeathIntervalTicks` | 3 | Ticks between starvation deaths |
| `policyDurationTicks` | 5 | How long a policy lasts |
| `policyCooldownTicks` | 8 | Cooldown after a policy expires |
| `workshopProductionIntervalTicks` | 1 | Workshop produces every N ticks |
| `randomEventsEnabled` | true | Toggle random events |
| `tradeExchangeRate` | 2 | Legacy field (live rates use the table above) |
| `baseTradeCapacity` / `traderCapacityBonus` | 10 / 10 | Legacy trade-capacity fields |
| `maxBuildsPerTick` | 2 | Builds allowed per tick |

## Save File Location

The game uses 5 named slots; slot 1 is the default.

```
~/.daimyosimulator/savegame_<slot>.json            (Linux / macOS)
%USERPROFILE%\.daimyosimulator\savegame_<slot>.json (Windows)
```
