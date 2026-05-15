package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.domain.Grid;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.domain.VillageParameters;
import it.unipd.daimyosimulator.core.factory.PolicyFactory;
import it.unipd.daimyosimulator.core.policy.PolicyManager;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceStock;
import it.unipd.daimyosimulator.core.villager.HousingStatus;
import it.unipd.daimyosimulator.core.villager.Villager;

import java.util.ArrayList;
import java.util.List;

public final class VillageInitializer {
    private final GameConfig config;
    private final RandomProvider randomProvider;
    private final HousingService housingService = new HousingService();
    private final VillageParameterCalculator parameterCalculator =
            new VillageParameterCalculator(housingService, new HappinessCalculator());

    public VillageInitializer(GameConfig config, RandomProvider randomProvider) {
        this.config = config;
        this.randomProvider = randomProvider;
    }

    public Village createDefaultVillage() {
        return createVillage(config.gridWidth(), config.gridHeight());
    }

    public Village createVillage(int width, int height) {
        GameConfig villageConfig = config.withGridSize(width, height);
        Grid grid = new Grid(width, height);
        int forestCount = generateForests(grid, villageConfig.forestDensity());
        if (forestCount == 0) {
            grid.placeNaturalFeature(NaturalFeature.FOREST, new Position(width - 1, height - 1));
        }

        List<Villager> villagers = new ArrayList<>();
        for (int i = 0; i < villageConfig.initialVillagers(); i++) {
            villagers.add(new Villager(i + 1L, HousingStatus.UNHOUSED));
        }

        Village village = new Village(
                grid,
                new ResourceStock(villageConfig.initialRice(), villageConfig.initialTimber(),
                        villageConfig.initialTools(), villageConfig.initialLuxuryGoods()),
                new VillageParameters(),
                villagers,
                new PolicyManager(new PolicyFactory()),
                villageConfig
        );
        housingService.assignHousing(village);
        parameterCalculator.recalculate(village);
        village.addEvent("New village founded");
        return village;
    }

    private int generateForests(Grid grid, double density) {
        int count = 0;
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (randomProvider.nextDouble() < density) {
                    grid.placeNaturalFeature(NaturalFeature.FOREST, new Position(x, y));
                    count++;
                }
            }
        }
        return count;
    }
}
