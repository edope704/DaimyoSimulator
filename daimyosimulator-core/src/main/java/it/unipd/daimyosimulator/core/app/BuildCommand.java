package it.unipd.daimyosimulator.core.app;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;

public record BuildCommand(BuildingType type, Position position) {
}
