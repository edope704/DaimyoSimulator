package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class InsufficientTimberTest {
    @Test
    void insufficientTimberRejected() {
        var village = TestFixtures.village();
        village.getResources().set(ResourceType.TIMBER, 0);
        var result = TestFixtures.constructionService(village.getConfig())
                .constructBuilding(village, BuildingType.DWELLING, new Position(0, 0));
        assertFalse(result.success());
    }
}
