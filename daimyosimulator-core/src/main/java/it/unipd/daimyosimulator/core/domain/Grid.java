package it.unipd.daimyosimulator.core.domain;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.building.BuildingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Grid {
    private final int width;
    private final int height;
    private final Cell[][] cells;

    public Grid(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Grid size must be positive");
        }
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(new Position(x, y));
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isInside(Position position) {
        Objects.requireNonNull(position, "position");
        return position.x() >= 0 && position.y() >= 0 && position.x() < width && position.y() < height;
    }

    public Cell getCell(Position position) {
        if (!isInside(position)) {
            throw new IllegalArgumentException("Position outside grid: " + position);
        }
        return cells[position.y()][position.x()];
    }

    public List<Cell> getCells() {
        List<Cell> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.add(cells[y][x]);
            }
        }
        return result;
    }

    public List<Cell> getNeighbors(Position position, int range) {
        if (!isInside(position)) {
            throw new IllegalArgumentException("Position outside grid: " + position);
        }
        List<Cell> result = new ArrayList<>();
        for (Position candidate : position.neighbors(range)) {
            if (isInside(candidate)) {
                result.add(getCell(candidate));
            }
        }
        return result;
    }

    public void placeBuilding(Building building, Position position) {
        getCell(position).setBuilding(building);
    }

    public void placeNaturalFeature(NaturalFeature feature, Position position) {
        getCell(position).setNaturalFeature(feature);
    }

    public long countBuildings(BuildingType type) {
        return getCells().stream()
                .flatMap(cell -> cell.getBuilding().stream())
                .filter(building -> building.getType() == type)
                .count();
    }

    public boolean hasBuilding(BuildingType type) {
        return countBuildings(type) > 0;
    }

    public boolean hasBuildingWithin(Position position, BuildingType type, int range) {
        return getNeighbors(position, range).stream()
                .flatMap(cell -> cell.getBuilding().stream())
                .anyMatch(building -> building.getType() == type);
    }

    public boolean hasNaturalFeatureWithin(Position position, NaturalFeature feature, int range) {
        Cell cell = getCell(position);
        if (cell.getNaturalFeature().filter(feature::equals).isPresent()) {
            return true;
        }
        return getNeighbors(position, range).stream()
                .flatMap(candidate -> candidate.getNaturalFeature().stream())
                .anyMatch(feature::equals);
    }
}
