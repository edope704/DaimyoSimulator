package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class ResourceTheftEvent implements RandomEvent {
    @Override
    public String name() {
        return "Resource Theft";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        ResourceType type = ResourceType.values()[randomProvider.nextInt(ResourceType.values().length)];
        int lost = village.getResources().consumeUpTo(type, 5);
        return name() + ": lost " + lost + " " + type;
    }
}
