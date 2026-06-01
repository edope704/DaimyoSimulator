package core.event;

import core.domain.Village;
import core.random.RandomProvider;

public interface RandomEvent {
    String name();

    /** One-sentence explanation of the trigger condition (shown in the event modal). */
    String explain();

    String apply(Village village, RandomProvider randomProvider);

    /** Returns true if this event grants resources (shown in green); false if it takes resources (shown in red). */
    default boolean isPositive() { return false; }
}
