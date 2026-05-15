package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class CraftsmanshipBreakthroughEvent implements RandomEvent {
    @Override
    public String name() {
        return "Craftsmanship Breakthrough";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        village.getResources().add(ResourceType.TOOLS, 4);
        village.getParameters().setCraftsmanship(village.getParameters().getCraftsmanship() + 5);
        return name() + ": gained 4 TOOLS";
    }
}
