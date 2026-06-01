package core.event;

import core.domain.Village;
import core.random.RandomProvider;
import core.resource.ResourceType;

public final class ResourceTheftEvent implements RandomEvent {
    @Override
    public String name() {
        return "Resource Theft";
    }

    @Override
    public String explain() {
        return "Bandits raided your village! Low Protection and poor Housing increase theft risk. "
             + "Build Guard Posts and Dwellings to reduce future incidents.";
    }

    @Override
    public String apply(Village village, RandomProvider randomProvider) {
        int impact = scaledImpact(village);
        ResourceType type = ResourceType.values()[randomProvider.nextInt(ResourceType.values().length)];
        int lost = village.getResources().consumeUpTo(type, impact);
        return name() + ": lost " + lost + " " + type;
    }

    /** Base 20, scaling +1 per 10 ticks elapsed (floored). */
    static int scaledImpact(Village village) {
        return 20 + (int) (village.getTickNumber() / 10);
    }
}
