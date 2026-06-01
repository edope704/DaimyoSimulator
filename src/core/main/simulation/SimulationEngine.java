package core.simulation;

import core.app.result.TickResult;
import core.domain.Village;

public final class SimulationEngine {
    private final TickProcessor tickProcessor;

    public SimulationEngine(TickProcessor tickProcessor) {
        this.tickProcessor = tickProcessor;
    }

    public TickResult advanceTick(Village village) {
        return tickProcessor.process(village);
    }
}
