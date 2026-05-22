---
name: project-overhaul-2026-05
description: Major UX/UI and core-balance overhaul completed May 2026 — key decisions, file map, and balance constants
metadata:
  type: project
---

## Changes made in the May 2026 overhaul

**Why:** Full UX/UI + core-feature pass requested by Antonio.

**How to apply:** Reference when adding future features to avoid duplicating or conflating these.

### New files
- `EventReport.java` (core/event) – structured random-event record (name, explanation, consequence)
- `EventModal.java` (gdx/ui) – modal pop-up for random events
- `WarningPanel.java` (gdx/ui) – right-side resource trend tracker (3-tick sliding window)
- `SaveLoadDialog.java` (gdx/ui) – 5-slot save/load chooser
- `CommandsDialog.java` (gdx/ui) – in-game command reference
- `ForestBorderRenderer.java` (gdx/render) – dense forest ring around 20×20 grid using feature_forest sprite

### Key balance constants (all in `GameConfig.defaults()`)
- `ricePerVillagerPerTick = 2`  (food pressure)
- `birthRate = 15`, `birthRiceCost = 40`  (slow pop growth)
- `birthFoodThreshold = 70`, `birthHousingThreshold = 60`, `birthHappinessThreshold = 60`

### Architecture notes
- `TickResult` now carries `randomEventReports: List<EventReport>` alongside `randomEvents: List<String>`
- `RandomEventManager.evaluate()` still returns `List<String>` for backward compat with tests; `evaluateFull()` returns `List<EventReport>`
- `VillageScreen` now accepts an optional `CoreGameFacade` constructor arg for Load-from-main-menu
- Multi-slot saves: `CoreGameFacade.slotPath(int)` → `~/.daimyosimulator/savegame_N.json`
- `HudSkinFactory` generates NinePatch rounded drawables via `Pixmap`
- `ForestBorderRenderer` uses a deterministic hash to vary tree scale/position without extra assets
- `SpriteSheetRegionRegistry`: tiles now use `transparentEdges=true`; background detection broadened to catch mid-gray checkerboard (min≥155 range)
