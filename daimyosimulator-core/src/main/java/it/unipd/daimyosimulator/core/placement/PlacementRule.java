package it.unipd.daimyosimulator.core.placement;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;

public interface PlacementRule {
    PlacementCheck validate(Village village, Building building, Position position);
}
