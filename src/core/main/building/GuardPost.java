package core.building;

import core.villager.Role;

import java.util.List;
import java.util.Map;

public final class GuardPost extends AbstractBuilding {
    public GuardPost() {
        super(BuildingType.GUARD_POST, 25, "Guard Post", 0, Map.of(Role.SAMURAI, 2), List.of());
    }
}
