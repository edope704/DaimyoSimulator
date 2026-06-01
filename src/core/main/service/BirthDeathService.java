package core.service;

import core.domain.Village;
import core.random.RandomProvider;
import core.villager.Villager;

import java.util.ArrayList;
import java.util.List;

/**
 * Population growth is driven exclusively by rice abundance.
 *
 * Each tick a progress counter fills based on the rice surplus
 * (current stock minus expected consumption).  Once it reaches 100
 * a new citizen is born and the counter resets.  Large surpluses fill
 * the counter quickly; thin margins fill it slowly; starvation resets it.
 *
 * Gain per tick:
 *   surplus > 0  → 10 + min(surplus / 3, 40)  (10–50 gain → birth every 2–10 ticks)
 *   rice > 0 but no surplus → 3               (birth every ~33 ticks)
 *   rice == 0   → counter resets to 0         (starvation)
 */
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

        int population = village.getVillagers().size();
        int rice = village.getResources().getRice();
        int expectedConsumption = Math.max(1, population * village.getConfig().ricePerVillagerPerTick());
        int surplus = rice - expectedConsumption;

        if (rice > 0) {
            int gain = (surplus > 0)
                    ? 10 + Math.min(surplus / 3, 40)
                    : 3;
            village.addBirthProgress(gain);

            if (village.getBirthProgress() >= 100) {
                village.addVillager(false);
                housingService.assignHousing(village);
                village.setBirthProgress(village.getBirthProgress() - 100);
                births = 1;
                messages.add("A new villager was born");
            }
        } else {
            village.setBirthProgress(0);
        }

        if (rice == 0 && !village.getVillagers().isEmpty()) {
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
