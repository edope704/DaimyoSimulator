package it.unipd.daimyosimulator.gdx;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.gdx.assets.BuildingSpriteRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssetRegistryFallbackTest {
    @Test
    void buildingRegistryMapsDomainTypesToSpriteNames() {
        assertEquals("building_temple", new BuildingSpriteRegistry().spriteName(BuildingType.TEMPLE));
    }
}
