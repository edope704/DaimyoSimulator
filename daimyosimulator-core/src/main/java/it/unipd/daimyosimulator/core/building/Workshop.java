package it.unipd.daimyosimulator.core.building;

import it.unipd.daimyosimulator.core.placement.MineRequiredRule;
import it.unipd.daimyosimulator.core.villager.Role;

import java.util.List;
import java.util.Map;

public final class Workshop extends AbstractBuilding {
    public Workshop() {
        super(BuildingType.WORKSHOP, 35, "Workshop", 0,
                Map.of(Role.ARTISAN, 2), List.of(new MineRequiredRule()));
    }
}
