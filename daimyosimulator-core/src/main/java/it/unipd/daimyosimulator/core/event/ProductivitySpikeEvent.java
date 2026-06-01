package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class ProductivitySpikeEvent implements RandomEvent {
    @Override
    public String name() {
        return "Productivity Spike";
    }

    @Override public boolean isPositive() { return true; }

    @Override
    public String explain() {
        return "Your Samurai kept order and inspired the workforce! "
             + "High Protection (>=70) more than triples the chance of this bonus event. "
             + "You received free Rice and Timber.";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        int impact = ResourceTheftEvent.scaledImpact(village);
        village.getResources().add(ResourceType.RICE, impact);
        village.getResources().add(ResourceType.TIMBER, impact);
        return name() + ": gained " + impact + " RICE and " + impact + " TIMBER";
    }
}
