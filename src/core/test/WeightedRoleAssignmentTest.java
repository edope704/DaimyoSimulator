package core;

import core.building.BuildingType;
import core.service.JobAssignmentService;
import core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightedRoleAssignmentTest {
    @Test
    void freeSlotsExposeAssignmentWeights() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.RICE_FARM, 0, 0);
        TestFixtures.place(village, BuildingType.GUARD_POST, 1, 0);
        var freeSlots = new JobAssignmentService(TestFixtures.random()).freeSlots(village);
        assertTrue(freeSlots.get(Role.RICE_FARMER) > freeSlots.get(Role.SAMURAI));
    }
}
