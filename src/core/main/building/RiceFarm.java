package core.building;

import core.villager.Role;

import java.util.List;
import java.util.Map;

public final class RiceFarm extends AbstractBuilding {
    public RiceFarm() {
        super(BuildingType.RICE_FARM, 18, "Rice Farm", 0, Map.of(Role.RICE_FARMER, 3), List.of());
    }
}
