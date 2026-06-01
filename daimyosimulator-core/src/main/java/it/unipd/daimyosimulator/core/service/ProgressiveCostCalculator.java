package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.building.BuildingType;

/**
 * Computes scaling timber costs based on how many buildings of a type already exist.
 *
 * Formula: cost = baseCost + min(existingCount, 4) * delta(type)
 *   - Cost rises by delta for each of the first 4 copies placed.
 *   - From the 5th copy onward the price is frozen at the 5th-copy cost.
 *
 * Delta values:
 *   +10 for Temple, Market, Guard Post, Smithy, Mine, Workshop
 *   +5  for all other types (Dwelling, Farm, Paddy, Woodcutter)
 */
public final class ProgressiveCostCalculator {

    private ProgressiveCostCalculator() {}

    /** Cost increment per copy placed (up to the 4th). */
    public static int delta(BuildingType type) {
        return switch (type) {
            case TEMPLE, MARKET, GUARD_POST, SMITHY, MINE, WORKSHOP -> 10;
            default -> 5;
        };
    }

    /**
     * Returns the timber cost for placing a new building of {@code type}
     * given that {@code existingCount} buildings of that type already exist.
     *
     * @param type          the building type being placed
     * @param existingCount number of buildings of this type already on the map
     * @param baseCost      base timber cost of the building type
     */
    public static int scaledCost(BuildingType type, int existingCount, int baseCost) {
        return baseCost + Math.min(existingCount, 4) * delta(type);
    }
}
