package gdx.model;

import core.domain.NaturalFeature;
import core.domain.Position;

public record CellRenderModel(Position position, NaturalFeature naturalFeature, BuildingRenderModel building) {
}
