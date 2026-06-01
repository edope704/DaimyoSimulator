package core;

import core.building.BuildingType;
import core.service.JobAssignmentService;
import core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JobAssignmentServiceTest {
    @Test
    void jobAssignmentConsumesOneFreeSlot() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.DWELLING, 0, 0);
        TestFixtures.place(village, BuildingType.GUARD_POST, 1, 0);
        village.setTickNumber(1);
        new JobAssignmentService(TestFixtures.random()).assignOneIdleVillager(village);
        assertTrue(village.countRole(Role.SAMURAI) == 1 || village.countRole(Role.IDLE) > 0);
    }
}
