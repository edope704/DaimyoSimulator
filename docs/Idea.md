## **Types of Villagers**

Every person in the village is a **villager**.

Each villager can be assigned a role. Based on the living villagers available, the game manages who is assigned to which role randomly, depending on the number and type of buildings in the village.

Each villager can have one assigned role.

The roles are:

* **Unhoused Villager** — a villager without a dwelling  
* **Idle Villager** — a normal villager without an assigned job  
* **Employed Villagers**:  
  * **Rice Farmer(Agricultural)**  
  * **Woodcutter(Craftsmen )**  
  * **Blacksmith** — produces Tools goods in the smithy**(Craftsmen )**  
  * **Artisan** — produces luxury goods in the workshop**(Craftsmen )**  
  * **Trader**  
  * **Samurai(Protection )**  
  * **Monk**

Every villager is assigned to a dwelling at birth. If there are no available dwellings, the villager becomes unhoused.

A villager can also become unhoused if they remain idle for X ticks.

---

## **Resources**

*Note: every one of these resources will have its own bar.*

**Rice**: food for villagers, produced by rice paddies.

**Timber**: used to construct buildings, produced by woodcutter buildings.

**Tools**: used by samurai and rice farmers. Produced by blacksmiths in the smithy once every tick.

**Luxury Goods**: used by samurai and monks. Produced by artisans in the workshop once every several ticks.

---

## **Types of Buildings**

Every building is constructed using timber.

* **Dwelling**: houses villagers  
* **Rice Farm**: holds *farmers*  
* **Rice Paddy**: produces rice if there is a *Farm* nearby (*Rule Enforcement)*  
* **Woodcutter’s Hut**: produces timber and holds woodcutters, needs to be near a forest  
* **Smithy**: produces Tools and holds blacksmiths  
* **Mine**: needed by Workshop and Smithy (Rule enforcement for workshop and smithy)  
  Mine is a required building, and Workshop and Smithy can exist only if one mine is present  
* **Workshop**: produces luxury goods and holds artisans  
* **Market**:We have one different market building per resource. Every market holds traders and allows resources to be exchanged. More marketers in a single type of building  \= more resources that can be exchanged and the timer to trade is shorter.  
* **Guard Post** : holds samurai  
* **Temple**: holds monks

Note: *forest* is a building that gets spawned randomically on the map

---

## **Village Parameters**

The village has several core parameters based on the condition of its villagers.  
Every one of these parameters, other than its own bonuses and maluses, affects the general *happiness* parameter.

### **Happiness**

Happiness is managed based on the current state of the village.

It does not directly provide bonuses or penalties. It is simply an indicator of the village’s current situation.

### **Protection** 

Protection is based on the ratio between samurai and villagers.

Possible effects:

* **Bonus**: temporary productivity spike  
* **Penalty**: theft of timber or other resources

### **Food**

Food represents the amount of rice available in the village.

It basically is our powerplant.

Possible effects:

* **Bonus**: new villager birth  
* **Penalty**: villager death

### **Faith**

Given by *villagers / monks*. Unlocks random events.

### **Housing**

Given by *unhoused villagers / houses*. Unlocks random events, for example thefts of food if there are lots of homeless people.

### **Craftsmanship** 

Given by *villagers/ Tools* . Unlocks random events. 

---

## **Strategy Policies**

Policies are a button that can be toggled(1 at a time). They last X ticks and have a reload time of Y ticks.

- ***Agricultural Expansion Policy***: rice paddies produce 1.5x rice and consumes 1.5x Tools   
- ***Military Protection Policy***: samurai values becomes 1.5x and consumes 1.5x luxury and Tools   
- ***Craftsmen Production Policy***: Tools , wood and luxury production values becomes 1.5x and Craftsmen (Craftsmen  villagers)  consume 1.5x rice more

---

## **Time**

Ticks go on with a button that advances to the next tick.

---

## 

## 

## **Birth and Death**

Birth depends on high food levels. 100% is represented by maximum capacity. Maximum capacity is defined by the number of farms.

Example: if food is above 80%, a new villager spawns every X ticks, and food drops to 20%.  
Altra idea:  
*if Food \> X and Housing \> Y and Happiness \> Z*  
	*BirthProgress \+= BirthRate*  
*if BirthRare \>= 100*  
	*spawns villager and consumes amount of food*

Death depends on low food levels.

Example: if food reaches 0, one villager dies every X ticks.

---

## **How Are Idle Villagers Assigned to Jobs?**

The game checks where there is available space and assigns idle villagers randomly.

The assignment is influenced by the number of available job slots in each building type.

Example: if the village has 10 available slots in rice paddies and 2 available slots in the Guard Post , more villagers will probably be assigned as rice farmers, while fewer will be assigned as samurai.

This happens automatically based on the buildings the player has chosen to construct  
The same assignment modality is used for dying villagers as well.

---

## **How Are Villagers Removed From Jobs?**

Villagers are removed from jobs using the same logic as job assignment.

The system chooses probabilistically based on the available roles and building distribution.

Note: these assignment weights can be adjusted manually to keep the game balanced.

Note: one villager is assigned every X ticks.

---

## 

## **Do Rice Paddies Produce Food Without Farmers?**

No, rice paddies do not produce food by default. At least one farmer is needed.  
There is no way we are making it possible to have 0 farmers.

Rice is collected automatically. Rice is consumed every tick.

## **Recommended tick order**

1\. Advance tick counter.

2\. Update active policy duration and cooldown.

3\. Validate building rules.

4\. Assign idle villagers to available jobs.

5\. Produce resources.

6\. Consume resources.

7\. Apply shortages and penalties.

8\. Update village parameters.

9\. Recalculate happiness.

10\. Process births and deaths.

11\. Trigger random events if conditions are met.

12\. Notify dashboard/status observers. 


---

## **Renderer and UX/UI**

DaimyoSimulator uses **libGDX** for the visual presentation layer while keeping the simulation core in pure Java.

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

### **World Renderer**

The world renderer displays the ancient Japanese village as a 2D pixel-art map. It renders:

* logical grid cells as visual tiles;
* buildings placed by the player;
* natural features such as forests;
* selection overlays;
* simple visual animations.

The world layer uses `SpriteBatch` for drawing sprites and `OrthographicCamera` for camera movement, zoom, and coordinate conversion. It receives immutable snapshots or view models from the pure Java core, such as `VillageSnapshot`, `CellViewModel`, and `BuildingViewModel`.

The renderer must not contain simulation rules. It does not calculate production, validate placement, consume resources, assign jobs, apply policies, trigger random events, save/load data, or advance ticks.

### **Game UI / HUD**

The HUD is built with libGDX `Stage` and Scene2D UI. It contains:

* build buttons for selecting buildings;
* resource display for Rice, Timber, Tools, and Luxury Goods;
* population display for total, idle, unhoused, and employed villagers;
* village parameter display for Happiness, Protection, Food, Faith, Housing, and Craftsmanship;
* selected building or selected cell panel;
* next-tick, pause, and speed controls;
* strategy policy buttons;
* event and status log;
* menus and dashboard panels.

The UI never mutates domain objects directly. Buttons and input events call a `GameController`, `CoreGameFacade`, or application service. The core returns immutable DTOs, snapshots, or view models used by the HUD to refresh the displayed values.

### **Asset pipeline**

Assets are created manually by the project team. The expected style is **2D pixelated Japanese-inspired graphics** suitable for an ancient village around year 1200.

Assets live in the libGDX module, for example:

```text
src/libgdx/main/resources/assets/
├── atlases/
├── textures/
│   ├── tiles/
│   ├── buildings/
│   ├── features/
│   ├── icons/
│   └── placeholders/
├── skins/
└── mapping/
```

Domain types are mapped to visual assets only in the libGDX layer. Examples:

```text
BuildingType.DWELLING        -> building_dwelling
BuildingType.RICE_FARM       -> building_rice_farm
BuildingType.GUARD_POST      -> building_guard_post
NaturalFeature.FOREST        -> feature_forest
ResourceType.RICE            -> icon_resource_rice
PolicyType.AGRICULTURAL      -> icon_policy_agricultural_expansion
```

During early development, individual PNG files can be used directly. Before final delivery, stable sprites can be packed into a `TextureAtlas`. Missing assets should use a placeholder sprite and log a clear warning so gameplay and tests can continue while art is still incomplete.

### **Separation from core logic**

The architecture follows MVC / Clean Architecture:

* `core` contains the pure Java simulation and JUnit tests.
* `libgdx` contains graphics, input, HUD, menus, asset loading, and adapters.
* `desktop` contains the desktop launcher.

The libGDX layer may depend on the core module. The core module must never depend on libGDX and must never import `com.badlogic.gdx.*`.
