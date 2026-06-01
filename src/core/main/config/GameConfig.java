package core.config;

public record GameConfig(
        int gridWidth,
        int gridHeight,
        int initialRice,
        int initialTimber,
        int initialTools,
        int initialLuxuryGoods,
        int initialVillagers,
        double forestDensity,
        int adjacencyRange,
        int jobAssignmentIntervalTicks,
        int ricePerVillagerPerTick,
        int birthFoodThreshold,
        int birthHousingThreshold,
        int birthHappinessThreshold,
        int birthRate,
        int birthRiceCost,
        int starvationDeathIntervalTicks,
        int policyDurationTicks,
        int policyCooldownTicks,
        int workshopProductionIntervalTicks,
        boolean randomEventsEnabled,
        int tradeExchangeRate,
        int baseTradeCapacity,
        int traderCapacityBonus,
        // Max buildings a player may place per tick (resets on tick advance).
        int maxBuildsPerTick
) {
    public GameConfig {
        if (gridWidth <= 0 || gridHeight <= 0) {
            throw new IllegalArgumentException("Grid size must be positive");
        }
        if (initialRice < 0 || initialTimber < 0 || initialTools < 0 || initialLuxuryGoods < 0) {
            throw new IllegalArgumentException("Initial resources cannot be negative");
        }
        if (initialVillagers < 0) {
            throw new IllegalArgumentException("Initial villagers cannot be negative");
        }
        if (forestDensity < 0 || forestDensity > 1) {
            throw new IllegalArgumentException("Forest density must be between 0 and 1");
        }
        if (adjacencyRange <= 0) {
            throw new IllegalArgumentException("Adjacency range must be positive");
        }
        if (jobAssignmentIntervalTicks <= 0 || starvationDeathIntervalTicks <= 0
                || policyDurationTicks <= 0 || policyCooldownTicks <= 0
                || workshopProductionIntervalTicks <= 0) {
            throw new IllegalArgumentException("Tick intervals must be positive");
        }
        if (tradeExchangeRate <= 0 || baseTradeCapacity < 0 || traderCapacityBonus < 0) {
            throw new IllegalArgumentException("Trade values must be positive or zero where applicable");
        }
        if (maxBuildsPerTick <= 0) {
            throw new IllegalArgumentException("maxBuildsPerTick must be positive");
        }
    }

    public static GameConfig defaults() {
        return new GameConfig(
                20,   // gridWidth
                20,   // gridHeight
                100,  // initialRice
                100,  // initialTimber
                20,   // initialTools
                10,   // initialLuxuryGoods
                8,    // initialVillagers
                0.10, // forestDensity
                1,    // adjacencyRange
                1,    // jobAssignmentIntervalTicks
                2,    // ricePerVillagerPerTick  (was 1 – doubles food pressure)
                70,   // birthFoodThreshold      (was 80 – slightly more achievable)
                60,   // birthHousingThreshold
                60,   // birthHappinessThreshold
                25,   // birthRate              
                40,   // birthRiceCost           (was 20 – birth is expensive)
                3,    // starvationDeathIntervalTicks
                5,    // policyDurationTicks
                8,    // policyCooldownTicks
                1,    // workshopProductionIntervalTicks
                true, // randomEventsEnabled
                2,    // tradeExchangeRate
                10,   // baseTradeCapacity
                10,   // traderCapacityBonus
                2     // maxBuildsPerTick
        );
    }

    public GameConfig withGridSize(int width, int height) {
        return new GameConfig(width, height, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                initialVillagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus, maxBuildsPerTick);
    }

    public GameConfig withInitialResources(int rice, int timber, int tools, int luxuryGoods) {
        return new GameConfig(gridWidth, gridHeight, rice, timber, tools, luxuryGoods,
                initialVillagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus, maxBuildsPerTick);
    }

    public GameConfig withInitialVillagers(int villagers) {
        return new GameConfig(gridWidth, gridHeight, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                villagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus, maxBuildsPerTick);
    }

    public GameConfig withForestDensity(double density) {
        return new GameConfig(gridWidth, gridHeight, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                initialVillagers, density, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus, maxBuildsPerTick);
    }

    public GameConfig withRandomEventsEnabled(boolean enabled) {
        return new GameConfig(gridWidth, gridHeight, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                initialVillagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, enabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus, maxBuildsPerTick);
    }
}
