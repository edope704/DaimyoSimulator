package it.unipd.daimyosimulator.core.app;

import it.unipd.daimyosimulator.core.resource.ResourceType;

public record TradeRequest(ResourceType from, ResourceType to, int amount) {
}
