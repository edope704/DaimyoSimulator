package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;

public final class ReligiousFestivalEvent implements RandomEvent {
    @Override
    public String name() {
        return "Religious Festival";
    }

    @Override
    public String explain() {
        return "Your Temple's Monks organised a great festival! "
             + "Faith ≥50 is required for this event to occur. "
             + "Faith and Happiness both increased.";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        village.getParameters().setFaith(village.getParameters().getFaith() + 8);
        village.getParameters().setHappiness(village.getParameters().getHappiness() + 5);
        return name() + ": faith and happiness improved";
    }
}
