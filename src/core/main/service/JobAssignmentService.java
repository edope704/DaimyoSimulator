package core.service;

import core.domain.Village;
import core.random.RandomProvider;
import core.resource.ResourceType;
import core.villager.Role;
import core.villager.Villager;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class JobAssignmentService {
    private final RandomProvider randomProvider;

    public JobAssignmentService(RandomProvider randomProvider) {
        this.randomProvider = randomProvider;
    }

    public Optional<String> assignOneIdleVillager(Village village) {
        if (village.getTickNumber() % village.getConfig().jobAssignmentIntervalTicks() != 0) {
            return Optional.empty();
        }
        Optional<Villager> idle = village.getVillagers().stream().filter(Villager::isIdle).findFirst();
        if (idle.isEmpty()) {
            return Optional.empty();
        }
        EnumMap<Role, Integer> freeSlots = freeSlots(village);
        int totalSlots = freeSlots.values().stream().mapToInt(Integer::intValue).sum();
        if (totalSlots <= 0) {
            return Optional.empty();
        }
        int roll = randomProvider.nextInt(totalSlots);
        Role chosen = Role.IDLE;
        for (Map.Entry<Role, Integer> entry : freeSlots.entrySet()) {
            roll -= entry.getValue();
            if (roll < 0) {
                chosen = entry.getKey();
                break;
            }
        }
        idle.get().assignRole(chosen);
        return Optional.of("Villager " + idle.get().getId() + " assigned as " + chosen);
    }

    public EnumMap<Role, Integer> freeSlots(Village village) {
        EnumMap<Role, Integer> slots = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            slots.put(role, 0);
        }
        village.getGrid().getCells().stream()
                .flatMap(cell -> cell.getBuilding().stream())
                .flatMap(building -> building.getJobSlots().entrySet().stream())
                .forEach(entry -> slots.merge(entry.getKey(), entry.getValue(), Integer::sum));
        village.roleCounts().forEach((role, count) -> slots.computeIfPresent(role, (ignored, capacity) -> Math.max(0, capacity - count)));
        slots.remove(Role.IDLE);
        slots.remove(Role.UNHOUSED);
        // Luxury deprivation: no promotions to prestige roles when luxury stock is exhausted.
        if (village.getResources().get(ResourceType.LUXURY_GOODS) == 0) {
            slots.remove(Role.SAMURAI);
            slots.remove(Role.MONK);
        }
        return slots;
    }
}
