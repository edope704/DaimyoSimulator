package it.unipd.daimyosimulator.core.domain;

import it.unipd.daimyosimulator.core.building.Building;

import java.util.Objects;
import java.util.Optional;

public final class Cell {
    private final Position position;
    private Building building;
    private NaturalFeature naturalFeature;

    public Cell(Position position) {
        this.position = Objects.requireNonNull(position, "position");
    }

    public Position getPosition() {
        return position;
    }

    public Optional<Building> getBuilding() {
        return Optional.ofNullable(building);
    }

    public Optional<NaturalFeature> getNaturalFeature() {
        return Optional.ofNullable(naturalFeature);
    }

    public boolean isEmpty() {
        return building == null && naturalFeature == null;
    }

    public boolean hasBuilding() {
        return building != null;
    }

    public boolean hasNaturalFeature() {
        return naturalFeature != null;
    }

    public void setBuilding(Building building) {
        Objects.requireNonNull(building, "building");
        if (!isEmpty()) {
            throw new IllegalStateException("Cell " + position + " is occupied");
        }
        this.building = building;
    }

    public void setNaturalFeature(NaturalFeature naturalFeature) {
        Objects.requireNonNull(naturalFeature, "naturalFeature");
        if (!isEmpty()) {
            throw new IllegalStateException("Cell " + position + " is occupied");
        }
        this.naturalFeature = naturalFeature;
    }
}
