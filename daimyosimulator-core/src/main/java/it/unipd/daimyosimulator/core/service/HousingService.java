package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Cell;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.villager.HousingStatus;
import it.unipd.daimyosimulator.core.villager.Villager;

import java.util.ArrayList;
import java.util.List;

public final class HousingService {
    public int housingCapacity(Village village) {
        return village.getGrid().getCells().stream()
                .flatMap(cell -> cell.getBuilding().stream())
                .mapToInt(building -> building.getType() == BuildingType.DWELLING ? building.getHousingCapacity() : 0)
                .sum();
    }

    public int housedCount(Village village) {
        return (int) village.getVillagers().stream()
                .filter(villager -> villager.getHousingStatus() == HousingStatus.HOUSED)
                .count();
    }

    public int unhousedCount(Village village) {
        return village.getVillagers().size() - housedCount(village);
    }

    public int assignHousing(Village village) {
        int capacity = housingCapacity(village);
        int housed = housedCount(village);
        int assigned = 0;
        List<Position> dwellings = dwellingPositions(village);
        if (dwellings.isEmpty()) {
            return 0;
        }
        for (Villager villager : village.getVillagers()) {
            if (housed >= capacity) {
                break;
            }
            if (villager.getHousingStatus() == HousingStatus.UNHOUSED) {
                villager.houseAt(dwellings.get(housed % dwellings.size()));
                housed++;
                assigned++;
            }
        }
        return assigned;
    }

    private List<Position> dwellingPositions(Village village) {
        List<Position> positions = new ArrayList<>();
        for (Cell cell : village.getGrid().getCells()) {
            if (cell.getBuilding().filter(building -> building.getType() == BuildingType.DWELLING).isPresent()) {
                positions.add(cell.getPosition());
            }
        }
        return positions;
    }
}
