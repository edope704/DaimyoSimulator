package it.unipd.daimyosimulator.core.config;

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
        int traderCapacityBonus
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
    }

    public static GameConfig defaults() {
        return new GameConfig(
                20,
                20,
                100,
                100,
                20,
                10,
                8,
                0.10,
                1,
                1,
                1,
                80,
                60,
                60,
                25,
                20,
                3,
                5,
                8,
                3,
                true,
                2,
                10,
                10
        );
    }

    public GameConfig withGridSize(int width, int height) {
        return new GameConfig(width, height, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                initialVillagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus);
    }

    public GameConfig withInitialResources(int rice, int timber, int tools, int luxuryGoods) {
        return new GameConfig(gridWidth, gridHeight, rice, timber, tools, luxuryGoods,
                initialVillagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus);
    }

    public GameConfig withInitialVillagers(int villagers) {
        return new GameConfig(gridWidth, gridHeight, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                villagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus);
    }

    public GameConfig withForestDensity(double density) {
        return new GameConfig(gridWidth, gridHeight, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                initialVillagers, density, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, randomEventsEnabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus);
    }

    public GameConfig withRandomEventsEnabled(boolean enabled) {
        return new GameConfig(gridWidth, gridHeight, initialRice, initialTimber, initialTools, initialLuxuryGoods,
                initialVillagers, forestDensity, adjacencyRange, jobAssignmentIntervalTicks,
                ricePerVillagerPerTick, birthFoodThreshold, birthHousingThreshold, birthHappinessThreshold,
                birthRate, birthRiceCost, starvationDeathIntervalTicks, policyDurationTicks,
                policyCooldownTicks, workshopProductionIntervalTicks, enabled, tradeExchangeRate,
                baseTradeCapacity, traderCapacityBonus);
    }
}
