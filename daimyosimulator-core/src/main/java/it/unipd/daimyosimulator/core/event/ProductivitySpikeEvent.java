package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class ProductivitySpikeEvent implements RandomEvent {
    @Override
    public String name() {
        return "Productivity Spike";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        village.getResources().add(ResourceType.RICE, 5);
        village.getResources().add(ResourceType.TIMBER, 5);
        return name() + ": gained 5 RICE and 5 TIMBER";
    }
}
