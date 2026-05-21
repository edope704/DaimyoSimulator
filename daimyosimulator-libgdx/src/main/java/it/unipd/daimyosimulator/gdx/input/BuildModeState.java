package it.unipd.daimyosimulator.gdx.input;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;

import java.util.Optional;

public final class BuildModeState {
    private BuildingType selectedType;
    private boolean demolishMode;
    private Position previewPosition;
    private Boolean lastPlacementValid;

    public void enter(BuildingType type) {
        selectedType = type;
        demolishMode = false;
        lastPlacementValid = null;
    }

    /** Activates demolish mode; clears any build selection. */
    public void enterDemolish() {
        selectedType = null;
        demolishMode = true;
        lastPlacementValid = null;
    }

    public void clear() {
        selectedType = null;
        demolishMode = false;
        lastPlacementValid = null;
    }

    /** True when any build or demolish mode is active. */
    public boolean isActive() {
        return selectedType != null || demolishMode;
    }

    public boolean isDemolishMode() {
        return demolishMode;
    }

    public Optional<BuildingType> selectedType() {
        return Optional.ofNullable(selectedType);
    }

    public Optional<Position> previewPosition() {
        return Optional.ofNullable(previewPosition);
    }

    public void setPreviewPosition(Position previewPosition) {
        this.previewPosition = previewPosition;
    }

    public Optional<Boolean> lastPlacementValid() {
        return Optional.ofNullable(lastPlacementValid);
    }

    public void setLastPlacementValid(boolean lastPlacementValid) {
        this.lastPlacementValid = lastPlacementValid;
    }
}
