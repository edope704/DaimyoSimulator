package core.service;

import java.util.List;

public final class ShortageService {
    public List<String> applyShortages(ConsumptionResult consumptionResult) {
        return consumptionResult.shortages();
    }
}
