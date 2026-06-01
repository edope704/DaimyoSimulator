package gdx.model;

import core.building.BuildingType;
import core.domain.Position;

public record BuildingRenderModel(BuildingType type, Position position, String spriteName) {
}
