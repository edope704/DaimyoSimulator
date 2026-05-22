package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;

public interface RandomEvent {
    String name();

    /** One-sentence explanation of the trigger condition (shown in the event modal). */
    String explain();

    String apply(Village village, RandomProvider randomProvider);
}
