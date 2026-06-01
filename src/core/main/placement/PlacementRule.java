package core.placement;

import core.building.Building;
import core.domain.Position;
import core.domain.Village;

public interface PlacementRule {
    PlacementCheck validate(Village village, Building building, Position position);
}
