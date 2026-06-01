package core.event;

import core.domain.Village;
import core.random.RandomProvider;
import core.resource.ResourceType;

public final class WorkshopAccidentEvent implements RandomEvent {
    @Override
    public String name() {
        return "Workshop Accident";
    }

    @Override
    public String explain() {
        return "A fire broke out in your Workshop! "
             + "Any Workshop on the map carries a chance of this mishap per tick. "
             + "Tools and Luxury Goods were lost in the blaze.";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        int impact = ResourceTheftEvent.scaledImpact(village);
        int tools  = village.getResources().consumeUpTo(ResourceType.TOOLS,         impact);
        int luxury = village.getResources().consumeUpTo(ResourceType.LUXURY_GOODS,  impact);
        return name() + ": lost " + tools + " TOOLS and " + luxury + " LUXURY_GOODS";
    }
}
