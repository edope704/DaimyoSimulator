package it.unipd.daimyosimulator.core.app.result;

import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;

public record PlacementResult(boolean success, String message, VillageSnapshot beforeState, VillageSnapshot afterState) {
}
