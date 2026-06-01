package core.app.result;

import core.app.view.VillageSnapshot;

public record PlacementResult(boolean success, String message, VillageSnapshot beforeState, VillageSnapshot afterState) {
}
