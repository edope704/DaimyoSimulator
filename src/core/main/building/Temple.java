package core.building;

import core.villager.Role;

import java.util.List;
import java.util.Map;

public final class Temple extends AbstractBuilding {
    public Temple() {
        super(BuildingType.TEMPLE, 30, "Temple", 0, Map.of(Role.MONK, 2), List.of());
    }
}
