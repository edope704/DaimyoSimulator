package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.resource.ResourceStock;

import java.util.List;

public record ConsumptionResult(ResourceStock requested, ResourceStock consumed, List<String> shortages) {
    public ConsumptionResult {
        shortages = List.copyOf(shortages);
    }
}
