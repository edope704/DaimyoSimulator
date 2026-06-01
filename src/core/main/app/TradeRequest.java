package core.app;

import core.resource.ResourceType;

public record TradeRequest(ResourceType from, ResourceType to, int amount) {
}
