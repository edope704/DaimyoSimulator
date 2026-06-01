package it.unipd.daimyosimulator.core.app.view;

import it.unipd.daimyosimulator.core.domain.Position;

import java.util.List;
import java.util.Optional;

public record VillageSnapshot(
        int width,
        int height,
        long tick,
        List<CellViewModel> cells,
        ResourceViewModel resources,
        PopulationViewModel population,
        VillageParametersViewModel parameters,
        PolicyViewModel policy,
        List<String> latestEvents,
        /** Buildings placed since last tick advance. */
        int buildsThisTick,
        /** Max buildings allowed per tick (from GameConfig). */
        int maxBuildsPerTick,
        /** Ticks remaining before Market can trade again. 0 = ready. */
        int marketCooldownTicks
) {
    public VillageSnapshot {
        cells = List.copyOf(cells);
        latestEvents = List.copyOf(latestEvents);
    }

    public Optional<CellViewModel> cellAt(Position position) {
        return cells.stream().filter(cell -> cell.position().equals(position)).findFirst();
    }

    /** Builds remaining this tick before the limit is hit. */
    public int buildsRemaining() {
        return Math.max(0, maxBuildsPerTick - buildsThisTick);
    }
}
