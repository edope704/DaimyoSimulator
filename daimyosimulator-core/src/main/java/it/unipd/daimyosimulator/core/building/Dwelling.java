package it.unipd.daimyosimulator.core.building;

import java.util.List;
import java.util.Map;

public final class Dwelling extends AbstractBuilding {
    public Dwelling() {
        super(BuildingType.DWELLING, 15, "Dwelling", 4, Map.of(), List.of());
    }
}
