package it.unipd.daimyosimulator.core.domain;

import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.policy.PolicyManager;
import it.unipd.daimyosimulator.core.resource.ResourceStock;
import it.unipd.daimyosimulator.core.villager.Role;
import it.unipd.daimyosimulator.core.villager.Villager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Village {
    private final Grid grid;
    private final ResourceStock resources;
    private final VillageParameters parameters;
    private final List<Villager> villagers;
    private final PolicyManager policyManager;
    private final GameConfig config;
    private final List<String> eventHistory = new ArrayList<>();
    private long tickNumber;
    private int birthProgress;
    private int starvationTicks;
    private long nextVillagerId;

    public Village(
            Grid grid,
            ResourceStock resources,
            VillageParameters parameters,
            List<Villager> villagers,
            PolicyManager policyManager,
            GameConfig config
    ) {
        this.grid = Objects.requireNonNull(grid, "grid");
        this.resources = Objects.requireNonNull(resources, "resources");
        this.parameters = Objects.requireNonNull(parameters, "parameters");
        this.villagers = new ArrayList<>(Objects.requireNonNull(villagers, "villagers"));
        this.policyManager = Objects.requireNonNull(policyManager, "policyManager");
        this.config = Objects.requireNonNull(config, "config");
        this.nextVillagerId = this.villagers.stream().mapToLong(Villager::getId).max().orElse(0) + 1;
    }

    public Grid getGrid() {
        return grid;
    }

    public ResourceStock getResources() {
        return resources;
    }

    public VillageParameters getParameters() {
        return parameters;
    }

    public List<Villager> getVillagers() {
        return villagers;
    }

    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    public GameConfig getConfig() {
        return config;
    }

    public long getTickNumber() {
        return tickNumber;
    }

    public void setTickNumber(long tickNumber) {
        if (tickNumber < 0) {
            throw new IllegalArgumentException("Tick number cannot be negative");
        }
        this.tickNumber = tickNumber;
    }

    public long advanceTickCounter() {
        tickNumber++;
        return tickNumber;
    }

    public int getBirthProgress() {
        return birthProgress;
    }

    public void setBirthProgress(int birthProgress) {
        this.birthProgress = Math.max(0, birthProgress);
    }

    public void addBirthProgress(int amount) {
        setBirthProgress(birthProgress + amount);
    }

    public int getStarvationTicks() {
        return starvationTicks;
    }

    public void setStarvationTicks(int starvationTicks) {
        this.starvationTicks = Math.max(0, starvationTicks);
    }

    public void addEvent(String event) {
        if (event != null && !event.isBlank()) {
            eventHistory.add(event);
            if (eventHistory.size() > 100) {
                eventHistory.remove(0);
            }
        }
    }

    public List<String> getEventHistory() {
        return List.copyOf(eventHistory);
    }

    public void restoreEventHistory(List<String> events) {
        eventHistory.clear();
        if (events != null) {
            events.stream().filter(event -> event != null && !event.isBlank()).limit(100).forEach(eventHistory::add);
        }
    }

    public Villager addVillager(boolean housed) {
        Villager villager = new Villager(nextVillagerId++, housed
                ? it.unipd.daimyosimulator.core.villager.HousingStatus.HOUSED
                : it.unipd.daimyosimulator.core.villager.HousingStatus.UNHOUSED);
        villagers.add(villager);
        return villager;
    }

    public void removeVillager(Villager villager) {
        villagers.remove(Objects.requireNonNull(villager, "villager"));
    }

    public long countRole(Role role) {
        return villagers.stream().filter(villager -> villager.getRole() == role).count();
    }

    public Map<Role, Integer> roleCounts() {
        EnumMap<Role, Integer> counts = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            counts.put(role, 0);
        }
        for (Villager villager : villagers) {
            counts.merge(villager.getRole(), 1, Integer::sum);
        }
        return counts;
    }
}
