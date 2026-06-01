package core.service;

import core.domain.Village;
import core.domain.VillageParameters;
import core.villager.Role;

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

        // Security: 1 Samurai per 8 citizens = 100%. No flat bonus from Guard Post.
        int protection = population == 0 ? 100
                : Math.min(100, (int) Math.round(samurai * 800.0 / population
                        * village.getPolicyManager().getActivePolicy().protectionMultiplier()));

        int food = Math.min(100, (int) Math.round(village.getResources().getRice() * 100.0 / expectedRice));
        // Culture: 1 Monk per 12 citizens = 100%. No flat bonus from Temple.
        int faith = population == 0 ? 100
                : Math.min(100, (int) Math.round(monks * 1200.0 / population));
        int housing = population == 0 ? 100
                : (int) Math.round(housingService.housedCount(village) * 100.0 / population);
        // Craftsmanship: 50% from Artisan ratio (1:10 = 50pts) + 50% from Blacksmith ratio (1:6 = 50pts).
        int artisanPart    = population == 0 ? 0 : Math.min(50, (int) Math.round(artisans    * 500.0 / population));
        int blacksmithPart = population == 0 ? 0 : Math.min(50, (int) Math.round(blacksmiths * 300.0 / population));
        int craftsmanship  = artisanPart + blacksmithPart;

        parameters.setProtection(protection);
        parameters.setFood(food);
        parameters.setFaith(faith);
        parameters.setHousing(housing);
        parameters.setCraftsmanship(craftsmanship);
        parameters.setHappiness(happinessCalculator.calculate(parameters));
        return parameters;
    }
}
