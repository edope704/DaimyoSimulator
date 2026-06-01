package it.unipd.daimyosimulator.core.building;

import it.unipd.daimyosimulator.core.villager.Role;

import java.util.List;
import java.util.Map;

public final class Smithy extends AbstractBuilding {
    public Smithy() {
        super(BuildingType.SMITHY, 30, "Smithy", 0,
                Map.of(Role.BLACKSMITH, 2), List.of());
    }
}
