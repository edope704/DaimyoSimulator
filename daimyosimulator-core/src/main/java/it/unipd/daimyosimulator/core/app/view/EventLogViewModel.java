package it.unipd.daimyosimulator.core.app.view;

import java.util.List;

public record EventLogViewModel(List<String> events) {
    public EventLogViewModel {
        events = List.copyOf(events);
    }
}
