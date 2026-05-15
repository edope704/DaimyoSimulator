package it.unipd.daimyosimulator.core.app.result;

import it.unipd.daimyosimulator.core.app.view.ResourceViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;

import java.util.List;

public record TickResult(
        VillageSnapshot beforeState,
        VillageSnapshot afterState,
        ResourceViewModel producedResources,
        ResourceViewModel consumedResources,
        List<String> policyEffects,
        int births,
        int deaths,
        List<String> shortagePenalties,
        List<String> randomEvents,
        List<String> messages
) {
    public TickResult {
        policyEffects = List.copyOf(policyEffects);
        shortagePenalties = List.copyOf(shortagePenalties);
        randomEvents = List.copyOf(randomEvents);
        messages = List.copyOf(messages);
    }
}
