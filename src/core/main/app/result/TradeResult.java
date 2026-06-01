package core.app.result;

import core.app.view.VillageSnapshot;

public record TradeResult(boolean success, String message, VillageSnapshot afterState) {
}
