package it.unipd.daimyosimulator.core.simulation;

import it.unipd.daimyosimulator.core.app.result.TickResult;
import it.unipd.daimyosimulator.core.domain.Village;

public final class SimulationEngine {
    private final TickProcessor tickProcessor;

    public SimulationEngine(TickProcessor tickProcessor) {
        this.tickProcessor = tickProcessor;
    }

    public TickResult advanceTick(Village village) {
        return tickProcessor.process(village);
    }
}
