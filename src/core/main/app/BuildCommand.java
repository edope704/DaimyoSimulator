package core.app;

import core.building.BuildingType;
import core.domain.Position;

public record BuildCommand(BuildingType type, Position position) {
}
