# DaimyoSimulator - System Test Document (Jira)

## Scope

This document reports the validation result for each acceptance criterion in
`docs/UserStories.md`. To keep the Jira document compact, the full Given/When/Then
text remains in `UserStories.md`; this matrix uses the same AC identifiers.

## Method

| Field | Value |
|---|---|
| Validation date | 2026-06-02 |
| Tester | Static review by Codex |
| Evidence | Source code, UI code, docs, and available JUnit test classes |
| Not executed | Build, automated tests, manual UI run, flashing |
| Reason not executed | Project instruction: do not run builds, tests, flashing, or long commands |

Legend: `OK` means the criterion matches the implementation/evidence found.
`KO` means the criterion is missing or differs from the user-story requirement.

## Summary

| Total AC | OK | KO |
|---:|---:|---:|
| 113 | 103 | 10 |

## Validation Matrix

| AC | Result | Date | Comments |
|---|---|---|---|
| AC-01.1 | OK | 2026-06-02 | `VillageInitializer` creates a configurable grid via `GameConfig`. |
| AC-01.2 | OK | 2026-06-02 | `Cell` and `Position` expose coordinates, building, and natural feature state. |
| AC-01.3 | OK | 2026-06-02 | Initial resources covered by `InitialResourceStockTest`. |
| AC-01.4 | OK | 2026-06-02 | Parameters initialized and recalculated by `VillageParameterCalculator`. |
| AC-01.5 | OK | 2026-06-02 | Forest generation covered by `ForestGenerationTest`. |
| AC-01.6 | OK | 2026-06-02 | Grid/position validation rejects invalid coordinates without state mutation. |
| AC-02.1 | OK | 2026-06-02 | `ConstructionService` places buildings on valid empty cells. |
| AC-02.2 | OK | 2026-06-02 | Timber consumption checked in construction tests. |
| AC-02.3 | OK | 2026-06-02 | Occupied cells rejected by placement validation. |
| AC-02.4 | OK | 2026-06-02 | Insufficient timber rejection covered by `InsufficientTimberTest`. |
| AC-02.5 | OK | 2026-06-02 | `BuildingType`, factory, and build UI expose all required buildings. |
| AC-02.6 | OK | 2026-06-02 | Building creation uses `BuildingFactory`, not engine hardcoding. |
| AC-03.1 | OK | 2026-06-02 | Rice Paddy can exist, but production requires nearby Rice Farm by chosen design. |
| AC-03.2 | OK | 2026-06-02 | Nearby Rice Farm satisfies Rice Paddy production rule. |
| AC-03.3 | OK | 2026-06-02 | Woodcutter placement rule covered by `WoodcutterNearForestRuleTest`. |
| AC-03.4 | KO | 2026-06-02 | User story requires Smithy placement rejection without Mine; code allows placement and blocks production instead. |
| AC-03.5 | KO | 2026-06-02 | User story requires Workshop placement rejection without Mine; code allows placement and blocks production instead. |
| AC-03.6 | OK | 2026-06-02 | Smithy/Workshop placement can succeed when Mine exists and timber is enough. |
| AC-03.7 | OK | 2026-06-02 | Failed placement returns readable `PlacementResult` messages. |
| AC-04.1 | OK | 2026-06-02 | `Villager` stores exactly one current `Role`. |
| AC-04.2 | OK | 2026-06-02 | Unhoused status maps to `Role.UNHOUSED`. |
| AC-04.3 | OK | 2026-06-02 | Housed villager without job becomes `Role.IDLE`. |
| AC-04.4 | OK | 2026-06-02 | Rice Farm assignment maps to `Role.RICE_FARMER`. |
| AC-04.5 | OK | 2026-06-02 | Building job slots map to Woodcutter, Blacksmith, Artisan, Trader, Samurai, Monk. |
| AC-04.6 | OK | 2026-06-02 | `Villager.assignRole` replaces the previous role. |
| AC-05.1 | OK | 2026-06-02 | Dwelling housing capacity covered by `HousingServiceTest`. |
| AC-05.2 | OK | 2026-06-02 | Birth flow calls `HousingService.assignHousing` when slots exist. |
| AC-05.3 | OK | 2026-06-02 | New villager remains unhoused when no dwelling slot exists. |
| AC-05.4 | OK | 2026-06-02 | Housing parameter derives from housed/population ratio. |
| AC-05.5 | OK | 2026-06-02 | Resource theft probability increases when housing is poor. |
| AC-05.6 | KO | 2026-06-02 | No idle-age or idle-to-unhoused rule/config found. |
| AC-06.1 | OK | 2026-06-02 | `JobAssignmentService` assigns eligible idle villagers during tick processing. |
| AC-06.2 | OK | 2026-06-02 | Weighted slot selection makes roles with more slots more likely. |
| AC-06.3 | OK | 2026-06-02 | No assignment occurs when no free job slots exist. |
| AC-06.4 | OK | 2026-06-02 | Assigned role comes from the selected building slot type. |
| AC-06.5 | OK | 2026-06-02 | Assignment interval checked against `jobAssignmentIntervalTicks`. |
| AC-06.6 | KO | 2026-06-02 | Death removes a random villager; no weighted job-removal logic found. |
| AC-06.7 | OK | 2026-06-02 | Fixed `RandomProvider` supports reproducible assignment tests. |
| AC-07.1 | OK | 2026-06-02 | Rice Paddy near Rice Farm and farmer increases rice. |
| AC-07.2 | OK | 2026-06-02 | Rice Paddy without farmer produces no rice. |
| AC-07.3 | OK | 2026-06-02 | `ConsumptionService` consumes rice per living villager. |
| AC-07.4 | OK | 2026-06-02 | Rice surplus advances birth progress. |
| AC-07.5 | OK | 2026-06-02 | Zero rice advances starvation death counter. |
| AC-07.6 | OK | 2026-06-02 | `AgriculturalExpansionPolicy` multiplies rice production and farmer tool cost. |
| AC-08.1 | OK | 2026-06-02 | Valid Woodcutter's Hut with Woodcutter produces timber. |
| AC-08.2 | OK | 2026-06-02 | Invalid Woodcutter forest condition is rejected or warned by rules. |
| AC-08.3 | OK | 2026-06-02 | Smithy with Blacksmith produces tools in productive setup with Mine proximity. |
| AC-08.4 | OK | 2026-06-02 | Workshop with Artisan produces luxury goods after configured interval. |
| AC-08.5 | KO | 2026-06-02 | User story requires Smithy/Workshop construction rejection without Mine; code allows construction. |
| AC-08.6 | OK | 2026-06-02 | Role-based tools/luxury consumption implemented in `ConsumptionService`. |
| AC-08.7 | OK | 2026-06-02 | `CraftsmenProductionPolicy` boosts manufacturing and rice cost for craftsmen. |
| AC-09.1 | KO | 2026-06-02 | Markets are generic; no market-for-specific-resource type found. |
| AC-09.2 | OK | 2026-06-02 | Valid trade executes through `TradeService`; code requires Market but not Trader. |
| AC-09.3 | KO | 2026-06-02 | Trade capacity scales with number of Market buildings, not assigned Traders. |
| AC-09.4 | KO | 2026-06-02 | Trade timing is fixed by 10 tick cooldown, not shortened by more Traders. |
| AC-09.5 | OK | 2026-06-02 | Insufficient source resource rejects trade. |
| AC-09.6 | OK | 2026-06-02 | Exchange rate table updates source and target resources. |
| AC-10.1 | OK | 2026-06-02 | `TickProcessor` increments tick counter exactly once. |
| AC-10.2 | OK | 2026-06-02 | Tick order is explicit in `TickProcessor.process`. |
| AC-10.3 | OK | 2026-06-02 | Tick updates resources and parameters from current state. |
| AC-10.4 | OK | 2026-06-02 | Shortage handling runs before parameter recalculation. |
| AC-10.5 | OK | 2026-06-02 | `TickResult` contains before/after state, resources, policies, births/deaths, events. |
| AC-10.6 | OK | 2026-06-02 | UI refresh happens only after `TickResult` and updated snapshot return. |
| AC-11.1 | OK | 2026-06-02 | Protection formula uses Samurai count and population. |
| AC-11.2 | OK | 2026-06-02 | Food formula uses rice amount and expected consumption. |
| AC-11.3 | OK | 2026-06-02 | Faith formula uses Monk count and population. |
| AC-11.4 | OK | 2026-06-02 | Housing formula uses housed villagers and population. |
| AC-11.5 | OK | 2026-06-02 | Craftsmanship formula uses Artisan and Blacksmith ratios. |
| AC-11.6 | OK | 2026-06-02 | Happiness recalculates after all parameters update. |
| AC-11.7 | OK | 2026-06-02 | HUD parameter panel displays updated values after tick refresh. |
| AC-12.1 | KO | 2026-06-02 | Birth progress is driven by rice surplus only; food/housing/happiness thresholds are config values but not used. |
| AC-12.2 | KO | 2026-06-02 | Birth spawns villager, but no configured birth rice cost is consumed. |
| AC-12.3 | OK | 2026-06-02 | Birth with dwelling capacity assigns housing. |
| AC-12.4 | OK | 2026-06-02 | Birth without dwelling capacity leaves villager unhoused. |
| AC-12.5 | OK | 2026-06-02 | Starvation death occurs after configured zero-rice interval. |
| AC-12.6 | OK | 2026-06-02 | Dead villager is removed from population; housing/job references disappear with entity. |
| AC-12.7 | OK | 2026-06-02 | `TickResult` reports births and deaths. |
| AC-13.1 | OK | 2026-06-02 | Agricultural Expansion activation sets active policy. |
| AC-13.2 | OK | 2026-06-02 | Agricultural policy applies 1.5 rice production and farmer tool consumption. |
| AC-13.3 | OK | 2026-06-02 | Military policy applies 1.5 protection and Samurai consumption. |
| AC-13.4 | OK | 2026-06-02 | Craftsmen policy applies 1.5 manufacturing and craftsman rice consumption. |
| AC-13.5 | OK | 2026-06-02 | `PolicyManager` rejects activation while another policy is active. |
| AC-13.6 | OK | 2026-06-02 | Policy duration expiry removes active policy and starts cooldown. |
| AC-13.7 | OK | 2026-06-02 | Policy cooldown rejects early reactivation. |
| AC-13.8 | OK | 2026-06-02 | Engine delegates modifiers through `PolicyStrategy`. |
| AC-14.1 | OK | 2026-06-02 | `RandomEventManager` evaluates events during tick. |
| AC-14.2 | OK | 2026-06-02 | Low protection increases theft probability. |
| AC-14.3 | OK | 2026-06-02 | High protection increases productivity event probability. |
| AC-14.4 | OK | 2026-06-02 | Poor housing increases theft event probability. |
| AC-14.5 | OK | 2026-06-02 | Faith and craftsmanship thresholds enable related events. |
| AC-14.6 | OK | 2026-06-02 | Events mutate resources or parameters through event effects. |
| AC-14.7 | OK | 2026-06-02 | `TickResult` stores event names/effects via `EventReport`. |
| AC-14.8 | OK | 2026-06-02 | Fixed random provider supports reproducible event tests. |
| AC-15.1 | OK | 2026-06-02 | Save creates JSON through `VillagePersistenceService`. |
| AC-15.2 | OK | 2026-06-02 | DTO includes grid, cells, features, villagers, resources, parameters, tick, policies, cooldowns, birth progress. |
| AC-15.3 | OK | 2026-06-02 | Load restores persisted village state through `VillageMapper`. |
| AC-15.4 | OK | 2026-06-02 | Loaded village can advance ticks through controller/facade. |
| AC-15.5 | OK | 2026-06-02 | Invalid save handling covered by `InvalidSaveFileTest`. |
| AC-15.6 | OK | 2026-06-02 | Policy cooldowns, market cooldown, tick counters, and progress values are serialized. |
| AC-16.1 | OK | 2026-06-02 | libGDX screen renders map, buildings, and natural features. |
| AC-16.2 | OK | 2026-06-02 | Camera pan/zoom stays in UI layer and does not mutate core state. |
| AC-16.3 | OK | 2026-06-02 | HUD panels show resources, population, and all village parameters. |
| AC-16.4 | OK | 2026-06-02 | BuildMenu selection enters build mode without constructing domain objects. |
| AC-16.5 | OK | 2026-06-02 | Screen clicks convert to `Position` through mapper/router and reach controller/facade. |
| AC-16.6 | OK | 2026-06-02 | Successful placement refreshes map and resource snapshot. |
| AC-16.7 | OK | 2026-06-02 | Failed placement leaves snapshot unchanged and logs status/error message. |
| AC-16.8 | OK | 2026-06-02 | `SelectedBuildingPanel` displays building details from view models. |
| AC-16.9 | OK | 2026-06-02 | Next-tick UI calls facade and advances exactly one tick. |
| AC-16.10 | OK | 2026-06-02 | Resource, population, parameter, policy, and event panels refresh from immutable view models. |
| AC-16.11 | OK | 2026-06-02 | Pause/speed controls change automatic tick timing only. |
| AC-16.12 | OK | 2026-06-02 | Policy buttons route through controller/facade and update HUD status. |
| AC-16.13 | OK | 2026-06-02 | UI uses snapshots/view models and does not directly mutate domain objects. |
| AC-16.14 | OK | 2026-06-02 | Missing assets use fallback placeholder and readable warning path. |

## KO Items To Fix Or Reconcile

| AC | Required action |
|---|---|
| AC-03.4, AC-03.5, AC-08.5 | Either reject Smithy/Workshop construction without Mine, or update user stories to say Mine is a production/proximity rule. |
| AC-05.6 | Implement idle-to-unhoused rule/config, or remove criterion if out of scope. |
| AC-06.6 | Implement weighted job-removal logic, or update criterion to random villager death/removal. |
| AC-09.1, AC-09.3, AC-09.4 | Add resource-specific markets and Trader-based capacity/timing, or update market criteria to current generic/cooldown design. |
| AC-12.1, AC-12.2 | Use birth thresholds and configured birth rice cost, or update birth criteria to current rice-surplus model. |
