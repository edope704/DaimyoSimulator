package core;

import core.domain.Position;
import core.villager.HousingStatus;
import core.villager.Role;
import core.villager.Villager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VillagerTest {
    @Test
    void villagerHasOneRoleAndHousingStatus() {
        Villager villager = new Villager(1, HousingStatus.UNHOUSED);
        assertEquals(Role.UNHOUSED, villager.getRole());
        villager.houseAt(new Position(0, 0));
        assertEquals(Role.IDLE, villager.getRole());
        villager.assignRole(Role.MONK);
        assertTrue(villager.isEmployed());
    }
}
