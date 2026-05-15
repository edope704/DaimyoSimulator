package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.service.JobAssignmentService;
import it.unipd.daimyosimulator.core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleAssignmentServiceTest {
    @Test
    void idleVillagerAssignedToFreeRole() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.DWELLING, 0, 0);
        TestFixtures.place(village, BuildingType.RICE_FARM, 1, 0);
        village.setTickNumber(1);
        new JobAssignmentService(TestFixtures.random()).assignOneIdleVillager(village);
        assertEquals(1, village.countRole(Role.RICE_FARMER));
    }
}
