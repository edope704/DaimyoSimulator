package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class ReligiousFestivalEvent implements RandomEvent {
    @Override
    public String name() {
        return "Religious Festival";
    }

    @Override public boolean isPositive() { return true; }

    @Override
    public String explain() {
        return "Your Temple's Monks organised a great festival! "
             + "Faith >=50 is required for this event to occur. "
             + "The celebration yielded bonus rice and luxury goods.";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        int impact = ResourceTheftEvent.scaledImpact(village);
        int riceBonus   = Math.max(1, impact / 2);
        int luxuryBonus = Math.max(1, impact / 4);
        village.getResources().add(ResourceType.RICE,         riceBonus);
        village.getResources().add(ResourceType.LUXURY_GOODS, luxuryBonus);
        return name() + ": gained " + riceBonus + " RICE and " + luxuryBonus + " LUXURY_GOODS";
    }
}
