package gdx;

import core.building.BuildingType;
import gdx.assets.BuildingSpriteRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssetRegistryFallbackTest {
    @Test
    void buildingRegistryMapsDomainTypesToSpriteNames() {
        assertEquals("building_temple", new BuildingSpriteRegistry().spriteName(BuildingType.TEMPLE));
    }
}
