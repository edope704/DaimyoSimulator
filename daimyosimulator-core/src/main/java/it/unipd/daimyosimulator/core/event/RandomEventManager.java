package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;

import java.util.ArrayList;
import java.util.List;

public final class RandomEventManager {
    private final RandomProvider randomProvider;

    public RandomEventManager(RandomProvider randomProvider) {
        this.randomProvider = randomProvider;
    }

    public List<String> evaluate(Village village) {
        List<String> events = new ArrayList<>();
        if (!village.getConfig().randomEventsEnabled()) {
            return events;
        }

        double theftProbability = 0.03
                + (100 - village.getParameters().getProtection()) / 1000.0
                + (100 - village.getParameters().getHousing()) / 1500.0;
        if (randomProvider.chance(theftProbability)) {
            events.add(apply(village, new ResourceTheftEvent()));
        }

        double productivityProbability = village.getParameters().getProtection() >= 70 ? 0.06 : 0.02;
        if (randomProvider.chance(productivityProbability)) {
            events.add(apply(village, new ProductivitySpikeEvent()));
        }

        if (village.getParameters().getFaith() >= 50 && randomProvider.chance(0.05)) {
            events.add(apply(village, new ReligiousFestivalEvent()));
        }

        if (village.getParameters().getCraftsmanship() >= 50 && randomProvider.chance(0.05)) {
            events.add(apply(village, new CraftsmanshipBreakthroughEvent()));
        }

        if (village.getGrid().hasBuilding(BuildingType.WORKSHOP) && randomProvider.chance(0.03)) {
            events.add(apply(village, new WorkshopAccidentEvent()));
        }
        return events;
    }

    private String apply(Village village, RandomEvent event) {
        String message = event.apply(village, randomProvider);
        village.addEvent(message);
        return message;
    }
}
