package core.persistence.dto;

import core.villager.HousingStatus;
import core.villager.Role;

public final class VillagerDTO {
    public long id;
    public Role role;
    public HousingStatus housingStatus;
    public PositionDTO dwellingPosition;
}
