package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.app.TradeRequest;
import it.unipd.daimyosimulator.core.app.SnapshotMapper;
import it.unipd.daimyosimulator.core.app.result.TradeResult;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.Role;

public final class TradeService {
    private final SnapshotMapper snapshotMapper;

    public TradeService(SnapshotMapper snapshotMapper) {
        this.snapshotMapper = snapshotMapper;
    }

    public TradeResult trade(Village village, TradeRequest request) {
        if (request.from() == request.to()) {
            return new TradeResult(false, "Trade source and target must differ", snapshotMapper.toSnapshot(village));
        }
        if (!village.getGrid().hasBuilding(BuildingType.MARKET)) {
            return new TradeResult(false, "Trade requires a Market", snapshotMapper.toSnapshot(village));
        }
        int traders = (int) village.countRole(Role.TRADER);
        int capacity = village.getConfig().baseTradeCapacity() + traders * village.getConfig().traderCapacityBonus();
        if (request.amount() <= 0 || request.amount() > capacity) {
            return new TradeResult(false, "Trade amount must be between 1 and " + capacity, snapshotMapper.toSnapshot(village));
        }
        if (!village.getResources().has(request.from(), request.amount())) {
            return new TradeResult(false, "Insufficient " + request.from() + " for trade", snapshotMapper.toSnapshot(village));
        }
        int received = Math.max(1, request.amount() / village.getConfig().tradeExchangeRate());
        village.getResources().consume(request.from(), request.amount());
        village.getResources().add(request.to(), received);
        String message = "Traded " + request.amount() + " " + request.from() + " for " + received + " " + request.to();
        village.addEvent(message);
        return new TradeResult(true, message, snapshotMapper.toSnapshot(village));
    }
}
