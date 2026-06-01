package core;

import core.app.SnapshotMapper;
import core.building.BuildingType;
import core.config.GameConfig;
import core.domain.Position;
import core.domain.Village;
import core.factory.BuildingFactory;
import core.placement.CompositePlacementValidator;
import core.random.FixedRandomProvider;
import core.random.RandomProvider;
import core.service.*;

final class TestFixtures {
    private TestFixtures() {
    }

    static GameConfig config() {
        return GameConfig.defaults()
                .withGridSize(5, 5)
                .withForestDensity(0)
                .withInitialResources(100, 500, 50, 30)
                .withRandomEventsEnabled(false);
    }

    static RandomProvider random() {
        return new FixedRandomProvider(0.0, 0);
    }

    static Village village() {
        return new VillageInitializer(config(), random()).createVillage(5, 5);
    }

    static ConstructionService constructionService(GameConfig config) {
        HousingService housingService = new HousingService();
        VillageParameterCalculator parameterCalculator = new VillageParameterCalculator(housingService, new HappinessCalculator());
        return new ConstructionService(
                new BuildingFactory(config),
                new CompositePlacementValidator(),
                housingService,
                parameterCalculator,
                new SnapshotMapper()
        );
    }

    static void place(Village village, BuildingType type, int x, int y) {
        var result = constructionService(village.getConfig()).constructBuilding(village, type, new Position(x, y));
        if (!result.success()) {
            throw new AssertionError(result.message());
        }
    }
}
