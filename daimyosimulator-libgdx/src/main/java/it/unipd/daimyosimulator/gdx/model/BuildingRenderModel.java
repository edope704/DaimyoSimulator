package it.unipd.daimyosimulator.gdx.model;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;

public record BuildingRenderModel(BuildingType type, Position position, String spriteName) {
}
