package core.building;

import core.villager.Role;

import java.util.List;
import java.util.Map;

public final class Market extends AbstractBuilding {
    public Market() {
        super(BuildingType.MARKET, 25, "Market", 0, Map.of(Role.TRADER, 2), List.of());
    }
}
