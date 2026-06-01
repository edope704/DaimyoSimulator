# DaimyoSimulator — Design Document

**Project type:** Java rule-based simulation engine inspired by SimCity Lite  
**Scenario:** Ancient Japan village management, around year 1200  
**Main objective:** The player develops and balances a village by placing buildings, assigning villagers indirectly through the available job slots, managing resources, activating one strategy policy at a time, and advancing the simulation through ticks.

This document is written for the GitHub design deliverable. The diagrams use **Mermaid**, which is rendered directly by GitHub Markdown.

---

## 1. Domain Model

### 1.1 Domain overview

DaimyoSimulator is centered on a `Village`. A village contains a logical grid map, the current village state, the available villagers, placed buildings, resources, active policies, and event history. The player does not manually control each villager. Instead, the player constructs buildings, and the simulation assigns available villagers to roles according to the job slots created by those buildings.

The domain is intentionally independent from the graphical presentation. The same core simulation must be usable from unit tests, persistence services, or a future alternative UI without importing libGDX.

The main domain concepts are:

- **Village**: aggregate root of the simulation.
- **Grid**: a rectangular logical map, for example 20x20 cells.
- **Cell**: one map position. It can contain one building or one natural feature.
- **Natural Feature**: forest is generated randomly during map creation and is required by Woodcutter's Huts.
- **Villager**: one person living in the village. A villager can be idle, unhoused, or employed.
- **Role**: the current role assigned to a villager, such as Rice Farmer, Woodcutter, Blacksmith, Artisan, Trader, Samurai, or Monk.
- **Building**: construction placed by the player using timber. Buildings create housing capacity, job slots, production, exchange, or parameter bonuses.
- **Resources**: Rice, Timber, Tools, and Luxury Goods.
- **Village Parameters**: Happiness, Protection, Food, Faith, Housing, and Craftsmanship.
- **Strategy Policy**: one active temporary policy that modifies production, protection, or consumption.
- **Random Event**: condition-based or probability-based event triggered during ticks.

### 1.2 Domain model diagram

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

    class Position {
        int x
        int y
    }

    class NaturalFeature {
        <<enumeration>>
        FOREST
    }

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

    class Villager {
        Role role
        HousingStatus housingStatus
    }

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

### 1.3 Buildings and responsibilities

| Building | Main responsibility | Job slots / role | Rule enforcement |
|---|---|---|---|
| `Dwelling` | Provides housing capacity | none | Required to house villagers born in the village |
| `RiceFarm` | Holds farmers | Rice Farmer | Needed near Rice Paddies |
| `RicePaddy` | Produces rice | indirectly uses farmers from Rice Farm | Produces only if a Rice Farm is nearby and at least one farmer exists |
| `WoodcuttersHut` | Produces timber | Woodcutter | Must be near forest |
| `Mine` | Unlocks Smithy and Workshop | optional / none | At least one Mine must exist before Smithy or Workshop can be built |
| `Smithy` | Produces tools | Blacksmith | Requires Mine |
| `Workshop` | Produces luxury goods | Artisan | Requires Mine |
| `Market` | Exchanges resources | Trader | Exchange amount and speed depend on traders |
| `GuardPost` | Increases protection | Samurai | Consumes tools/luxury goods under policy effects |
| `Temple` | Increases faith | Monk | Supports faith-based events and happiness |

### 1.4 Resource and parameter model

The central resources are:

- **Rice**: consumed every tick by living villagers. Produced by Rice Paddies.
- **Timber**: consumed when constructing buildings. Produced by Woodcutter's Huts.
- **Tools**: produced by Smithies. Consumed by rice farmers and samurai, especially under policies.
- **Luxury Goods**: produced by Workshops. Consumed by monks and samurai, especially under policies.

The main village parameters are recalculated each tick:

- **Happiness**: global indicator based on the whole village condition.
- **Protection**: based mainly on the ratio between Samurai and villagers.
- **Food**: based on rice stock and expected rice consumption.
- **Faith**: based on monks and temples compared with total population.
- **Housing**: based on housed villagers compared with total villagers.
- **Craftsmanship**: based on available tools, craftsmen, smithies, and workshops.

Happiness is not calculated manually by the player. It is derived from the other parameters by `HappinessCalculator`, so it stays testable and separated from `VillageState`.

---

## 2. System Sequence Diagrams

The following diagrams are **system sequence diagrams**. They show the external interaction between the player and the DaimyoSimulator system without exposing internal classes.

### 2.1 SSD — Create a new village

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System

    Player->>System: startNewVillage(width, height)
    System-->>Player: villageCreated(initialState, generatedMap)
    Player->>System: viewVillageSummary()
    System-->>Player: villageSummary(resources, parameters, tickNumber)
```

### 2.2 SSD — Construct a building

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System

    Player->>System: requestConstructBuilding(buildingType, position)
    System-->>Player: constructionResult(success/failure, message)
    Player->>System: viewCell(position)
    System-->>Player: cellStatus(position, buildingOrFeature)
```

### 2.3 SSD — Advance one tick

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System

    Player->>System: advanceTick()
    System-->>Player: tickResult(beforeState, afterState, production, consumption, events)
    Player->>System: viewDashboard()
    System-->>Player: dashboardData(resources, parameters, villagers, activePolicy)
```

### 2.4 SSD — Activate a strategy policy

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System

    Player->>System: activatePolicy(policyType)
    System-->>Player: policyActivationResult(success/failure, activePolicy, cooldown)
    Player->>System: advanceTick()
    System-->>Player: tickResult(policyEffectsApplied)
```

### 2.5 SSD — Save and load village

```mermaid
sequenceDiagram
    actor Player
    participant System as DaimyoSimulator System

    Player->>System: saveVillage(filePath)
    System-->>Player: saveResult(success/failure, message)
    Player->>System: loadVillage(filePath)
    System-->>Player: loadResult(success/failure, restoredVillageState)
```

---

## 3. Architectural Organization

### 3.1 Three responsibility split

DaimyoSimulator is separated into three responsibilities:

| Responsibility | Technology | Owns | Must not own |
|---|---|---|---|
| Core Game Logic | Pure Java | Village simulation, logical grid, buildings, placement rules, villagers/jobs, economy/resources, policies, random events, save/load, tick engine | libGDX imports, rendering, Scene2D widgets, camera logic |
| Game World Renderer | libGDX | Drawing the village map, tiles, buildings, forests, animations, SpriteBatch, OrthographicCamera, visual mapping from snapshots | Simulation rules, resource formulas, placement validation, persistence |
| Game UI / HUD | libGDX Scene2D UI | Build buttons, resources, population, parameters, selected building panel, pause/speed/next-tick, policy buttons, event log, menus | Direct mutation of `Village`, `Cell`, `Building`, `ResourceStock`, or `Villager` |

The UI and renderer communicate with the core only through `GameController`, `CoreGameFacade`, application services, DTOs, immutable snapshots, or view models.

### 3.2 Maven multi-module architecture

```text
daimyosimulator/
├── pom.xml
├── src/
│   ├── core/
│   │   ├── pom.xml
│   │   ├── main/...
│   │   └── test/...
│   ├── libgdx/
│   │   ├── pom.xml
│   │   ├── main/...
│   │   ├── main/resources/assets/...
│   │   └── test/...
│   └── desktop/
│       ├── pom.xml
│       └── main/DesktopLauncher.java
```

Dependencies flow in one direction:

```text
desktop
        ↓
libgdx
        ↓
core
```

The forbidden direction is:

```text
core ❌ must not depend on libgdx
```

The core module must compile and run JUnit tests without any `com.badlogic.gdx.*` imports.

### 3.3 Package architecture

#### Core module packages

```text
core
├── application/
│   ├── CoreGameFacade.java
│   ├── GameController.java
│   ├── VillageInitializer.java
│   ├── BuildCommand.java
│   ├── TickCommand.java
│   ├── PlacementResult.java
│   ├── PolicyActivationResult.java
│   └── TickResult.java
├── application/view/
│   ├── VillageSnapshot.java
│   ├── CellViewModel.java
│   ├── BuildingViewModel.java
│   ├── DashboardViewModel.java
│   ├── ResourceViewModel.java
│   ├── PopulationViewModel.java
│   ├── PolicyViewModel.java
│   └── EventLogViewModel.java
├── domain/
│   ├── village/
│   ├── grid/
│   ├── building/
│   ├── villager/
│   ├── resource/
│   ├── policy/
│   ├── rule/
│   └── event/
├── engine/
│   ├── SimulationEngine.java
│   ├── TickProcessor.java
│   ├── TickContext.java
│   ├── JobAssignmentService.java
│   ├── ProductionService.java
│   ├── ConsumptionService.java
│   ├── ShortageService.java
│   ├── BirthDeathService.java
│   ├── VillageParameterCalculator.java
│   └── HappinessCalculator.java
├── factory/
│   ├── BuildingFactory.java
│   └── PolicyFactory.java
└── persistence/
    ├── VillagePersistenceService.java
    ├── VillageMapper.java
    ├── dto/
    └── json/
```

#### libGDX module packages

```text
gdx
├── app/
│   └── DaimyoSimulatorGame.java
├── screen/
│   ├── LoadingScreen.java
│   ├── MainMenuScreen.java
│   └── VillageScreen.java
├── render/
│   ├── WorldRenderer.java
│   ├── TileRenderer.java
│   ├── BuildingRenderer.java
│   ├── NaturalFeatureRenderer.java
│   ├── AnimationRenderer.java
│   ├── GridOverlayRenderer.java
│   └── RenderConstants.java
├── ui/
│   ├── HudStageFactory.java
│   ├── DashboardHud.java
│   ├── BuildMenu.java
│   ├── ResourcePanel.java
│   ├── PopulationPanel.java
│   ├── VillageParameterPanel.java
│   ├── SelectedBuildingPanel.java
│   ├── SpeedControlPanel.java
│   ├── PolicyPanel.java
│   ├── EventLogPanel.java
│   └── MenuOverlay.java
├── input/
│   ├── GameInputProcessor.java
│   ├── CameraController.java
│   ├── BuildModeState.java
│   ├── ScreenToGridMapper.java
│   └── InputCommandRouter.java
├── asset/
│   ├── GameAssetManager.java
│   ├── AssetPaths.java
│   ├── BuildingSpriteRegistry.java
│   ├── TileSpriteRegistry.java
│   ├── IconRegistry.java
│   └── MissingAssetFallback.java
└── adapter/
    ├── SnapshotToRenderModelAdapter.java
    ├── CellRenderModel.java
    ├── BuildingRenderModel.java
    └── HudViewModelAdapter.java
```

#### desktop module package

```text
desktop
└── DesktopLauncher.java
```

---

## 4. Design Class Model

### 4.1 Controller/facade and core class diagram

```mermaid
classDiagram
    class CoreGameFacade {
        +startNewVillage(width, height) VillageSnapshot
        +placeBuilding(type, position) PlacementResult
        +advanceTick() TickResult
        +activatePolicy(policyType) PolicyActivationResult
        +inspectCell(position) CellViewModel
        +getSnapshot() VillageSnapshot
        +save(path) SaveResult
        +load(path) VillageSnapshot
    }

    class GameController {
        +startNewVillage(width, height)
        +constructBuilding(type, position)
        +advanceTick()
        +activatePolicy(policyType)
        +save(path)
        +load(path)
    }

    class SimulationEngine {
        +advanceTick(Village) TickResult
    }

    class Village {
        -Grid grid
        -VillageState state
        -List~Villager~ villagers
        -PolicyManager policyManager
        +construct(Building, Position)
        +getState()
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
        int width
        int height
        List~CellViewModel~ cells
        DashboardViewModel dashboard
    }

    class CellViewModel {
        <<immutable>>
        Position position
        NaturalFeature feature
        BuildingViewModel building
    }

    class BuildingViewModel {
        <<immutable>>
        BuildingType type
        int workers
        int jobSlots
        String productionSummary
    }

    class DashboardViewModel {
        <<immutable>>
        ResourceViewModel resources
        PopulationViewModel population
        PolicyViewModel policy
        EventLogViewModel eventLog
    }

    class VillagePersistenceService {
        +save(Village, Path)
        +load(Path) Village
    }

    CoreGameFacade --> GameController
    GameController --> PlacementService
    GameController --> SimulationEngine
    GameController --> VillagePersistenceService
    CoreGameFacade --> VillageSnapshot
    CoreGameFacade --> CellViewModel
    CoreGameFacade --> DashboardViewModel
    SimulationEngine --> TickResult
    VillageSnapshot *-- CellViewModel
    VillageSnapshot *-- DashboardViewModel
    CellViewModel *-- BuildingViewModel
```

### 4.2 Building hierarchy and interfaces

```mermaid
classDiagram
    class Building {
        <<abstract>>
        -BuildingType type
        -int timberCost
        +getType() BuildingType
        +getTimberCost() int
        +getPlacementRules() List~PlacementRule~
    }

    class JobProvider {
        <<interface>>
        +getRole() Role
        +getJobSlots() int
    }

    class ResourceProducer {
        <<interface>>
        +produce(Village, TickContext) BuildingEffect
    }

    class ResourceConsumer {
        <<interface>>
        +consume(Village, TickContext) ConsumptionPlan
    }

    class Dwelling {
        +getHousingCapacity() int
    }

    class RiceFarm
    class RicePaddy
    class WoodcuttersHut
    class Mine
    class Smithy
    class Workshop
    class Market
    class GuardPost
    class Temple

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

    JobProvider <|.. RiceFarm
    JobProvider <|.. WoodcuttersHut
    JobProvider <|.. Smithy
    JobProvider <|.. Workshop
    JobProvider <|.. Market
    JobProvider <|.. GuardPost
    JobProvider <|.. Temple

    ResourceProducer <|.. RicePaddy
    ResourceProducer <|.. WoodcuttersHut
    ResourceProducer <|.. Smithy
    ResourceProducer <|.. Workshop

    ResourceConsumer <|.. RicePaddy
    ResourceConsumer <|.. GuardPost
    ResourceConsumer <|.. Temple
```

### 4.3 Strategy policies

```mermaid
classDiagram
    class PolicyStrategy {
        <<interface>>
        +getName() String
        +modifyProduction(BuildingEffect) BuildingEffect
        +modifyConsumption(ConsumptionPlan) ConsumptionPlan
        +modifyProtection(int) int
    }

    class NoPolicy
    class AgriculturalExpansionPolicy
    class MilitaryProtectionPolicy
    class CraftsmenProductionPolicy

    PolicyStrategy <|.. NoPolicy
    PolicyStrategy <|.. AgriculturalExpansionPolicy
    PolicyStrategy <|.. MilitaryProtectionPolicy
    PolicyStrategy <|.. CraftsmenProductionPolicy

    class PolicyManager {
        -PolicyStrategy activePolicy
        -int remainingTicks
        -int cooldownTicks
        +activate(PolicyType)
        +canActivate(PolicyType) boolean
        +updateDurationAndCooldown()
        +getActivePolicy() PolicyStrategy
    }

    PolicyManager --> PolicyStrategy
```

Policy behavior:

| Policy | Production effect | Consumption / cost effect |
|---|---|---|
| `AgriculturalExpansionPolicy` | Rice Paddy production x1.5 | Tool consumption by agriculture x1.5 |
| `MilitaryProtectionPolicy` | Protection value from Samurai x1.5 | Samurai consume x1.5 Tools and Luxury Goods |
| `CraftsmenProductionPolicy` | Timber, Tools, and Luxury Goods production x1.5 | Craftsmen consume x1.5 Rice |

### 4.4 libGDX application structure

```mermaid
classDiagram
    class DaimyoSimulatorGame {
        +create()
        +setScreen(Screen)
        +dispose()
    }

    class LoadingScreen {
        +show()
        +render(delta)
    }

    class VillageScreen {
        -SpriteBatch batch
        -OrthographicCamera camera
        -Stage hudStage
        -WorldRenderer worldRenderer
        -DashboardHud dashboardHud
        -GameInputProcessor inputProcessor
        +render(delta)
        +resize(width, height)
        +dispose()
    }

    class WorldRenderer {
        +render(VillageSnapshot, OrthographicCamera)
    }

    class HudStageFactory {
        +create(controller) Stage
    }

    class GameAssetManager {
        +loadAll()
        +getRegion(assetKey) TextureRegion
        +dispose()
    }

    DaimyoSimulatorGame --> LoadingScreen
    DaimyoSimulatorGame --> VillageScreen
    LoadingScreen --> GameAssetManager
    VillageScreen --> WorldRenderer
    VillageScreen --> HudStageFactory
    VillageScreen --> GameAssetManager
```

### 4.5 WorldRenderer and UI/HUD collaboration

```mermaid
classDiagram
    class WorldRenderer {
        +render(snapshot, camera)
    }

    class TileRenderer {
        +renderTiles(snapshot)
    }

    class NaturalFeatureRenderer {
        +renderFeatures(snapshot)
    }

    class BuildingRenderer {
        +renderBuildings(snapshot)
    }

    class AnimationRenderer {
        +renderAnimations(snapshot, delta)
    }

    class DashboardHud {
        +refresh(DashboardViewModel)
        +showCell(CellViewModel)
        +showMessage(String)
    }

    class BuildMenu {
        +setBuildMode(BuildingType)
    }

    class ResourcePanel {
        +refresh(ResourceViewModel)
    }

    class PopulationPanel {
        +refresh(PopulationViewModel)
    }

    class SelectedBuildingPanel {
        +show(CellViewModel)
    }

    class SpeedControlPanel {
        +pause()
        +resume()
        +nextTick()
        +setSpeed(multiplier)
    }

    class PolicyPanel {
        +activatePolicy(PolicyType)
    }

    WorldRenderer --> TileRenderer
    WorldRenderer --> NaturalFeatureRenderer
    WorldRenderer --> BuildingRenderer
    WorldRenderer --> AnimationRenderer
    DashboardHud --> BuildMenu
    DashboardHud --> ResourcePanel
    DashboardHud --> PopulationPanel
    DashboardHud --> SelectedBuildingPanel
    DashboardHud --> SpeedControlPanel
    DashboardHud --> PolicyPanel
```

### 4.6 Boundary between libGDX and pure Java core

```mermaid
classDiagram
    namespace Pure_Java_Core {
        class CoreGameFacade
        class GameController
        class VillageSnapshot
        class CellViewModel
        class BuildingViewModel
        class DashboardViewModel
        class SimulationEngine
        class Village
    }

    namespace LibGDX_Presentation {
        class VillageScreen
        class WorldRenderer
        class DashboardHud
        class GameInputProcessor
        class CameraController
    }

    VillageScreen --> CoreGameFacade
    WorldRenderer --> VillageSnapshot
    DashboardHud --> DashboardViewModel
    DashboardHud --> CoreGameFacade
    GameInputProcessor --> CoreGameFacade
    GameInputProcessor --> CameraController
    CoreGameFacade --> GameController
    GameController --> SimulationEngine
    SimulationEngine --> Village
```

---

## 5. Internal Sequence Diagrams

The following diagrams show the internal object collaboration for the most significant operations.

### 5.1 Internal sequence — Construct a building in the core

```mermaid
sequenceDiagram
    actor Player
    participant UI as libGDX HUD/Input
    participant C as CoreGameFacade
    participant PS as PlacementService
    participant BF as BuildingFactory
    participant RS as ResourceStock
    participant Rules as PlacementRules
    participant V as Village
    participant G as Grid

    Player->>UI: choose building type and position
    UI->>C: placeBuilding(type, position)
    C->>PS: placeBuilding(village, type, position)
    PS->>BF: create(type)
    BF-->>PS: building
    PS->>G: getCell(position)
    G-->>PS: cell
    PS->>RS: has(TIMBER, building.cost)
    RS-->>PS: true/false
    PS->>Rules: validate(village, building, position)
    Rules-->>PS: valid/invalid
    alt valid placement and enough timber
        PS->>RS: consume(TIMBER, building.cost)
        PS->>V: construct(building, position)
        V->>G: place(building, position)
        PS-->>C: PlacementResult(success)
    else invalid placement
        PS-->>C: PlacementResult(failure, reason)
    end
    C-->>UI: placement result and updated snapshot
```

### 5.2 Internal sequence — Advance one tick in the core

```mermaid
sequenceDiagram
    actor Player
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

    Player->>C: advanceTick()
    C->>E: advanceTick(village)
    E->>E: snapshot beforeState
    E->>PM: updateDurationAndCooldown()
    E->>JR: assignIdleVillagers(village)
    E->>PR: produce(village, activePolicy)
    PR->>PM: getActivePolicy()
    PM-->>PR: PolicyStrategy
    PR->>RC: add produced resources
    E->>CS: consume(village, activePolicy)
    CS->>PM: getActivePolicy()
    PM-->>CS: PolicyStrategy
    CS->>RC: consume required resources
    E->>PC: recalculate parameters
    E->>BD: processBirthsAndDeaths(village)
    E->>REM: evaluateEvents(village)
    REM-->>E: event effects
    E->>E: build TickResult
    E-->>C: TickResult
    C->>VM: toVillageSnapshot(village, tickResult)
    VM-->>C: VillageSnapshot
    C-->>Player: TickResult and updated snapshot
```

Tick order implemented by `SimulationEngine`:

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
12. Build `TickResult` and updated immutable view models.

### 5.3 Internal sequence — Activate a strategy policy

```mermaid
sequenceDiagram
    actor Player
    participant UI as PolicyPanel
    participant C as CoreGameFacade
    participant PM as PolicyManager
    participant PF as PolicyFactory
    participant V as Village

    Player->>UI: select policy
    UI->>C: activatePolicy(policyType)
    C->>PM: canActivate(policyType)
    alt policy can be activated
        PM->>PF: create(policyType)
        PF-->>PM: PolicyStrategy
        PM->>PM: set activePolicy and duration
        PM->>V: record active policy
        PM-->>C: activation success
    else policy is unavailable or on cooldown
        PM-->>C: activation failure
    end
    C-->>UI: PolicyActivationResult and updated dashboard view model
```

### 5.4 Internal sequence — Save and load village

```mermaid
sequenceDiagram
    actor Player
    participant C as CoreGameFacade
    participant P as VillagePersistenceService
    participant M as VillageMapper
    participant FS as FileSystem
    participant V as Village

    Player->>C: save(path)
    C->>P: save(village, path)
    P->>M: toDTO(village)
    M-->>P: VillageDTO
    P->>FS: write JSON file
    FS-->>P: write result
    P-->>C: SaveResult
    C-->>Player: save message

    Player->>C: load(path)
    C->>P: load(path)
    P->>FS: read JSON file
    FS-->>P: json data
    P->>M: fromDTO(json data)
    M-->>P: Village
    P-->>C: loaded Village
    C-->>Player: load message and restored VillageSnapshot
```

### 5.5 Selecting a building from the HUD and placing it on the map

```mermaid
sequenceDiagram
    actor Player
    participant BuildMenu
    participant BuildMode as BuildModeState
    participant Input as GameInputProcessor
    participant Mapper as ScreenToGridMapper
    participant C as CoreGameFacade
    participant HUD as DashboardHud
    participant Screen as VillageScreen

    Player->>BuildMenu: click "Dwelling"
    BuildMenu->>BuildMode: selectedBuilding = DWELLING
    Player->>Input: click map cell
    Input->>Mapper: toGridPosition(screenX, screenY, camera)
    Mapper-->>Input: Position(x, y)
    Input->>C: placeBuilding(DWELLING, position)
    C-->>Input: PlacementResult and VillageSnapshot
    Input->>Screen: setSnapshot(snapshot)
    Screen->>HUD: refresh(snapshot.dashboard)
    alt success
        HUD->>HUD: show "Building placed"
    else failure
        HUD->>HUD: show placement error
    end
```

### 5.6 Clicking a cell and opening the selected building panel

```mermaid
sequenceDiagram
    actor Player
    participant Input as GameInputProcessor
    participant Mapper as ScreenToGridMapper
    participant C as CoreGameFacade
    participant HUD as DashboardHud
    participant Panel as SelectedBuildingPanel

    Player->>Input: click map cell
    Input->>Mapper: toGridPosition(screenX, screenY, camera)
    Mapper-->>Input: Position
    Input->>C: inspectCell(position)
    C-->>Input: CellViewModel
    Input->>HUD: showCell(cellViewModel)
    HUD->>Panel: display building/feature/empty cell details
```

### 5.7 Pressing next tick / pause / speed controls

```mermaid
sequenceDiagram
    actor Player
    participant SpeedPanel as SpeedControlPanel
    participant Playback as TickPlaybackController
    participant C as CoreGameFacade
    participant HUD as DashboardHud
    participant Screen as VillageScreen

    alt next tick
        Player->>SpeedPanel: click Next Tick
        SpeedPanel->>C: advanceTick()
        C-->>SpeedPanel: TickResult and VillageSnapshot
        SpeedPanel->>Screen: setSnapshot(snapshot)
        Screen->>HUD: refresh(snapshot.dashboard)
    else pause
        Player->>SpeedPanel: click Pause
        SpeedPanel->>Playback: setPaused(true)
    else speed change
        Player->>SpeedPanel: click 2x / 4x
        SpeedPanel->>Playback: setSpeed(multiplier)
    end
```

### 5.8 Refreshing the HUD after TickResult

```mermaid
sequenceDiagram
    participant C as CoreGameFacade
    participant E as SimulationEngine
    participant Adapter as HudViewModelAdapter
    participant HUD as DashboardHud
    participant Log as EventLogPanel

    C->>E: advanceTick(village)
    E-->>C: TickResult
    C->>Adapter: buildDashboardViewModel(TickResult, Village)
    Adapter-->>C: DashboardViewModel
    C-->>HUD: updated snapshot/dashboard
    HUD->>HUD: refresh resources/population/parameters/policy
    HUD->>Log: append events from TickResult
```

### 5.9 Loading textures/assets at application startup

```mermaid
sequenceDiagram
    participant Game as DaimyoSimulatorGame
    participant Loading as LoadingScreen
    participant Assets as GameAssetManager
    participant Registry as BuildingSpriteRegistry
    participant Screen as VillageScreen

    Game->>Loading: setScreen(LoadingScreen)
    Loading->>Assets: queue TextureAtlas, UI skin, icons, placeholder
    Assets->>Assets: loadAll()
    Assets-->>Loading: loaded
    Loading->>Registry: validate BuildingType/NaturalFeature mappings
    alt all required mappings exist or fallback available
        Loading->>Game: setScreen(VillageScreen)
        Game->>Screen: show()
    else fatal asset configuration error
        Loading->>Loading: show readable error
    end
```

---

## 6. Rendering Flow

The game screen is split into two layers:

```text
Screen
|
|-- World layer
|   rendered with SpriteBatch and OrthographicCamera
|
|-- UI layer
    rendered with Stage and Scene2D UI
```

Recommended `VillageScreen.render(delta)` flow:

1. Read the latest immutable `VillageSnapshot` held by the screen or request it from `CoreGameFacade` after a command.
2. Update camera position and zoom through `CameraController`.
3. Clear the screen.
4. Render the world layer through `WorldRenderer`:
   - ground tiles;
   - forests and other natural features;
   - placed buildings;
   - grid or selection overlays;
   - visual-only animations.
5. Call `hudStage.act(delta)` to update Scene2D UI behavior.
6. Refresh `DashboardHud` only when the snapshot or `TickResult` changes.
7. Call `hudStage.draw()` to render Scene2D UI.

`render(delta)` must never execute simulation rules. It must not calculate production, consumption, placement validity, job assignment, random events, births/deaths, policy effects, or persistence. Simulation state changes happen only when a command is sent to the controller/facade.

---

## 7. Input Flow

Use a libGDX `InputMultiplexer`:

```text
InputMultiplexer
├── Stage input processor          // UI buttons first
└── GameInputProcessor             // map/camera input if UI did not consume event
```

### 7.1 Camera pan and zoom

`CameraController` handles:

- keyboard pan with WASD or arrow keys;
- mouse drag or middle-button drag pan;
- mouse wheel or trackpad zoom;
- min/max zoom clamp;
- optional map bounds clamp.

Camera movement is visual only and must not touch the core domain model.

### 7.2 Build mode and map cell selection

1. Player selects a building button in `BuildMenu`.
2. `BuildMenu` updates `BuildModeState`.
3. Player clicks the world map.
4. `GameInputProcessor` calls `ScreenToGridMapper`.
5. `ScreenToGridMapper` uses `camera.unproject(screenX, screenY)`, then computes:
   - `gridX = floor(worldX / TILE_SIZE)`;
   - `gridY = floor(worldY / TILE_SIZE)`.
6. The mapper returns a core `Position(gridX, gridY)`.
7. If build mode is active, `GameInputProcessor` calls `CoreGameFacade.placeBuilding(buildingType, position)`.
8. If build mode is not active, `GameInputProcessor` calls `CoreGameFacade.inspectCell(position)`.
9. The HUD refreshes from returned `PlacementResult`, `CellViewModel`, or `VillageSnapshot`.

### 7.3 UI button events

| UI action | libGDX component | Core call / effect |
|---|---|---|
| Select build type | `BuildMenu` | Updates `BuildModeState`; no immediate domain mutation |
| Place building | `GameInputProcessor` | `CoreGameFacade.placeBuilding(type, position)` |
| Select cell | `GameInputProcessor` | `CoreGameFacade.inspectCell(position)` |
| Next tick | `SpeedControlPanel` | `CoreGameFacade.advanceTick()` |
| Pause/speed | `SpeedControlPanel` | Updates tick playback timing only |
| Activate policy | `PolicyPanel` | `CoreGameFacade.activatePolicy(policyType)` |
| Save/load | `MenuOverlay` | `CoreGameFacade.save(path)` / `CoreGameFacade.load(path)` |

---

## 8. Asset Pipeline

### 8.1 Assets folder structure

```text
src/libgdx/main/resources/assets/
├── atlases/
│   ├── village.atlas
│   └── ui.atlas
├── textures/
│   ├── tiles/
│   │   ├── tile_grass.png
│   │   ├── tile_dirt.png
│   │   └── tile_selection.png
│   ├── features/
│   │   └── feature_forest.png
│   ├── buildings/
│   │   ├── building_dwelling.png
│   │   ├── building_rice_farm.png
│   │   ├── building_rice_paddy.png
│   │   ├── building_woodcutters_hut.png
│   │   ├── building_mine.png
│   │   ├── building_smithy.png
│   │   ├── building_workshop.png
│   │   ├── building_market.png
│   │   ├── building_guard_post.png
│   │   └── building_temple.png
│   ├── icons/
│   │   ├── icon_resource_rice.png
│   │   ├── icon_resource_timber.png
│   │   ├── icon_resource_tools.png
│   │   ├── icon_resource_luxury_goods.png
│   │   ├── icon_policy_agricultural_expansion.png
│   │   ├── icon_policy_military_protection.png
│   │   └── icon_policy_craftsmen_production.png
│   └── placeholders/
│       └── missing_asset.png
├── skins/
│   ├── daimyo-ui.json
│   └── daimyo-ui.atlas
└── mapping/
    └── sprite-map.json
```

### 8.2 Naming convention

```text
tile_<terrain>.png
feature_<feature_type>.png
building_<building_type>.png
building_<building_type>_<animation_state>_<frame>.png
icon_resource_<resource_type>.png
icon_policy_<policy_type>.png
ui_<component>.png
```

Examples:

```text
building_dwelling.png
building_guard_post.png
feature_forest.png
icon_resource_rice.png
icon_policy_agricultural_expansion.png
```

### 8.3 TextureAtlas and individual textures

During early development, individual PNG files are acceptable because they are easier to replace manually. Before the final delivery, the project should pack stable sprites into `village.atlas` and access them through `TextureRegion` names.

### 8.4 BuildingType-to-sprite mapping

| Core type | Asset key |
|---|---|
| `BuildingType.DWELLING` | `building_dwelling` |
| `BuildingType.RICE_FARM` | `building_rice_farm` |
| `BuildingType.RICE_PADDY` | `building_rice_paddy` |
| `BuildingType.WOODCUTTERS_HUT` | `building_woodcutters_hut` |
| `BuildingType.MINE` | `building_mine` |
| `BuildingType.SMITHY` | `building_smithy` |
| `BuildingType.WORKSHOP` | `building_workshop` |
| `BuildingType.MARKET` | `building_market` |
| `BuildingType.GUARD_POST` | `building_guard_post` |
| `BuildingType.TEMPLE` | `building_temple` |
| `NaturalFeature.FOREST` | `feature_forest` |

The mapping is owned by the libGDX module. The core knows only domain enums and must not know file paths, texture names, `TextureRegion`, `Animation`, or `AssetManager`.

### 8.5 Missing asset fallback

During development:

1. `GameAssetManager` logs a warning such as `Missing sprite for BuildingType.SMITHY`.
2. `MissingAssetFallback` returns `missing_asset.png`.
3. The game continues running.
4. Final release validation can fail if required production assets are still missing.

---

## 9. Design Decisions and Pattern Mapping

| Requirement / problem | Design decision | Pattern / principle |
|---|---|---|
| Different policies modify production and consumption | Represent each policy as a `PolicyStrategy` | Strategy Pattern |
| Building creation should not be hardcoded inside the engine | Use `BuildingFactory` and `BuildingType` | Factory Pattern |
| UI should not own the model | Scene2D HUD talks to `CoreGameFacade` and receives immutable view models | MVC / Clean Architecture |
| Renderer should not run rules | `WorldRenderer` draws `VillageSnapshot` only | Single Responsibility |
| Save/load must not use a database | Use JSON DTOs through `VillagePersistenceService` | DTO + Service |
| Simulation tick must remain understandable and testable | Split tick work into services: job assignment, production, consumption, parameters, birth/death, events | GRASP / Single Responsibility |
| Placement rules must be extensible | Implement separate `PlacementRule` classes | Open/Closed Principle |
| Building behavior must be polymorphic | Use abstract `Building` plus interfaces like `JobProvider` and `ResourceProducer` | Inheritance + interfaces |
| Missing art during development should not block testing | Use placeholder sprite and warning | Fail-soft development workflow |

---

## 10. Testing Strategy

### 10.1 Core unit tests

Core logic remains pure JUnit and does not require libGDX initialization. Suggested tests:

```text
GridTest
CellTest
PositionTest
BuildingFactoryTest
PlacementRuleTest
ConstructionServiceTest
SimulationEngineTest
TickProcessorOrderTest
PolicyStrategyTest
RandomEventManagerTest
VillagePersistenceServiceTest
VillageSnapshotMapperTest
```

### 10.2 Controller/facade tests

`CoreGameFacade` and `GameController` should be unit-tested with deterministic services or fake dependencies:

- placement delegates to `PlacementService`;
- invalid placement does not change the snapshot;
- `advanceTick()` returns `TickResult` and updated `DashboardViewModel`;
- policy activation respects one active policy and cooldowns;
- `inspectCell()` returns `CellViewModel`, not a mutable `Cell`.

### 10.3 Renderer/UI tests

Renderer and UI classes should stay thin and are mainly validated through integration, manual, and system tests:

- asset loading smoke test;
- missing asset fallback check;
- screen-to-grid coordinate mapping test;
- manual check for camera pan/zoom;
- manual check for HUD refresh after placement and ticks.

Acceptance criteria must verify that UI actions go through the controller/facade and never mutate domain objects directly.

---

## 11. Traceability to User Stories

| Design area | Related user stories |
|---|---|
| Village initialization and grid | US-01 |
| General building construction | US-02 |
| Placement and prerequisite rules | US-03 |
| Villagers and roles | US-04 |
| Dwellings and housing | US-05 |
| Automatic job assignment | US-06 |
| Rice production and consumption | US-07 |
| Timber, Tools, Luxury Goods production | US-08 |
| Markets and resource exchange | US-09 |
| Tick engine | US-10 |
| Village parameters and happiness | US-11 |
| Birth and death | US-12 |
| Strategy policies | US-13 |
| Random events | US-14 |
| Save and load | US-15 |
| libGDX renderer, HUD, and dashboard | US-16 |

---

## 12. Notes for Implementation

- Keep domain logic independent from the UI and renderer.
- Do not create a single class that performs all calculations. `SimulationEngine` coordinates the tick, while specialized services calculate the details.
- Keep formulas simple and deterministic where possible. Random events should use a `RandomProvider` or fixed seed during unit tests.
- The `core` module must never import `com.badlogic.gdx.*`.
- The libGDX module may depend on the core, but it must use immutable snapshots/view models instead of mutable domain objects.
- `WorldRenderer` must draw the world only. It must not enforce placement rules or run production/consumption logic.
- `DashboardHud` and Scene2D panels must call `CoreGameFacade` or `GameController` for commands.
- All important rules should have unit tests: invalid placement, resource shortage, policy effects, rice production, death by starvation, birth with enough food/housing/happiness, and save/load integrity.
- Mermaid diagrams in this document can be copied directly into GitHub and rendered automatically.
