# DaimyoSimulator — Updated Jira-Ready User Stories

## Purpose of this document

This document replaces the older **ShiroLogic_User_Stories.md** version and aligns the user stories with the final **DaimyoSimulator.md** concept.

The old document used terms such as **province**, **daimyō**, **season**, **edict**, **treasury**, **loyalty**, **roads**, **samurai district**, **market district**, and **forest protection edict**. The final DaimyoSimulator concept instead focuses on a smaller village simulation with:

- villagers and villager roles;
- rice, timber, tools, and luxury goods;
- dwellings, farms, paddies, woodcutter huts, smithies, mines, workshops, markets, guard posts, temples, and forests;
- village parameters: happiness, protection, food, faith, housing, and craftsmanship;
- one active strategy policy at a time;
- a tick-based simulation.

The stories below keep the university assignment requirements visible: rule-based grid, object placement, resource management, tick engine, policy system using Strategy Pattern, persistence, dashboard, testability, and clear acceptance criteria.

---

## Recommended Jira fields

For each Jira issue, use this structure:

```text
Title:
Epic:
As a:
I want:
So that:

Description:
Acceptance Criteria:
Technical Notes:
Related Classes:
Related Tests:
Priority:
Status:
```

Suggested Jira statuses:

```text
Backlog → Ready → In Progress → Code Review → Testing → Done
```

---

## Shared DaimyoSimulator vocabulary

| Concept | DaimyoSimulator term |
|---|---|
| City / province | Village |
| Player | Village manager / player |
| Tick / turn | Tick |
| Residential building | Dwelling |
| Food | Rice |
| Construction material | Timber |
| Manufactured resource | Tools |
| Luxury resource | Luxury Goods |
| Security | Protection |
| Religion | Faith |
| Technology / industry | Craftsmanship |
| Military building | Guard Post |
| Religious building | Temple |
| Policy / ordinance / edict | Strategy Policy |
| Main health indicator | Happiness |

---

# Epic 1 — Village Map and Building Placement

---

## US-01 — Create a new DaimyoSimulator village

### Epic

Village Map and Building Placement

### User story

**As a** player,  
**I want** to create a new empty DaimyoSimulator village,  
**so that** I can start a new simulation from a valid initial state.

### Description

The system initializes the village state, the logical map, the initial resources, the initial villagers, the tick counter, and the randomly spawned forests. This story is the foundation for all other stories.

Even if the game has a simple UI, the map must exist as a logic-based grid in the domain model. The UI must not own the simulation data.

### Acceptance criteria

```text
AC-01.1
Given the user starts a new simulation
When the system initializes the village
Then a logical grid map is created with a fixed configurable size, for example 20x20 cells

AC-01.2
Given the village map has been created
When the user inspects a cell
Then the cell has coordinates and can report whether it is empty, occupied by a building, or occupied by a natural feature

AC-01.3
Given the village is initialized
When the user checks the village resources
Then rice, timber, tools, and luxury goods are initialized to their configured starting values

AC-01.4
Given the village is initialized
When the user checks the village parameters
Then happiness, protection, food, faith, housing, and craftsmanship are initialized

AC-01.5
Given the village is initialized
When the system creates the map
Then forest features are spawned randomly according to the configured map-generation rules

AC-01.6
Given the user tries to access coordinates outside the grid
When the operation is executed
Then the system rejects the operation with a clear error message and the village state remains unchanged
```

### Technical notes

Create the grid and village state independently from the UI. A `VillageInitializer` or `GameInitializer` can build the initial state.

### Related classes

```text
Village
VillageState
Grid
Cell
Position
ResourceStock
VillageInitializer
ForestFeature
```

### Related tests

```text
VillageInitializerTest
GridTest
CellTest
PositionTest
InitialResourceStockTest
ForestGenerationTest
```

### Priority

Very High

---

## US-02 — Construct buildings using timber

### Epic

Village Map and Building Placement

### User story

**As a** player,  
**I want** to construct buildings on the village grid using timber,  
**so that** I can decide how the village develops.

### Description

The player can place DaimyoSimulator buildings on the map. Every building costs timber. The system must reject construction when the target cell is invalid, occupied, or when the village does not have enough timber.

This story covers the general building placement mechanism. Specific rules for farms, paddies, woodcutters, mines, smithies, workshops, markets, guard posts, temples, and dwellings are detailed in later stories.

### Acceptance criteria

```text
AC-02.1
Given the player selects an empty valid cell
When the player constructs a building
Then the building is placed in that cell

AC-02.2
Given the player constructs a building
When the placement succeeds
Then the required amount of timber is subtracted from the village resources

AC-02.3
Given the selected cell is already occupied
When the player tries to construct a building
Then the placement is rejected and no timber is consumed

AC-02.4
Given the village does not have enough timber
When the player tries to construct a building
Then the placement is rejected and the village state remains unchanged

AC-02.5
Given the player requests the list of constructible buildings
When the system returns the list
Then it includes Dwelling, Rice Farm, Rice Paddy, Woodcutter's Hut, Smithy, Mine, Workshop, Market, Guard Post, and Temple

AC-02.6
Given the player constructs a building
When the building is created
Then the construction is performed through a building creation component instead of hardcoding every concrete class inside the simulation engine
```

### Technical notes

Use a Factory Pattern or equivalent creation service for buildings. The simulation engine should not know all concrete building constructors.

### Related classes

```text
Building
BuildingType
BuildingFactory
ConstructionService
VillageState
Grid
Cell
ResourceStock
```

### Related tests

```text
ConstructionServiceTest
BuildingFactoryTest
BuildingPlacementTest
InsufficientTimberTest
OccupiedCellPlacementTest
```

### Priority

Very High

---

## US-03 — Enforce building placement and prerequisite rules

### Epic

Village Map and Building Placement

### User story

**As a** player,  
**I want** construction rules to be enforced,  
**so that** the village grows according to meaningful simulation constraints.

### Description

Some buildings require nearby buildings or map features. Rice paddies need a rice farm nearby to produce rice. Woodcutter huts need to be near a forest. Smithies and workshops need a mine nearby to operate.

These rules are important because the assignment requires rule enforcement in the simulation engine.

> **Implementation note (current build):** Only the Woodcutter's Hut carries a hard *placement* rule (must be within range 1 of a forest). The Mine requirement for Smithy/Workshop and the Rice Farm requirement for Rice Paddy are enforced at **production** time (the building can be placed anywhere but only produces when the required neighbour is within `adjacencyRange`, default 1). The unused `MineRequiredRule` class remains in the codebase for reference.

### Acceptance criteria

```text
AC-03.1
Given the player tries to place a Rice Paddy
When there is no Rice Farm close enough according to the configured adjacency rule
Then the placement or production rule is rejected according to the chosen design

AC-03.2
Given a Rice Paddy is near a Rice Farm
When the player places or validates the Rice Paddy
Then the rule is satisfied

AC-03.3
Given the player tries to place a Woodcutter's Hut
When there is no forest near the selected cell
Then the placement is rejected

AC-03.4
Given the player tries to place a Smithy
When the village has no Mine
Then the placement is rejected

AC-03.5
Given the player tries to place a Workshop
When the village has no Mine
Then the placement is rejected

AC-03.6
Given the village has at least one Mine
When the player places a Smithy or Workshop on a valid empty cell
Then the placement can succeed if the village has enough timber

AC-03.7
Given a rule is violated during construction or validation
When the system rejects the action
Then the error message explains which rule failed
```

### Technical notes

Keep placement rules in separate `PlacementRule` or `SimulationRule` classes. Do not put every rule directly inside the `Village` class.

### Related classes

```text
PlacementRule
RicePaddyNearFarmRule
WoodcutterNearForestRule
MineRequiredRule
Grid
Position
ConstructionService
```

### Related tests

```text
RicePaddyNearFarmRuleTest
WoodcutterNearForestRuleTest
MineRequiredRuleTest
PlacementRuleTest
```

### Priority

Very High

---

# Epic 2 — Villagers, Housing, and Jobs

---

## US-04 — Manage villagers and villager roles

### Epic

Villagers, Housing, and Jobs

### User story

**As a** player,  
**I want** every person in the village to be represented as a villager with one role,  
**so that** the simulation can assign people to housing, jobs, protection, trade, and faith.

### Description

Every person in the village is a `Villager`. A villager can have only one role at a time. Roles include Unhoused Villager, Idle Villager, Rice Farmer, Woodcutter, Blacksmith, Artisan, Trader, Samurai, and Monk.

This story introduces the citizen model needed for the DaimyoSimulator domain.

### Acceptance criteria

```text
AC-04.1
Given a villager exists
When the system checks the villager
Then the villager has exactly one current role

AC-04.2
Given a villager has no dwelling
When the system updates housing status
Then the villager role or status can be marked as Unhoused Villager

AC-04.3
Given a villager has a dwelling but no job
When the system updates job status
Then the villager can be marked as Idle Villager

AC-04.4
Given a villager is assigned to a Rice Farm
When the assignment succeeds
Then the villager role becomes Rice Farmer

AC-04.5
Given a villager is assigned to a Woodcutter's Hut, Smithy, Workshop, Market, Guard Post, or Temple
When the assignment succeeds
Then the villager role becomes Woodcutter, Blacksmith, Artisan, Trader, Samurai, or Monk respectively

AC-04.6
Given the system tries to assign a villager to a second role
When the villager already has a role
Then the previous role is replaced or the assignment is rejected according to the chosen role-management rule
```

### Technical notes

Separate villager identity from role logic. A `Role` enum can be enough for the first version, but a role strategy/class hierarchy can be introduced later if needed.

### Related classes

```text
Villager
VillagerRole
VillagerStatus
RoleAssignmentService
Dwelling
WorkplaceBuilding
```

### Related tests

```text
VillagerTest
VillagerRoleTest
RoleAssignmentServiceTest
```

### Priority

High

---

## US-05 — Manage dwellings and housing status

### Epic

Villagers, Housing, and Jobs

### User story

**As a** player,  
**I want** to build dwellings and house villagers,  
**so that** the housing parameter can affect village happiness and events.

### Description

Dwellings provide housing capacity. Every villager is assigned to a dwelling at birth if one is available. If no dwelling capacity is available, the villager becomes unhoused. The final DaimyoSimulator rules also allow a villager to become unhoused after remaining idle for a configured number of ticks.

### Acceptance criteria

```text
AC-05.1
Given the player constructs a Dwelling
When the placement succeeds
Then the total housing capacity of the village increases

AC-05.2
Given a new villager is born and a dwelling slot is available
When the villager is created
Then the villager is assigned to a dwelling

AC-05.3
Given a new villager is born and no dwelling slot is available
When the villager is created
Then the villager becomes unhoused

AC-05.4
Given one or more villagers are unhoused
When village parameters are updated
Then the housing parameter is affected

AC-05.5
Given the number of unhoused villagers is high enough
When random events are evaluated
Then housing-related events such as food theft can become more likely

AC-05.6
Given a villager remains idle for the configured number of ticks
When the idle-to-unhoused rule is active
Then the villager can become unhoused according to the DaimyoSimulator rules
```

### Technical notes

Keep the idle-to-unhoused behavior configurable because it is a special gameplay rule and may need balancing.

### Related classes

```text
Dwelling
HousingService
Villager
VillageState
HousingCalculator
RandomEventManager
```

### Related tests

```text
DwellingTest
HousingServiceTest
UnhousedVillagerTest
HousingCalculatorTest
IdleToUnhousedRuleTest
```

### Priority

High

---

## US-06 — Automatically assign idle villagers to available jobs

### Epic

Villagers, Housing, and Jobs

### User story

**As a** player,  
**I want** idle villagers to be assigned automatically to available jobs,  
**so that** buildings become productive without manually assigning every villager.

### Description

The system checks available job slots in buildings and assigns idle villagers probabilistically. The probability depends on the number and type of available slots. For example, if the village has many rice farmer slots and few samurai slots, rice farmer assignment should be more likely.

The same weighted logic can also be used when removing villagers from jobs, such as when a villager dies.

### Acceptance criteria

```text
AC-06.1
Given there are idle villagers and available job slots
When the job assignment step runs during a tick
Then at least one eligible idle villager can be assigned to an available role

AC-06.2
Given the village has more available Rice Farmer slots than Samurai slots
When automatic job assignment runs many times with a fixed random seed
Then Rice Farmer assignments occur more often than Samurai assignments

AC-06.3
Given a building has no free job slots
When job assignment runs
Then no villager is assigned to that building

AC-06.4
Given a villager is assigned to a job
When the assignment succeeds
Then the villager role matches the building type

AC-06.5
Given the configured assignment rate is one villager every X ticks
When fewer than X ticks have passed since the last assignment
Then no automatic assignment occurs

AC-06.6
Given a villager must be removed from a job because of death or another rule
When the removal logic runs
Then the system chooses the removed job according to the configured weighted logic

AC-06.7
Given tests use a fixed random seed
When assignments are executed
Then the results are reproducible
```

### Technical notes

Use a `RandomProvider` interface or injectable random seed to make tests deterministic.

### Related classes

```text
JobAssignmentService
JobSlot
WorkplaceBuilding
Villager
VillagerRole
RandomProvider
```

### Related tests

```text
JobAssignmentServiceTest
WeightedRoleAssignmentTest
JobRemovalServiceTest
RandomProviderTest
```

### Priority

Very High

---

# Epic 3 — Resource Production and Trade

---

## US-07 — Produce and consume rice

### Epic

Resource Production and Trade

### User story

**As a** player,  
**I want** rice farms and rice paddies to produce rice only when farmers are available,  
**so that** food management is central to village survival.

### Description

Rice is the food resource of the village. Rice paddies produce rice only if the required farming conditions are satisfied. In the final DaimyoSimulator rules, rice paddies do not produce food by default without farmers. Rice is collected automatically and consumed every tick.

### Acceptance criteria

```text
AC-07.1
Given a Rice Paddy exists near a Rice Farm
And the Rice Farm has at least one Rice Farmer
When the production step runs during a tick
Then the village rice amount increases

AC-07.2
Given a Rice Paddy exists but no Rice Farmer is available
When the production step runs during a tick
Then the Rice Paddy produces no rice

AC-07.3
Given the village has living villagers
When the consumption step runs during a tick
Then rice is consumed according to the number of villagers

AC-07.4
Given rice is high enough according to the configured threshold
When birth and food rules are evaluated
Then the village can progress toward a new birth

AC-07.5
Given rice reaches zero
When death rules are evaluated
Then one villager can die every configured number of ticks

AC-07.6
Given Agricultural Expansion Policy is active
When rice production is calculated
Then rice paddy production is multiplied by the policy modifier and tool consumption is also increased
```

### Technical notes

Rice production, rice consumption, starvation, and birth readiness should be testable in isolation.

### Related classes

```text
RiceFarm
RicePaddy
RiceProductionService
RiceConsumptionService
FoodCalculator
BirthDeathService
AgriculturalExpansionPolicy
```

### Related tests

```text
RiceProductionServiceTest
RiceConsumptionServiceTest
RicePaddyWithoutFarmerTest
FoodCalculatorTest
AgriculturalExpansionPolicyTest
```

### Priority

Very High

---

## US-08 — Produce timber, tools, and luxury goods

### Epic

Resource Production and Trade

### User story

**As a** player,  
**I want** craftsmen buildings to produce timber, tools, and luxury goods,  
**so that** the village can construct buildings, support workers, and maintain advanced roles.

### Description

Timber is produced by woodcutters in Woodcutter's Huts near forests. Tools are produced by blacksmiths in Smithies. Luxury Goods are produced by artisans in Workshops every several ticks. Smithies and Workshops require at least one Mine to exist.

Tools are used by samurai and rice farmers. Luxury Goods are used by samurai and monks.

### Acceptance criteria

```text
AC-08.1
Given a Woodcutter's Hut is near a forest and has at least one Woodcutter
When the production step runs
Then timber increases

AC-08.2
Given a Woodcutter's Hut is not near a forest
When placement or validation occurs
Then the rule is rejected according to the construction rules

AC-08.3
Given a Smithy exists and has at least one Blacksmith
When the production step runs
Then tools increase once every tick

AC-08.4
Given a Workshop exists and has at least one Artisan
When the configured number of ticks has passed
Then luxury goods increase

AC-08.5
Given there is no Mine in the village
When the player tries to build a Smithy or Workshop
Then construction is rejected

AC-08.6
Given samurai, monks, or rice farmers exist
When the consumption step runs
Then the correct amount of tools or luxury goods is consumed according to their role requirements

AC-08.7
Given Craftsmen Production Policy is active
When production is calculated
Then timber, tools, and luxury goods production are multiplied by the policy modifier and craftsmen consume more rice
```

### Technical notes

Implement different production frequencies. Tools are produced every tick; luxury goods are produced every several ticks.

### Related classes

```text
WoodcutterHut
Smithy
Mine
Workshop
CraftProductionService
ResourceConsumptionService
CraftsmenProductionPolicy
ProductionFrequencyRule
```

### Related tests

```text
WoodcutterHutTest
SmithyTest
WorkshopTest
MineRequiredRuleTest
CraftProductionServiceTest
ProductionFrequencyRuleTest
CraftsmenProductionPolicyTest
```

### Priority

High

---

## US-09 — Exchange resources through markets

### Epic

Resource Production and Trade

### User story

**As a** player,  
**I want** markets and traders to exchange resources,  
**so that** I can recover from shortages and support different strategies.

### Description

The final DaimyoSimulator concept uses one different market building per resource. Each market holds traders and allows resources to be exchanged. More traders in a market increase how much can be exchanged and shorten the trade timer.

> **Implementation note (current build):** Trading shipped as a **single shared Market type** (any Market cell opens the same dialog) rather than one market per resource. Trade-volume capacity scales with the number of Market buildings (`10 units × Market count`), and after any trade the market is locked for a fixed **10-tick cooldown** (instead of a per-trader timer). Exchange uses an asymmetric rate table (see the project [README](../README.md)), not a single flat rate.

### Acceptance criteria

```text
AC-09.1
Given the player builds a Market for a specific resource type
When construction succeeds
Then that market can be used for trades involving that resource type

AC-09.2
Given a Market has at least one Trader
When the player requests a valid trade
Then the system schedules or executes the exchange according to the market rules

AC-09.3
Given a Market has more Traders assigned
When trade capacity is calculated
Then more resources can be exchanged

AC-09.4
Given a Market has more Traders assigned
When trade timing is calculated
Then the time required to complete a trade is shorter

AC-09.5
Given the village does not have enough of the source resource
When the player requests a trade
Then the trade is rejected

AC-09.6
Given a trade is completed
When the village resources are updated
Then the source resource decreases and the target resource increases according to the configured exchange rate
```

### Technical notes

A simple market service is enough. You do not need a complex economy unless the core simulation is already complete.

### Related classes

```text
Market
ResourceMarket
TradeRequest
TradeService
Trader
ResourceType
ResourceStock
```

### Related tests

```text
MarketTest
TradeServiceTest
TradeCapacityTest
TradeTimerTest
InvalidTradeTest
```

### Priority

Medium

---

# Epic 4 — Tick Engine and Village Parameters

---

## US-10 — Advance the simulation by one tick

### Epic

Tick Engine and Village Parameters

### User story

**As a** player,  
**I want** to advance the village simulation by one tick,  
**so that** I can observe how buildings, villagers, resources, policies, and events change the village over time.

### Description

Ticks are advanced through a button or command. The tick processor must execute the final DaimyoSimulator tick order:

1. Advance tick counter.
2. Update active policy duration and cooldown.
3. Validate building rules.
4. Assign idle villagers to available jobs.
5. Produce resources.
6. Consume resources.
7. Apply shortages and penalties.
8. Update village parameters.
9. Recalculate happiness.
10. Process births and deaths.
11. Trigger random events if conditions are met.
12. Notify dashboard/status observers.

### Acceptance criteria

```text
AC-10.1
Given the user presses the next-tick button
When the command is executed
Then the tick counter increases by one

AC-10.2
Given a tick is executed
When the tick processor runs
Then the steps are executed in the documented order

AC-10.3
Given buildings, villagers, resources, and an active policy exist
When a tick is executed
Then the system updates resources and parameters according to the current state

AC-10.4
Given shortages occur during the tick
When shortage rules are applied
Then penalties are applied before village parameters and happiness are finalized

AC-10.5
Given a tick completes
When the system returns the result
Then the TickResult contains before-state, after-state, produced resources, consumed resources, policy effects, births/deaths, and event information

AC-10.6
Given the dashboard or logger observes village state
When a tick completes
Then observers are notified after all state changes are complete
```

### Technical notes

Use a `TickProcessor` or `SimulationEngine` that coordinates services instead of implementing every calculation directly in one huge class.

### Related classes

```text
SimulationEngine
TickProcessor
TickResult
VillageState
PolicyManager
ProductionService
ConsumptionService
VillageParameterCalculator
RandomEventManager
VillageObserver
```

### Related tests

```text
SimulationEngineTest
TickProcessorOrderTest
TickResultTest
ObserverNotificationTest
```

### Priority

Very High

---

## US-11 — Calculate village parameters and happiness

### Epic

Tick Engine and Village Parameters

### User story

**As a** player,  
**I want** the village parameters and happiness to be recalculated every tick,  
**so that** I can understand the current condition of the village.

### Description

The village has several parameters: happiness, protection, food, faith, housing, and craftsmanship. Each parameter is based on the state of villagers, roles, resources, buildings, and shortages. All parameters except happiness contribute to the general happiness indicator.

Happiness itself is an indicator of the current situation. It does not need to apply direct bonuses or penalties unless the team later decides to extend the design.

### Acceptance criteria

```text
AC-11.1
Given the number of Samurai or total villagers changes
When village parameters are recalculated
Then protection changes according to the configured protection formula

AC-11.2
Given rice amount or rice capacity changes
When village parameters are recalculated
Then food changes according to the configured food formula

AC-11.3
Given the number of Monks or villagers changes
When village parameters are recalculated
Then faith changes according to the configured faith formula

AC-11.4
Given the number of unhoused villagers or dwellings changes
When village parameters are recalculated
Then housing changes according to the configured housing formula

AC-11.5
Given tools amount or villager count changes
When village parameters are recalculated
Then craftsmanship changes according to the configured craftsmanship formula

AC-11.6
Given protection, food, faith, housing, and craftsmanship have been updated
When happiness is recalculated
Then happiness reflects the current village situation

AC-11.7
Given the dashboard displays village parameters
When a tick is completed
Then the dashboard shows updated happiness, protection, food, faith, housing, and craftsmanship
```

### Technical notes

Keep parameter formulas in separate calculator classes or methods so they are easy to test and tune.

### Related classes

```text
VillageParameterCalculator
HappinessCalculator
ProtectionCalculator
FoodCalculator
FaithCalculator
HousingCalculator
CraftsmanshipCalculator
VillageState
```

### Related tests

```text
VillageParameterCalculatorTest
HappinessCalculatorTest
ProtectionCalculatorTest
FoodCalculatorTest
FaithCalculatorTest
HousingCalculatorTest
CraftsmanshipCalculatorTest
```

### Priority

High

---

## US-12 — Process births and deaths

### Epic

Tick Engine and Village Parameters

### User story

**As a** player,  
**I want** villagers to be born or die according to food, housing, happiness, and starvation rules,  
**so that** population growth depends on how well I manage the village.

### Description

Birth depends on high food levels and can also depend on housing and happiness. The preferred rule for implementation is the birth-progress rule because it is smoother and easier to test than an instant food drop from 80% to 20%.

Death depends on low food levels. If rice reaches zero, one villager can die every configured number of ticks.

### Acceptance criteria

```text
AC-12.1
Given food, housing, and happiness are above the configured birth thresholds
When a tick is processed
Then birth progress increases by the configured birth rate

AC-12.2
Given birth progress reaches or exceeds 100
When births are processed
Then one new villager is spawned and a configured amount of rice is consumed

AC-12.3
Given a new villager is spawned and a dwelling slot is available
When the birth is processed
Then the villager is assigned to a dwelling

AC-12.4
Given a new villager is spawned and no dwelling slot is available
When the birth is processed
Then the villager becomes unhoused

AC-12.5
Given rice reaches zero
When the death rule is evaluated after the configured number of starvation ticks
Then one living villager dies

AC-12.6
Given a villager dies
When the death is processed
Then the villager is removed from population, housing, and any assigned job

AC-12.7
Given births or deaths occur during a tick
When the TickResult is generated
Then it reports the population change clearly
```

### Technical notes

Use configurable thresholds for food, housing, happiness, birth rate, rice cost per birth, and death interval.

### Related classes

```text
BirthDeathService
BirthProgress
Villager
HousingService
JobAssignmentService
VillageState
TickResult
```

### Related tests

```text
BirthDeathServiceTest
BirthProgressTest
BirthWithAvailableDwellingTest
BirthWithoutDwellingTest
StarvationDeathTest
VillagerDeathCleanupTest
```

### Priority

High

---

# Epic 5 — Strategy Policies and Random Events

---

## US-13 — Activate one strategy policy at a time

### Epic

Strategy Policies and Random Events

### User story

**As a** player,  
**I want** to activate one strategy policy at a time,  
**so that** I can temporarily change the village's production or protection strategy.

### Description

The final DaimyoSimulator policies are:

- Agricultural Expansion Policy: rice paddies produce 1.5x rice and consume 1.5x tools.
- Military Protection Policy: samurai values become 1.5x and consume 1.5x luxury goods and tools.
- Craftsmen Production Policy: tools, timber, and luxury goods production become 1.5x, while craftsmen consume 1.5x more rice.

Only one policy can be active at a time. Policies last X ticks and have a reload/cooldown time of Y ticks. This story must be implemented with the Strategy Pattern.

### Acceptance criteria

```text
AC-13.1
Given no policy is active
When the player activates Agricultural Expansion Policy
Then that policy becomes the active policy

AC-13.2
Given Agricultural Expansion Policy is active
When rice production is calculated
Then rice production is multiplied by 1.5 and tool consumption related to farming is multiplied by 1.5

AC-13.3
Given Military Protection Policy is active
When protection is calculated
Then samurai protection value is multiplied by 1.5 and samurai luxury/tools consumption is multiplied by 1.5

AC-13.4
Given Craftsmen Production Policy is active
When craftsmen production is calculated
Then timber, tools, and luxury goods production are multiplied by 1.5 and craftsmen rice consumption is multiplied by 1.5

AC-13.5
Given a policy is already active
When the player tries to activate another policy
Then the system rejects the action or replaces the policy according to the chosen policy-manager rule

AC-13.6
Given an active policy has lasted X ticks
When the policy duration expires
Then the active policy is removed and its cooldown starts

AC-13.7
Given a policy is on cooldown
When the player tries to activate it before Y ticks pass
Then the activation is rejected

AC-13.8
Given the simulation engine calculates production, consumption, or protection
When a policy is active
Then the calculation is modified through the active policy strategy object, not through hardcoded policy-specific if-statements inside the engine
```

### Technical notes

Implement `PolicyStrategy` and concrete policy classes. Use `NoPolicy` as the default strategy.

### Related classes

```text
PolicyStrategy
NoPolicy
AgriculturalExpansionPolicy
MilitaryProtectionPolicy
CraftsmenProductionPolicy
PolicyManager
SimulationEngine
```

### Related tests

```text
PolicyManagerTest
AgriculturalExpansionPolicyTest
MilitaryProtectionPolicyTest
CraftsmenProductionPolicyTest
PolicyDurationCooldownTest
SimulationEnginePolicyStrategyTest
```

### Priority

Very High

---

## US-14 — Trigger random village events

### Epic

Strategy Policies and Random Events

### User story

**As a** player,  
**I want** random events to occur based on village parameters,  
**so that** the simulation feels dynamic and the village state has consequences.

### Description

Random events can be unlocked or influenced by village parameters. Protection can cause productivity bonuses or theft penalties. Housing can unlock theft of food when there are many unhoused villagers. Faith and craftsmanship can unlock additional positive or negative events.

The first version should keep events simple and testable. Example events:

- Resource Theft: removes rice, timber, tools, or luxury goods.
- Productivity Spike: increases production temporarily.
- Religious Festival: improves happiness or faith temporarily.
- Craftsmanship Breakthrough: adds tools or improves craftsmanship temporarily.
- Workshop Accident: reduces tools/luxury goods or lowers happiness.

### Acceptance criteria

```text
AC-14.1
Given random events are enabled
When the event step runs during a tick
Then the RandomEventManager evaluates whether an event occurs

AC-14.2
Given protection is low
When events are evaluated
Then theft events become more likely

AC-14.3
Given protection is high
When events are evaluated
Then theft events become less likely or productivity bonus events become more likely

AC-14.4
Given housing is poor because there are many unhoused villagers
When events are evaluated
Then food theft or unrest events can become available

AC-14.5
Given faith or craftsmanship crosses a configured threshold
When events are evaluated
Then faith-related or craftsmanship-related events can become available

AC-14.6
Given an event occurs
When the event is applied
Then village resources, parameters, or temporary modifiers are changed according to the event effect

AC-14.7
Given a tick completes
When the TickResult is generated
Then the event name and event effects are recorded

AC-14.8
Given tests use a fixed random seed
When events are evaluated
Then event results are reproducible
```

### Technical notes

A generic `RandomEvent` interface is enough. Use simple numeric effects and avoid complex event chains until the core simulation works.

### Related classes

```text
RandomEvent
RandomEventManager
RandomProvider
ResourceTheftEvent
ProductivitySpikeEvent
ReligiousFestivalEvent
CraftsmanshipBreakthroughEvent
WorkshopAccidentEvent
TickResult
```

### Related tests

```text
RandomEventManagerTest
ResourceTheftEventTest
ProductivitySpikeEventTest
ParameterBasedEventUnlockTest
RandomSeedEventTest
```

### Priority

Medium

---

# Epic 6 — Persistence and Dashboard

---

## US-15 — Save and load the village

### Epic

Persistence and Dashboard

### User story

**As a** player,  
**I want** to save and load the current village,  
**so that** I can continue the simulation later.

### Description

The assignment requires file-based persistence. The system should save and load the village using JSON or XML. JSON is recommended. No database should be used.

The save file must include enough information to continue the simulation correctly: grid, buildings, natural features, villagers, roles, housing assignments, resources, parameters, tick number, active policy, cooldowns, birth progress, event settings, and relevant timers.

### Acceptance criteria

```text
AC-15.1
Given the village has buildings, villagers, resources, and an updated state
When the player saves the village
Then a JSON save file is created

AC-15.2
Given the save file is created
When the file is inspected
Then it contains grid size, cell contents, buildings, forest features, villagers, villager roles, resources, parameters, tick number, active policy, policy cooldowns, and birth progress

AC-15.3
Given a valid save file exists
When the player loads it
Then the grid, buildings, villagers, resources, parameters, active policy, cooldowns, and tick number are restored

AC-15.4
Given a village has been loaded
When the player advances one tick
Then the simulation continues from the loaded state

AC-15.5
Given the save file is missing, corrupted, or incompatible
When the player tries to load it
Then the system shows a clear error message and does not crash

AC-15.6
Given the village was saved while a policy cooldown or production timer was active
When the village is loaded
Then the timer values are preserved
```

### Technical notes

Use DTOs for persistence if serializing the domain objects directly becomes difficult. (Implemented with **Jackson** via `VillagePersistenceService` + `VillageMapper` and the `core.persistence.dto` records, saving to 5 numbered slots.)

### Related classes

```text
SaveLoadService
VillageDTO
GridDTO
CellDTO
BuildingDTO
VillagerDTO
ResourceStockDTO
PolicyDTO
JsonSerializer
JsonDeserializer
```

### Related tests

```text
SaveLoadServiceTest
JsonSerializationTest
JsonDeserializationTest
InvalidSaveFileTest
SaveLoadIntegrationTest
PolicyTimerPersistenceTest
```

### Priority

High

---

## US-16 — Interact with the village through the libGDX HUD

### Epic

Presentation, Dashboard, and User Interaction

### User story

**As a** player,  
**I want** to view and control the village through a libGDX Scene2D dashboard and HUD,  
**so that** I can understand the village state and perform common actions visually without bypassing the core simulation rules.

### Description

The dashboard is implemented as a **libGDX Scene2D UI**, not as a console, Swing, or JavaFX dashboard. It is displayed above the libGDX world renderer and shows the current resources, villagers, roles, buildings, parameters, active policy, cooldowns, current tick, selected cell/building details, and recent events.

The world map is rendered with libGDX `SpriteBatch` and `OrthographicCamera`. The HUD is rendered with libGDX `Stage` and Scene2D UI widgets. The libGDX layer communicates with the simulation only through `GameController`, `CoreGameFacade`, application services, DTOs, immutable snapshots, or view models.

This story is listed as US-16 because it is useful for presentation. If the team wants to strictly stay within 15 official Jira stories, this can be included as a subtask of US-10 or US-15.

### Acceptance criteria

```text
AC-16.1
Given the village has been initialized
When the libGDX village screen is opened
Then the player sees a 2D pixel-art village map representing the logical grid, buildings, and natural features

AC-16.2
Given the player uses camera controls
When the player pans or zooms the map
Then the OrthographicCamera moves or changes zoom without changing the core village state

AC-16.3
Given the village has been initialized
When the Scene2D HUD is displayed
Then it shows rice, timber, tools, luxury goods, population, idle villagers, unhoused villagers, employed villagers, happiness, protection, food, faith, housing, and craftsmanship

AC-16.4
Given the player selects a building button in the BuildMenu
When the selection is made
Then the UI enters build mode for that BuildingType and no domain object is mutated yet

AC-16.5
Given the player is in build mode
When the player clicks a valid map cell
Then the screen coordinates are converted to a core Position and the placement command is forwarded to GameController or CoreGameFacade

AC-16.6
Given a placement command succeeds
When the HUD refreshes
Then the new building appears on the map and the displayed resources match the updated VillageSnapshot

AC-16.7
Given a placement command fails
When the HUD refreshes
Then the map and resources remain unchanged and the event/status log shows the placement error

AC-16.8
Given the player clicks a cell containing a building
When the selection is processed
Then the SelectedBuildingPanel displays the building type, workers/job slots, production or role information, and relevant status from CellViewModel or BuildingViewModel

AC-16.9
Given the player presses the next-tick button
When the command is executed
Then the UI calls GameController or CoreGameFacade and the tick counter advances exactly once

AC-16.10
Given a tick has completed
When the core returns a TickResult and updated VillageSnapshot
Then ResourcePanel, PopulationPanel, VillageParameterPanel, PolicyPanel, and EventLogPanel refresh from immutable view models

AC-16.11
Given the player uses pause or speed controls
When automatic tick playback is paused or the speed is changed
Then only the timing of advanceTick calls changes and no simulation rule is executed inside the renderer

AC-16.12
Given the player clicks a policy button
When the policy command is executed
Then the request goes through GameController or CoreGameFacade and the HUD displays the active policy or cooldown result

AC-16.13
Given the dashboard displays village state
When the user interacts with any UI control
Then the UI never directly changes Village, Cell, Building, ResourceStock, Villager, or other domain objects

AC-16.14
Given a required development sprite is missing
When the renderer requests the asset
Then a placeholder sprite is shown and a readable warning is logged
```

### Technical notes

Use MVC / Clean Architecture. The world renderer and HUD belong to the libGDX module. They may depend on the core module but must only use the public controller/facade API and immutable view models.

Recommended UI subtasks:

```text
US-16.1 — View the pixel-art village map
US-16.2 — Move and zoom the camera
US-16.3 — Select build buttons from the Scene2D BuildMenu
US-16.4 — Place buildings through the libGDX map
US-16.5 — View resources, population, and parameters in the HUD
US-16.6 — Select a building and view SelectedBuildingPanel
US-16.7 — Control next tick, pause, and speed from the UI
US-16.8 — Activate strategy policies from PolicyPanel
US-16.9 — Verify every UI mutation goes through GameController/CoreGameFacade
```

### Related classes

```text
DaimyoSimulatorGame
VillageScreen
WorldRenderer
TileRenderer
BuildingRenderer
AnimationRenderer
HudStageFactory
DashboardHud
BuildMenu
ResourcePanel
PopulationPanel
VillageParameterPanel
SelectedBuildingPanel
SpeedControlPanel
PolicyPanel
EventLogPanel
GameInputProcessor
CameraController
ScreenToGridMapper
GameController
CoreGameFacade
VillageSnapshot
CellViewModel
BuildingViewModel
DashboardViewModel
```

### Related tests

```text
CoreGameFacadeTest
DashboardViewModelTest
ScreenToGridMapperTest
BuildModeStateTest
UiCommandBoundaryTest
AssetRegistrySmokeTest
ManualHudRefreshSystemTest
ManualCameraControlSystemTest
```

### Priority

Medium
