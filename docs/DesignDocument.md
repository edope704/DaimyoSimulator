# DaimyoSimulator — Design Document

**Project type:** Java rule-based simulation engine inspired by SimCity Lite — Ancient Japan village management (~year 1200).

**Main objective:** the player develops and balances a village by placing buildings, letting the simulation assign villagers to the job slots those buildings create, managing resources, activating one strategy policy at a time, and advancing the simulation through ticks.

The core simulation is pure Java (no libGDX), so it is usable from unit tests, persistence, or any UI. The libGDX module renders immutable snapshots and sends commands through `CoreGameFacade`. Diagrams use **Mermaid**, rendered directly by GitHub.

---

## 1. Domain Model

DaimyoSimulator is centered on a `Village` (aggregate root) holding a logical `Grid`, the current resources (`ResourceStock`) and `VillageParameters`, the `Villager` list, a `PolicyManager`, and the tick counter. The player does not control villagers directly: they construct buildings, and the simulation assigns idle villagers to the job slots created by those buildings.

Main concepts:

- **Grid / Cell / Position** — a rectangular logical map (e.g. 20×20); each cell holds at most one building or one natural feature.
- **Natural Feature** — `FOREST`, generated randomly at map creation; required near Woodcutter's Huts.
- **Villager / Role** — a person who is unhoused, idle, or employed (Rice Farmer, Woodcutter, Blacksmith, Artisan, Trader, Samurai, Monk).
- **Building** — placed by the player using timber; provides housing, job slots, production, exchange, or bonuses.
- **Resources** — Rice, Timber, Tools, Luxury Goods.
- **Village Parameters** — Happiness, Protection, Food, Faith, Housing, Craftsmanship; recalculated every tick by `VillageParameterCalculator`. Happiness is derived from the other parameters by `HappinessCalculator`, keeping that rule testable and separate from the raw `VillageParameters`.
- **Strategy Policy** — one active temporary policy modifying production, protection, or consumption.
- **Random Event** — condition- or probability-based, triggered during ticks.

```mermaid
classDiagram
    class Village {
        Grid grid
        ResourceStock resources
        VillageParameters parameters
        List~Villager~ villagers
        PolicyManager policyManager
        long tickNumber
        int birthProgress
        getResources()
        getParameters()
    }
    class Grid {
        int width
        int height
        getCell(Position)
        getNeighbours(Position)
    }
    class Cell {
        Position position
        Building building
        NaturalFeature feature
        isEmpty()
    }
    class Position { int x; int y }
    class NaturalFeature { <<enumeration>> FOREST }
    class ResourceStock {
        int rice
        int timber
        int tools
        int luxuryGoods
        has(ResourceType, int)
        add(ResourceType, int)
        consume(ResourceType, int)
    }
    class VillageParameters {
        int happiness
        int protection
        int food
        int faith
        int housing
        int craftsmanship
    }
    class Villager { long id; Role role; HousingStatus housingStatus }
    class HousingStatus { <<enumeration>> HOUSED; UNHOUSED }
    class Role {
        <<enumeration>>
        UNHOUSED
        IDLE
        RICE_FARMER
        WOODCUTTER
        BLACKSMITH
        ARTISAN
        TRADER
        SAMURAI
        MONK
    }
    class Building {
        <<interface>>
        getType() BuildingType
        getTimberCost() int
        getJobSlots() Map~Role,Integer~
        getPlacementRules() List~PlacementRule~
    }
    class AbstractBuilding {
        <<abstract>>
        BuildingType type
        int timberCost
        int housingCapacity
        Map~Role,Integer~ jobSlots
        List~PlacementRule~ placementRules
    }
    class PolicyStrategy {
        <<interface>>
        getType() PolicyType
        productionMultiplier(ResourceType, BuildingType) double
        riceConsumptionMultiplier(Role) double
        toolsConsumptionMultiplier(Role) double
        luxuryConsumptionMultiplier(Role) double
        protectionMultiplier() double
    }

    Village *-- Grid
    Village *-- ResourceStock
    Village *-- VillageParameters
    Village *-- Villager
    Village *-- PolicyManager
    PolicyManager --> PolicyStrategy
    Grid *-- Cell
    Cell *-- Position
    Cell o-- Building
    Cell o-- NaturalFeature
    Villager --> Role
    Villager --> HousingStatus
    Building <|.. AbstractBuilding
    AbstractBuilding <|-- Dwelling
    AbstractBuilding <|-- RiceFarm
    AbstractBuilding <|-- RicePaddy
    AbstractBuilding <|-- WoodcuttersHut
    AbstractBuilding <|-- Mine
    AbstractBuilding <|-- Smithy
    AbstractBuilding <|-- Workshop
    AbstractBuilding <|-- Market
    AbstractBuilding <|-- GuardPost
    AbstractBuilding <|-- Temple
```

**Buildings (responsibility / job slots / key rule):** `Dwelling` (housing 4) · `RiceFarm` (Rice Farmer ×3) · `RicePaddy` (rice; needs a Rice Farm in range + ≥1 farmer) · `WoodcuttersHut` (timber, Woodcutter ×3; **must be within range 1 of a Forest**) · `Mine` (lets adjacent Smithy/Workshop produce) · `Smithy` (tools, Blacksmith ×2; needs Mine in range) · `Workshop` (luxury goods, Artisan ×2; needs Mine in range) · `Market` (exchange, Trader ×2; shared, 10-tick cooldown) · `GuardPost` (protection, Samurai ×2) · `Temple` (faith, Monk ×2).

---

## 2. System Sequence Diagrams

These show external interaction between the player and the system, without internal classes.

**2.1 — Create a new village**

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System
    Player->>System: startNewVillage(width, height)
    System-->>Player: villageCreated(initialState, generatedMap)
    Player->>System: viewVillageSummary()
    System-->>Player: villageSummary(resources, parameters, tickNumber)
```

**2.2 — Construct a building**

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System
    Player->>System: requestConstructBuilding(buildingType, position)
    System-->>Player: constructionResult(success/failure, message)
```

**2.3 — Advance one tick**

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System
    Player->>System: advanceTick()
    System-->>Player: tickResult(beforeState, afterState, production, consumption, events)
    Player->>System: viewDashboard()
    System-->>Player: dashboardData(resources, parameters, villagers, activePolicy)
```

**2.4 — Activate a strategy policy**

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System
    Player->>System: activatePolicy(policyType)
    System-->>Player: policyActivationResult(success/failure, activePolicy, cooldown)
```

**2.5 — Save and load**

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System
    Player->>System: saveVillage(slot)
    System-->>Player: saveResult(success/failure, message)
    Player->>System: loadVillage(slot)
    System-->>Player: loadResult(success/failure, restoredSnapshot)
```

---

## 3. Design Class Model

The core is a pure-Java domain reached through `CoreGameFacade` → `GameController`, which delegates to specialized services and returns immutable view models (`VillageSnapshot`, `CellViewModel`, `DashboardViewModel`). The libGDX presentation never touches mutable domain objects.

**3.1 — Application boundary and core**

```mermaid
classDiagram
    class CoreGameFacade {
        +startNewVillage(width, height) VillageSnapshot
        +applyStarterBuildings() VillageSnapshot
        +constructBuilding(type, position) PlacementResult
        +demolishBuilding(position) PlacementResult
        +advanceTick() TickResult
        +activatePolicy(policyType) PolicyActivationResult
        +requestTrade(TradeRequest) TradeResult
        +inspectCell(position) CellViewModel
        +getCurrentSnapshot() VillageSnapshot
        +getDashboard() DashboardViewModel
        +saveVillage(slot) SaveResult
        +loadVillage(slot) LoadResult
    }
    class GameController {
        +startNewVillage(width, height)
        +constructBuilding(type, position)
        +demolishBuilding(position)
        +advanceTick()
        +activatePolicy(policyType)
        +getDashboard()
        +saveVillage(path); +loadVillage(path)
    }
    class SimulationEngine { +advanceTick(Village) TickResult }
    class TickProcessor { +process(Village) TickResult }
    class Village {
        -Grid grid
        -ResourceStock resources
        -VillageParameters parameters
        -List~Villager~ villagers
        -PolicyManager policyManager
        +getResources(); +getParameters()
    }
    class TickResult {
        <<record>>
        VillageSnapshot beforeState
        VillageSnapshot afterState
        ResourceViewModel producedResources
        ResourceViewModel consumedResources
        int births; int deaths
        List~String~ policyEffects
        List~String~ shortagePenalties
        List~EventReport~ randomEventReports
    }
    class VillageSnapshot {
        <<immutable>>
        int width; int height
        List~CellViewModel~ cells
        DashboardViewModel dashboard
    }
    class CellViewModel {
        <<immutable>>
        Position position
        NaturalFeature feature
        BuildingViewModel building
    }
    class DashboardViewModel {
        <<immutable>>
        ResourceViewModel resources
        PopulationViewModel population
        PolicyViewModel policy
        EventLogViewModel eventLog
    }
    class VillagePersistenceService { +save(Village, Path); +load(Path, GameConfig) Village }

    CoreGameFacade --> GameController
    GameController --> ConstructionService
    GameController --> SimulationEngine
    GameController --> TradeService
    GameController --> VillagePersistenceService
    SimulationEngine --> TickProcessor
    CoreGameFacade --> VillageSnapshot
    TickProcessor --> TickResult
    VillageSnapshot *-- CellViewModel
    VillageSnapshot *-- DashboardViewModel
    CellViewModel *-- BuildingViewModel
```

**3.2 — Building hierarchy (data-driven).** Each building declares timber cost, housing capacity, job slots (`Map<Role,Integer>`), and placement rules; production/consumption are computed by the tick services from these declarations — there are no per-building `produce`/`consume` methods.

```mermaid
classDiagram
    class Building {
        <<interface>>
        +getType() BuildingType
        +getTimberCost() int
        +getDisplayName() String
        +getHousingCapacity() int
        +getJobSlots() Map~Role,Integer~
        +getPlacementRules() List~PlacementRule~
    }
    class AbstractBuilding {
        <<abstract>>
        -BuildingType type
        -int timberCost
        -int housingCapacity
        -Map~Role,Integer~ jobSlots
        -List~PlacementRule~ placementRules
    }
    Building <|.. AbstractBuilding
    AbstractBuilding <|-- Dwelling
    AbstractBuilding <|-- RiceFarm
    AbstractBuilding <|-- RicePaddy
    AbstractBuilding <|-- WoodcuttersHut
    AbstractBuilding <|-- Mine
    AbstractBuilding <|-- Smithy
    AbstractBuilding <|-- Workshop
    AbstractBuilding <|-- Market
    AbstractBuilding <|-- GuardPost
    AbstractBuilding <|-- Temple
```

**3.3 — Strategy policies.** One policy is active at a time, managed by `PolicyManager` with duration + cooldown.

```mermaid
classDiagram
    class PolicyStrategy {
        <<interface>>
        +getType() PolicyType
        +getDisplayName() String
        +productionMultiplier(ResourceType, BuildingType) double
        +riceConsumptionMultiplier(Role) double
        +toolsConsumptionMultiplier(Role) double
        +luxuryConsumptionMultiplier(Role) double
        +protectionMultiplier() double
    }
    class PolicyManager {
        -PolicyStrategy activePolicy
        -int activeRemainingTicks
        -Map~PolicyType,Integer~ cooldowns
        +activate(PolicyType, GameConfig) PolicyActivation
        +advanceTick(GameConfig) List~String~
        +isPolicyActive() boolean
        +getActivePolicy() PolicyStrategy
        +getCooldown(PolicyType) int
    }
    PolicyStrategy <|.. NoPolicy
    PolicyStrategy <|.. AgriculturalExpansionPolicy
    PolicyStrategy <|.. MilitaryProtectionPolicy
    PolicyStrategy <|.. CraftsmenProductionPolicy
    PolicyManager --> PolicyStrategy
```

| Policy | Production effect | Consumption / cost effect |
|---|---|---|
| `AgriculturalExpansionPolicy` | Rice Paddy ×1.5 | Agriculture tool consumption ×1.5 |
| `MilitaryProtectionPolicy` | Samurai protection ×1.5 | Samurai consume ×1.5 Tools + Luxury |
| `CraftsmenProductionPolicy` | Timber/Tools/Luxury ×1.5 | Craftsmen consume ×1.5 Rice |

**3.4 — libGDX / core boundary.** The presentation depends on the core only through the facade and immutable view models; the core never imports `com.badlogic.gdx.*`.

```mermaid
classDiagram
    namespace Pure_Java_Core {
        class CoreGameFacade
        class GameController
        class VillageSnapshot
        class DashboardViewModel
        class SimulationEngine
        class Village
    }
    namespace LibGDX_Presentation {
        class VillageScreen
        class WorldRenderer
        class DashboardHud
        class GameInputProcessor
    }
    VillageScreen --> CoreGameFacade
    WorldRenderer --> VillageSnapshot
    DashboardHud --> DashboardViewModel
    DashboardHud --> CoreGameFacade
    GameInputProcessor --> CoreGameFacade
    CoreGameFacade --> GameController
    GameController --> SimulationEngine
    SimulationEngine --> Village
```

---

## 4. Internal Sequence Diagrams

The most significant internal object collaborations.

**4.1 — Construct a building**

```mermaid
sequenceDiagram
    participant UI as libGDX HUD/Input
    participant C as CoreGameFacade
    participant GC as GameController
    participant CS as ConstructionService
    participant BF as BuildingFactory
    participant Val as CompositePlacementValidator
    participant Cost as ProgressiveCostCalculator
    participant RS as ResourceStock
    participant G as Grid
    participant HS as HousingService
    participant PC as VillageParameterCalculator

    UI->>C: constructBuilding(type, position)
    C->>GC: constructBuilding(type, position)
    GC->>CS: constructBuilding(village, type, position)
    CS->>BF: create(type)
    BF-->>CS: building
    CS->>Val: validate(village, building, position)
    Val-->>CS: PlacementCheck (timber? cell empty? inside grid? rules?)
    alt valid placement
        CS->>Cost: scaledCost(type, existingCount, baseCost)
        Cost-->>CS: timber cost
        CS->>RS: consume(TIMBER, cost)
        CS->>G: placeBuilding(building, position)
        CS->>HS: assignHousing(village)
        CS->>PC: recalculate(village)
        CS-->>GC: PlacementResult(success, before, after)
    else invalid
        CS-->>GC: PlacementResult(failure, reason)
    end
    GC-->>C: PlacementResult
    C-->>UI: result + updated snapshot
```

**4.2 — Advance one tick.** Orchestrated by `SimulationEngine`/`TickProcessor`: snapshot *before* → advance counter, reset build quota, decrement market cooldown → update policy duration/cooldown → validate building prerequisites → assign **one** idle villager (weighted by free slots) → produce → consume → apply shortages/penalties → luxury-deprivation desertion → recalculate parameters → births/deaths → recalculate again if population/desertion changed → random events → build `TickResult`.

```mermaid
sequenceDiagram
    participant C as CoreGameFacade
    participant E as SimulationEngine
    participant TP as TickProcessor
    participant VM as SnapshotMapper
    participant PM as PolicyManager
    participant JR as JobAssignmentService
    participant PR as ProductionService
    participant CS as ConsumptionService
    participant SH as ShortageService
    participant PC as VillageParameterCalculator
    participant BD as BirthDeathService
    participant REM as RandomEventManager

    C->>E: advanceTick(village)
    E->>TP: process(village)
    TP->>VM: toSnapshot(village)  %% beforeState
    TP->>TP: advanceTickCounter, resetBuildsThisTick, decrementMarketCooldown
    TP->>PM: advanceTick(config)
    TP->>TP: validateBuildingRules(village)
    TP->>JR: assignOneIdleVillager(village)
    TP->>PR: produce(village)
    PR->>PM: getActivePolicy()
    PM-->>PR: PolicyStrategy
    PR-->>TP: produced resources
    TP->>CS: consume(village)
    CS-->>TP: ConsumptionResult
    TP->>SH: applyShortages(consumption)
    TP->>TP: updateLuxuryDesertion(village)
    TP->>PC: recalculate(village)
    TP->>BD: process(village)
    alt births/deaths/desertions changed population
        TP->>PC: recalculate(village)
    end
    TP->>REM: evaluateFull(village)
    REM-->>TP: List~EventReport~
    TP->>VM: toSnapshot(village)  %% afterState
    TP-->>E: TickResult
    E-->>C: TickResult
```

**4.3 — Activate a strategy policy**

```mermaid
sequenceDiagram
    participant UI as PolicyPanel
    participant C as CoreGameFacade
    participant GC as GameController
    participant PM as PolicyManager
    participant PF as PolicyFactory

    UI->>C: activatePolicy(policyType)
    C->>GC: activatePolicy(policyType)
    GC->>PM: activate(policyType, config)
    alt no active policy and not on cooldown
        PM->>PF: create(policyType)
        PF-->>PM: PolicyStrategy
        PM->>PM: set activePolicy + activeRemainingTicks
        PM-->>GC: PolicyActivation(success)
    else already active / on cooldown
        PM-->>GC: PolicyActivation(failure)
    end
    GC-->>C: PolicyActivationResult(success, activeType, dashboard)
    C-->>UI: PolicyActivationResult + updated dashboard
```

**4.4 — Save and load**

```mermaid
sequenceDiagram
    participant C as CoreGameFacade
    participant GC as GameController
    participant P as VillagePersistenceService
    participant M as VillageMapper
    participant FS as FileSystem

    C->>GC: saveVillage(slot)
    GC->>P: save(village, path)
    P->>M: toDTO(village)
    M-->>P: VillageDTO
    P->>FS: write JSON file
    P-->>GC: saved
    GC-->>C: SaveResult

    C->>GC: loadVillage(slot)
    GC->>P: load(path, config)
    P->>FS: read JSON file
    FS-->>P: json data
    P->>M: fromDTO(VillageDTO, config)
    M-->>P: Village
    P-->>GC: Village
    GC-->>C: LoadResult
```
