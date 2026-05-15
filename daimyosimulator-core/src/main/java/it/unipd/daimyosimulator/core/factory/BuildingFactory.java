package it.unipd.daimyosimulator.core.factory;

import it.unipd.daimyosimulator.core.building.*;
import it.unipd.daimyosimulator.core.config.GameConfig;

import java.util.Objects;

public final class BuildingFactory {
    private final GameConfig config;

    public BuildingFactory(GameConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    public Building create(BuildingType type) {
        return switch (Objects.requireNonNull(type, "type")) {
            case DWELLING -> new Dwelling();
            case RICE_FARM -> new RiceFarm();
            case RICE_PADDY -> new RicePaddy();
            case WOODCUTTERS_HUT -> new WoodcuttersHut(config.adjacencyRange());
            case MINE -> new Mine();
            case SMITHY -> new Smithy();
            case WORKSHOP -> new Workshop();
            case MARKET -> new Market();
            case GUARD_POST -> new GuardPost();
            case TEMPLE -> new Temple();
        };
    }
}
