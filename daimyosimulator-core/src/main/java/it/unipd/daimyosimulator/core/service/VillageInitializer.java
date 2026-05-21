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
        int forestCount = generateForestClusters(grid, width, height, villageConfig.forestDensity());
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

    /**
     * Places forest clusters for a more natural look.
     * Seeds are scattered at low probability, then each seed expands up to two
     * steps to adjacent cells with a decaying spread chance. Total cells placed
     * is capped at {@code density * width * height} to honour the configured
     * density while still producing connected-looking groves instead of noise.
     */
    private int generateForestClusters(Grid grid, int width, int height, double density) {
        int target = Math.max(1, (int) Math.round(density * width * height));
        double seedDensity = density * 0.30; // fewer seeds, each grows a small patch
        boolean[][] placed = new boolean[height][width];
        int count = 0;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int y = 0; y < height && count < target; y++) {
            for (int x = 0; x < width && count < target; x++) {
                if (randomProvider.nextDouble() < seedDensity) {
                    if (!placed[y][x]) {
                        placed[y][x] = true;
                        count++;
                    }
                    // Try to grow 1-2 cells outward from this seed.
                    for (int d = 0; d < 4 && count < target; d++) {
                        int nx = x + dx[d];
                        int ny = y + dy[d];
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height
                                && !placed[ny][nx] && randomProvider.nextDouble() < 0.65) {
                            placed[ny][nx] = true;
                            count++;
                            // One more step from this neighbor.
                            for (int d2 = 0; d2 < 4 && count < target; d2++) {
                                int nnx = nx + dx[d2];
                                int nny = ny + dy[d2];
                                if (nnx >= 0 && nnx < width && nny >= 0 && nny < height
                                        && !placed[nny][nnx] && randomProvider.nextDouble() < 0.40) {
                                    placed[nny][nnx] = true;
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Write placed flags into grid.
        int finalCount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (placed[y][x]) {
                    grid.placeNaturalFeature(NaturalFeature.FOREST, new Position(x, y));
                    finalCount++;
                }
            }
        }
        return finalCount;
    }
}
