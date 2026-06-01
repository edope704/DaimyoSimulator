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

    /** Returns plain consequence strings (used by TickProcessor and existing tests). */
    public List<String> evaluate(Village village) {
        return evaluateFull(village).stream().map(EventReport::consequence).toList();
    }

    /** Returns structured event reports for UI modal display. */
    public List<EventReport> evaluateFull(Village village) {
        List<EventReport> reports = new ArrayList<>();
        if (!village.getConfig().randomEventsEnabled()) {
            return reports;
        }

        double theftProbability = 0.0075
                + (100 - village.getParameters().getProtection()) / 4000.0
                + (100 - village.getParameters().getHousing()) / 6000.0;
        if (randomProvider.chance(theftProbability)) {
            reports.add(applyFull(village, new ResourceTheftEvent()));
        }

        double productivityProbability = village.getParameters().getProtection() >= 70 ? 0.015 : 0.005;
        if (randomProvider.chance(productivityProbability)) {
            reports.add(applyFull(village, new ProductivitySpikeEvent()));
        }

        if (village.getParameters().getFaith() >= 50 && randomProvider.chance(0.0125)) {
            reports.add(applyFull(village, new ReligiousFestivalEvent()));
        }

        if (village.getParameters().getCraftsmanship() >= 50 && randomProvider.chance(0.0125)) {
            reports.add(applyFull(village, new CraftsmanshipBreakthroughEvent()));
        }

        if (village.getGrid().hasBuilding(BuildingType.WORKSHOP) && randomProvider.chance(0.0075)) {
            reports.add(applyFull(village, new WorkshopAccidentEvent()));
        }
        return reports;
    }

    private EventReport applyFull(Village village, RandomEvent event) {
        String consequence = event.apply(village, randomProvider);
        village.addEvent(consequence);
        return new EventReport(event.name(), event.explain(), consequence, event.isPositive());
    }
}
