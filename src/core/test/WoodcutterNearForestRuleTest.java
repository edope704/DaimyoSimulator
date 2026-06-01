package core;

import core.building.BuildingType;
import core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Woodcutter's Hut placement rule now accepts two valid conditions:
 *   1. Adjacent to a playable Forest tile (NaturalFeature.FOREST within adjacency range).
 *   2. Adjacent to the outer decorative tree border, i.e. within adjacency range of any
 *      grid edge (the non-playable trees that visually surround the map).
 *
 * TestFixtures uses a 5x5 grid (forestDensity=0) which always seeds exactly one
 * Forest at position (4,4) to guarantee at least one valid forest cell.
 */
class WoodcutterNearForestRuleTest {
    @Test
    void woodcutterRequiresNearbyForestOrBorder() {
        var village = TestFixtures.village();
        var service = TestFixtures.constructionService(village.getConfig());

        // Interior position not adjacent to the single forest at (4,4) and not at any edge: must fail.
        assertFalse(service.constructBuilding(village, BuildingType.WOODCUTTERS_HUT, new Position(1, 1)).success());

        // Adjacent to the forest at (4,4): must succeed.
        assertTrue(service.constructBuilding(village, BuildingType.WOODCUTTERS_HUT, new Position(3, 4)).success());

        // At the grid edge (adjacent to the outer tree border): must succeed.
        assertTrue(service.constructBuilding(village, BuildingType.WOODCUTTERS_HUT, new Position(0, 2)).success());
    }
}
