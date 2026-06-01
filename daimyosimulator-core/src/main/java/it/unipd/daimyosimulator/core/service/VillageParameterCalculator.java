package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.domain.VillageParameters;
import it.unipd.daimyosimulator.core.villager.Role;

public final class VillageParameterCalculator {
    private final HousingService housingService;
    private final HappinessCalculator happinessCalculator;

    public VillageParameterCalculator(HousingService housingService, HappinessCalculator happinessCalculator) {
        this.housingService = housingService;
        this.happinessCalculator = happinessCalculator;
    }

    public VillageParameters recalculate(Village village) {
        VillageParameters parameters = village.getParameters();
        int population = village.getVillagers().size();
        int samurai = (int) village.countRole(Role.SAMURAI);
        int monks = (int) village.countRole(Role.MONK);
        int artisans = (int) village.countRole(Role.ARTISAN);
        int blacksmiths = (int) village.countRole(Role.BLACKSMITH);
        int expectedRice = Math.max(1, population * village.getConfig().ricePerVillagerPerTick() * 5);

        int protection = population == 0 ? 100
                : (int) Math.round((samurai * 640.0 / population)
                        * village.getPolicyManager().getActivePolicy().protectionMultiplier());
        protection += (int) village.getGrid().countBuildings(BuildingType.GUARD_POST) * 20;

        int food = Math.min(100, (int) Math.round(village.getResources().getRice() * 100.0 / expectedRice));
        int faith = Math.min(100, (int) Math.round((population == 0 ? 100 : monks * 100.0 / population)
                + village.getGrid().countBuildings(BuildingType.TEMPLE) * 20));
        int housing = population == 0 ? 100
                : (int) Math.round(housingService.housedCount(village) * 100.0 / population);
        int craftsmanship = Math.min(100, village.getResources().getTools() * 3
                + artisans * 15
                + blacksmiths * 15
                + (int) village.getGrid().countBuildings(BuildingType.SMITHY) * 8
                + (int) village.getGrid().countBuildings(BuildingType.WORKSHOP) * 8);

        parameters.setProtection(protection);
        parameters.setFood(food);
        parameters.setFaith(faith);
        parameters.setHousing(housing);
        parameters.setCraftsmanship(craftsmanship);
        parameters.setHappiness(happinessCalculator.calculate(parameters));
        return parameters;
    }
}
