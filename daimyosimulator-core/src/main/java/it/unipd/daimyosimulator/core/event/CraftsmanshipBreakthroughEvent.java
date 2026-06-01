package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class CraftsmanshipBreakthroughEvent implements RandomEvent {
    @Override
    public String name() {
        return "Craftsmanship Breakthrough";
    }

    @Override public boolean isPositive() { return true; }

    @Override
    public String explain() {
        return "Your Artisans made a technical discovery! "
             + "Craftsmanship >=50 is required. "
             + "You gained bonus Tools and Timber from the improved techniques.";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        int impact = ResourceTheftEvent.scaledImpact(village);
        village.getResources().add(ResourceType.TOOLS,  impact);
        village.getResources().add(ResourceType.TIMBER, Math.max(1, impact / 2));
        return name() + ": gained " + impact + " TOOLS and " + Math.max(1, impact / 2) + " TIMBER";
    }
}
