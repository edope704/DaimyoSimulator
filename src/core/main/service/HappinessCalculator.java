package core.service;

import core.domain.VillageParameters;

public final class HappinessCalculator {
    public int calculate(VillageParameters parameters) {
        return (int) Math.round(parameters.getFood() * 0.30
                + parameters.getHousing() * 0.25
                + parameters.getProtection() * 0.20
                + parameters.getCraftsmanship() * 0.15
                + parameters.getFaith() * 0.10);
    }
}
