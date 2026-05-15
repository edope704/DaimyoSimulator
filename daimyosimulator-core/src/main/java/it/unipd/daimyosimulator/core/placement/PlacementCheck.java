package it.unipd.daimyosimulator.core.placement;

public record PlacementCheck(boolean success, String message) {
    public static PlacementCheck ok() {
        return new PlacementCheck(true, "OK");
    }

    public static PlacementCheck fail(String message) {
        return new PlacementCheck(false, message);
    }
}
