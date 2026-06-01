package core;

import core.building.BuildingType;
import core.factory.BuildingFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingFactoryTest {
    @Test
    void factoryCreatesAllBuildingTypes() {
        BuildingFactory factory = new BuildingFactory(TestFixtures.config());
        for (BuildingType type : BuildingType.values()) {
            assertEquals(type, factory.create(type).getType());
        }
    }
}
