package it.unipd.daimyosimulator.core.event;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;

public interface RandomEvent {
    String name();

    String apply(Village village, RandomProvider randomProvider);
}
