package it.unipd.daimyosimulator.core.app.result;

import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;

public record TradeResult(boolean success, String message, VillageSnapshot afterState) {
}
