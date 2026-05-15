package it.unipd.daimyosimulator.gdx.model;

import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Position;

public record CellRenderModel(Position position, NaturalFeature naturalFeature, BuildingRenderModel building) {
}
