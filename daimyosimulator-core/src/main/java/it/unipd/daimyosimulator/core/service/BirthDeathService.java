package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.Villager;

import java.util.ArrayList;
import java.util.List;

public final class BirthDeathService {
    private final HousingService housingService;
    private final RandomProvider randomProvider;

    public BirthDeathService(HousingService housingService, RandomProvider randomProvider) {
        this.housingService = housingService;
        this.randomProvider = randomProvider;
    }

    public BirthDeathResult process(Village village) {
        int births = 0;
        int deaths = 0;
        List<String> messages = new ArrayList<>();

        boolean eligibleForBirth = village.getParameters().getFood() >= village.getConfig().birthFoodThreshold()
                && village.getParameters().getHousing() >= village.getConfig().birthHousingThreshold()
                && village.getParameters().getHappiness() >= village.getConfig().birthHappinessThreshold();
        if (eligibleForBirth) {
            village.addBirthProgress(village.getConfig().birthRate());
            if (village.getBirthProgress() >= 100 && village.getResources().has(ResourceType.RICE, village.getConfig().birthRiceCost())) {
                village.getResources().consume(ResourceType.RICE, village.getConfig().birthRiceCost());
                village.addVillager(false);
                housingService.assignHousing(village);
                village.setBirthProgress(village.getBirthProgress() - 100);
                births = 1;
                messages.add("A new villager was born");
            }
        } else {
            village.setBirthProgress(0);
        }

        if (village.getResources().getRice() == 0 && !village.getVillagers().isEmpty()) {
            village.setStarvationTicks(village.getStarvationTicks() + 1);
            if (village.getStarvationTicks() >= village.getConfig().starvationDeathIntervalTicks()) {
                Villager dead = village.getVillagers().get(randomProvider.nextInt(village.getVillagers().size()));
                village.removeVillager(dead);
                village.setStarvationTicks(0);
                deaths = 1;
                messages.add("Villager " + dead.getId() + " died of starvation");
            }
        } else {
            village.setStarvationTicks(0);
        }
        return new BirthDeathResult(births, deaths, messages);
    }
}
