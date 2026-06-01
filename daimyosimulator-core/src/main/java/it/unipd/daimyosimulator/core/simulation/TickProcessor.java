package it.unipd.daimyosimulator.core.simulation;

import it.unipd.daimyosimulator.core.app.SnapshotMapper;
import it.unipd.daimyosimulator.core.app.result.TickResult;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Cell;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.event.EventReport;
import it.unipd.daimyosimulator.core.event.RandomEventManager;
import it.unipd.daimyosimulator.core.service.*;

import java.util.ArrayList;
import java.util.List;

public final class TickProcessor {
    private final SnapshotMapper snapshotMapper;
    private final JobAssignmentService jobAssignmentService;
    private final ProductionService productionService;
    private final ConsumptionService consumptionService;
    private final ShortageService shortageService;
    private final VillageParameterCalculator parameterCalculator;
    private final BirthDeathService birthDeathService;
    private final RandomEventManager randomEventManager;

    public TickProcessor(
            SnapshotMapper snapshotMapper,
            JobAssignmentService jobAssignmentService,
            ProductionService productionService,
            ConsumptionService consumptionService,
            ShortageService shortageService,
            VillageParameterCalculator parameterCalculator,
            BirthDeathService birthDeathService,
            RandomEventManager randomEventManager
    ) {
        this.snapshotMapper = snapshotMapper;
        this.jobAssignmentService = jobAssignmentService;
        this.productionService = productionService;
        this.consumptionService = consumptionService;
        this.shortageService = shortageService;
        this.parameterCalculator = parameterCalculator;
        this.birthDeathService = birthDeathService;
        this.randomEventManager = randomEventManager;
    }

    public TickResult process(Village village) {
        VillageSnapshot before = snapshotMapper.toSnapshot(village);
        List<String> messages = new ArrayList<>();
        List<String> policyEffects = new ArrayList<>();

        village.advanceTickCounter();
        // Reset per-tick build quota so the player gets fresh build actions each tick.
        village.resetBuildsThisTick();
        // Tick down the market trade cooldown.
        village.decrementMarketCooldown();

        List<String> policyMessages = village.getPolicyManager().advanceTick(village.getConfig());
        policyEffects.addAll(policyMessages);
        messages.addAll(policyMessages);

        messages.addAll(validateBuildingRules(village));

        jobAssignmentService.assignOneIdleVillager(village).ifPresent(messages::add);

        var produced = productionService.produce(village);
        var consumption = consumptionService.consume(village);
        List<String> shortages = shortageService.applyShortages(consumption);
        messages.addAll(shortages);

        parameterCalculator.recalculate(village);
        BirthDeathResult birthDeathResult = birthDeathService.process(village);
        messages.addAll(birthDeathResult.messages());
        if (birthDeathResult.births() > 0 || birthDeathResult.deaths() > 0) {
            parameterCalculator.recalculate(village);
        }

        List<EventReport> eventReports = randomEventManager.evaluateFull(village);
        List<String> randomEvents = eventReports.stream().map(EventReport::consequence).toList();
        messages.addAll(randomEvents);

        for (String message : messages) {
            village.addEvent("Tick " + village.getTickNumber() + ": " + message);
        }

        VillageSnapshot after = snapshotMapper.toSnapshot(village);
        return new TickResult(
                before,
                after,
                snapshotMapper.toResourceViewModel(produced),
                snapshotMapper.toResourceViewModel(consumption.consumed()),
                policyEffects,
                birthDeathResult.births(),
                birthDeathResult.deaths(),
                shortages,
                randomEvents,
                eventReports,
                messages
        );
    }

    private List<String> validateBuildingRules(Village village) {
        List<String> warnings = new ArrayList<>();
        for (Cell cell : village.getGrid().getCells()) {
            cell.getBuilding().ifPresent(building -> {
                if (building.getType() == BuildingType.WOODCUTTERS_HUT
                        && !village.getGrid().hasNaturalFeatureWithin(cell.getPosition(), NaturalFeature.FOREST,
                        village.getConfig().adjacencyRange())) {
                    warnings.add("Woodcutter's Hut at " + cell.getPosition() + " has no nearby forest");
                }
                if ((building.getType() == BuildingType.SMITHY || building.getType() == BuildingType.WORKSHOP)
                        && !village.getGrid().hasBuilding(BuildingType.MINE)) {
                    warnings.add(building.getDisplayName() + " at " + cell.getPosition() + " has no Mine prerequisite");
                }
            });
        }
        return warnings;
    }
}
