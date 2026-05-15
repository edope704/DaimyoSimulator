package it.unipd.daimyosimulator.core.app.view;

import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Position;

public record CellViewModel(Position position, NaturalFeature naturalFeature, BuildingViewModel building) {
    public boolean empty() {
        return naturalFeature == null && building == null;
    }
}
