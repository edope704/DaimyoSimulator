package it.unipd.daimyosimulator.gdx.input;

import it.unipd.daimyosimulator.core.building.BuildingType;

import java.util.Optional;

public final class BuildModeState {
    private BuildingType selectedType;

    public void enter(BuildingType type) {
        selectedType = type;
    }

    public void clear() {
        selectedType = null;
    }

    public boolean isActive() {
        return selectedType != null;
    }

    public Optional<BuildingType> selectedType() {
        return Optional.ofNullable(selectedType);
    }
}
