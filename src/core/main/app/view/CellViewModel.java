package core.app.view;

import core.domain.NaturalFeature;
import core.domain.Position;

public record CellViewModel(Position position, NaturalFeature naturalFeature, BuildingViewModel building) {
    public boolean empty() {
        return naturalFeature == null && building == null;
    }
}
