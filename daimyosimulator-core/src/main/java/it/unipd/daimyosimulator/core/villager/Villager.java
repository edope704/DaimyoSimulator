package it.unipd.daimyosimulator.core.villager;

import it.unipd.daimyosimulator.core.domain.Position;

import java.util.Objects;
import java.util.Optional;

public final class Villager {
    private final long id;
    private Role role;
    private HousingStatus housingStatus;
    private Position dwellingPosition;

    public Villager(long id, HousingStatus housingStatus) {
        if (id <= 0) {
            throw new IllegalArgumentException("Villager id must be positive");
        }
        this.id = id;
        this.housingStatus = Objects.requireNonNull(housingStatus, "housingStatus");
        this.role = housingStatus == HousingStatus.HOUSED ? Role.IDLE : Role.UNHOUSED;
    }

    public long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public HousingStatus getHousingStatus() {
        return housingStatus;
    }

    public Optional<Position> getDwellingPosition() {
        return Optional.ofNullable(dwellingPosition);
    }

    public void assignRole(Role role) {
        this.role = Objects.requireNonNull(role, "role");
        if (role == Role.UNHOUSED) {
            this.housingStatus = HousingStatus.UNHOUSED;
            this.dwellingPosition = null;
        }
    }

    public void houseAt(Position dwellingPosition) {
        this.housingStatus = HousingStatus.HOUSED;
        this.dwellingPosition = Objects.requireNonNull(dwellingPosition, "dwellingPosition");
        if (role == Role.UNHOUSED) {
            role = Role.IDLE;
        }
    }

    public void makeUnhoused() {
        this.housingStatus = HousingStatus.UNHOUSED;
        this.dwellingPosition = null;
        this.role = Role.UNHOUSED;
    }

    public boolean isIdle() {
        return role == Role.IDLE;
    }

    public boolean isEmployed() {
        return role != Role.IDLE && role != Role.UNHOUSED;
    }
}
