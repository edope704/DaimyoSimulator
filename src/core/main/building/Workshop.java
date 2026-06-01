package core.building;

import core.villager.Role;

import java.util.List;
import java.util.Map;

public final class Workshop extends AbstractBuilding {
    public Workshop() {
        super(BuildingType.WORKSHOP, 35, "Workshop", 0,
                Map.of(Role.ARTISAN, 2), List.of());
    }
}
