# DaimyoSimulator — Design Document

**Project type:** Java rule-based simulation engine inspired by SimCity Lite — Ancient Japan village management (~year 1200).

**Main objective:** the player develops and balances a village by placing buildings, letting the simulation assign villagers to the job slots those buildings create, managing resources, activating one strategy policy at a time, and advancing the simulation through ticks.

The core simulation is pure Java (no libGDX), so it is usable from unit tests, persistence, or any UI. The libGDX module renders immutable snapshots and sends commands through `CoreGameFacade`. Diagrams use **Mermaid**, rendered directly by GitHub.

---

## 1. Domain Model

DaimyoSimulator is centered on a `Village` (aggregate root) holding a logical `Grid`, the current `VillageState` (resources + parameters), the `Villager` list, the active policy, and the tick counter. The player does not control villagers directly: they construct buildings, and the simulation assigns idle villagers to the job slots created by those buildings.

Main concepts:

- **Grid / Cell / Position** — a rectangular logical map (e.g. 20×20); each cell holds at most one building or one natural feature.
- **Natural Feature** — `FOREST`, generated randomly at map creation; required near Woodcutter's Huts.
- **Villager / Role** — a person who is unhoused, idle, or employed (Rice Farmer, Woodcutter, Blacksmith, Artisan, Trader, Samurai, Monk).
- **Building** — placed by the player using timber; provides housing, job slots, production, exchange, or bonuses.
- **Resources** — Rice, Timber, Tools, Luxury Goods.
- **Village Parameters** — Happiness, Protection, Food, Faith, Housing, Craftsmanship; recalculated every tick. Happiness is derived from the other parameters by `HappinessCalculator`, keeping it testable and separate from `VillageState`.
- **Strategy Policy** — one active temporary policy modifying production, protection, or consumption.
- **Random Event** — condition- or probability-based, triggered during ticks.

```mermaid
classDiagram
    class Village {
        Grid grid
        VillageState state
        List~Villager~ villagers
        PolicyManager policyManager
        int tickNumber
        construct(Building, Position)
        getState()
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
    class VillageState {
        ResourceStock resources
        VillageParameters parameters
        int birthProgress
    }
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
    class Villager { Role role; HousingStatus housingStatus }
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
        <<abstract>>
        BuildingType type
        int timberCost
        getPlacementRules()
    }
    class PolicyStrategy {
        <<interface>>
        modifyProduction(BuildingEffect)
        modifyConsumption(ConsumptionPlan)
        modifyParameters(VillageParameters)
    }

    Village *-- Grid
    Village *-- VillageState
    Village *-- Villager
    Village *-- PolicyManager
    PolicyManager --> PolicyStrategy
    Grid *-- Cell
    Cell *-- Position
    Cell o-- Building
    Cell o-- NaturalFeature
    VillageState *-- ResourceStock
    VillageState *-- VillageParameters
    Villager --> Role
    Building <|-- Dwelling
    Building <|-- RiceFarm
    Building <|-- RicePaddy
    Building <|-- WoodcuttersHut
    Building <|-- Mine
    Building <|-- Smithy
    Building <|-- Workshop
    Building <|-- Market
    Building <|-- GuardPost
    Building <|-- Temple
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
    System-->>Player: loadResult(success/failure, restoredVillageState)
```

---

## 3. Design Class Model

The core is a pure-Java domain reached through `CoreGameFacade` → `GameController`, which delegates to specialized services and returns immutable view models (`VillageSnapshot`, `CellViewModel`, `DashboardViewModel`). The libGDX presentation never touches mutable domain objects.

**3.1 — Application boundary and core**

```mermaid
classDiagram
    class CoreGameFacade {
        +startNewVillage(width, height) VillageSnapshot
        +constructBuilding(type, position) PlacementResult
        +demolishBuilding(position) PlacementResult
        +advanceTick() TickResult
        +activatePolicy(policyType) PolicyActivationResult
        +requestTrade(TradeRequest) TradeResult
        +inspectCell(position) CellViewModel
        +getCurrentSnapshot() VillageSnapshot
        +saveVillage(slot) SaveResult
        +loadVillage(slot) LoadResult
    }
    class GameController {
        +constructBuilding(type, position)
        +advanceTick()
        +activatePolicy(policyType)
        +save(path); +load(path)
    }
    class SimulationEngine { +advanceTick(Village) TickResult }
    class Village {
        -Grid grid
        -VillageState state
        -List~Villager~ villagers
        -PolicyManager policyManager
        +construct(Building, Position)
    }
    class TickResult {
        VillageState beforeState
        VillageState afterState
        List~BuildingEffect~ buildingEffects
        List~EventEffect~ eventEffects
        String activePolicyName
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
    class VillagePersistenceService { +save(Village, Path); +load(Path) Village }

    CoreGameFacade --> GameController
    GameController --> ConstructionService
    GameController --> SimulationEngine
    GameController --> TradeService
    GameController --> VillagePersistenceService
    CoreGameFacade --> VillageSnapshot
    SimulationEngine --> TickResult
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
        +getName() String
        +modifyProduction(BuildingEffect) BuildingEffect
        +modifyConsumption(ConsumptionPlan) ConsumptionPlan
        +modifyProtection(int) int
    }
    class PolicyManager {
        -PolicyStrategy activePolicy
        -int remainingTicks
        -int cooldownTicks
        +activate(PolicyType)
        +canActivate(PolicyType) boolean
        +updateDurationAndCooldown()
        +getActivePolicy() PolicyStrategy
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
    participant CS as ConstructionService
    participant BF as BuildingFactory
    participant Cost as ProgressiveCostCalculator
    participant Val as CompositePlacementValidator
    participant RS as ResourceStock
    participant G as Grid

    UI->>C: constructBuilding(type, position)
    C->>CS: constructBuilding(village, type, position)
    CS->>BF: create(type)
    BF-->>CS: building
    CS->>Cost: scaledCost(type, existingCount, baseCost)
    Cost-->>CS: timber cost
    CS->>Val: validate(village, building, position)
    Val->>RS: enough timber? cell empty? inside grid? building rules?
    Val-->>CS: PlacementCheck
    alt valid placement
        CS->>RS: consume(TIMBER, cost)
        CS->>G: placeBuilding(building, position)
        CS-->>C: PlacementResult(success)
    else invalid
        CS-->>C: PlacementResult(failure, reason)
    end
    C-->>UI: result + updated snapshot
```

**4.2 — Advance one tick.** Orchestrated by `SimulationEngine`/`TickProcessor`: snapshot *before* → advance counter, reset build quota, decrement market cooldown → update policy duration/cooldown → assign **one** idle villager (weighted by free slots) → produce → consume → apply shortages/penalties → luxury-deprivation desertion → recalculate parameters → births/deaths → random events → build `TickResult`.

```mermaid
sequenceDiagram
    participant C as CoreGameFacade
    participant E as SimulationEngine
    participant PM as PolicyManager
    participant JR as JobAssignmentService
    participant PR as ProductionService
    participant CS as ConsumptionService
    participant RC as ResourceStock
    participant PC as ParameterCalculator
    participant BD as BirthDeathService
    participant REM as RandomEventManager
    participant VM as SnapshotMapper

    C->>E: advanceTick(village)
    E->>E: snapshot beforeState
    E->>PM: updateDurationAndCooldown()
    E->>JR: assignIdleVillagers(village)
    E->>PR: produce(village, activePolicy)
    PR->>PM: getActivePolicy()
    PM-->>PR: PolicyStrategy
    PR->>RC: add produced resources
    E->>CS: consume(village, activePolicy)
    CS->>RC: consume required resources
    E->>PC: recalculate parameters
    E->>BD: processBirthsAndDeaths(village)
    E->>REM: evaluateEvents(village)
    REM-->>E: event effects
    E->>E: build TickResult
    E-->>C: TickResult
    C->>VM: toVillageSnapshot(village, tickResult)
    VM-->>C: VillageSnapshot
```

**4.3 — Activate a strategy policy**

```mermaid
sequenceDiagram
    participant UI as PolicyPanel
    participant C as CoreGameFacade
    participant PM as PolicyManager
    participant PF as PolicyFactory

    UI->>C: activatePolicy(policyType)
    C->>PM: canActivate(policyType)
    alt can activate
        PM->>PF: create(policyType)
        PF-->>PM: PolicyStrategy
        PM->>PM: set activePolicy + duration
        PM-->>C: success
    else unavailable / on cooldown
        PM-->>C: failure
    end
    C-->>UI: PolicyActivationResult + updated dashboard
```

**4.4 — Save and load**

```mermaid
sequenceDiagram
    participant C as CoreGameFacade
    participant P as VillagePersistenceService
    participant M as VillageMapper
    participant FS as FileSystem

    C->>P: save(village, slot)
    P->>M: toDTO(village)
    M-->>P: VillageDTO
    P->>FS: write JSON file
    P-->>C: SaveResult

    C->>P: load(slot)
    P->>FS: read JSON file
    FS-->>P: json data
    P->>M: fromDTO(json data)
    M-->>P: Village
    P-->>C: loaded Village
```
