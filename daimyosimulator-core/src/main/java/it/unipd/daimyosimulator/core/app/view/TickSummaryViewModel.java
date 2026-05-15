package it.unipd.daimyosimulator.core.app.view;

import java.util.List;

public record TickSummaryViewModel(
        long tick,
        ResourceViewModel produced,
        ResourceViewModel consumed,
        int births,
        int deaths,
        List<String> messages
) {
    public TickSummaryViewModel {
        messages = List.copyOf(messages);
    }
}
