package core.building;

import java.util.List;
import java.util.Map;

public final class Mine extends AbstractBuilding {
    public Mine() {
        super(BuildingType.MINE, 25, "Mine", 0, Map.of(), List.of());
    }
}
