package it.unipd.daimyosimulator.core.persistence.dto;

import it.unipd.daimyosimulator.core.villager.HousingStatus;
import it.unipd.daimyosimulator.core.villager.Role;

public final class VillagerDTO {
    public long id;
    public Role role;
    public HousingStatus housingStatus;
    public PositionDTO dwellingPosition;
}
