package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class WorkshopAccidentEvent implements RandomEvent {
    @Override
    public String name() {
        return "Workshop Accident";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        int tools = village.getResources().consumeUpTo(ResourceType.TOOLS, 3);
        int luxury = village.getResources().consumeUpTo(ResourceType.LUXURY_GOODS, 3);
        village.getParameters().setHappiness(village.getParameters().getHappiness() - 5);
        return name() + ": lost " + tools + " TOOLS and " + luxury + " LUXURY_GOODS";
    }
}
