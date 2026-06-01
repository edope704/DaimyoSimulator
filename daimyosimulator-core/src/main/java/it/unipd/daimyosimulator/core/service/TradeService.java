package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.app.TradeRequest;
import it.unipd.daimyosimulator.core.app.SnapshotMapper;
import it.unipd.daimyosimulator.core.app.result.TradeResult;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class TradeService {
    /** Ticks the market is locked after any successful trade. */
    public static final int MARKET_COOLDOWN_TICKS = 10;
    /** Additional trade volume capacity granted by each Market building. */
    public static final int CAPACITY_PER_MARKET   = 10;

    private final SnapshotMapper snapshotMapper;

    public TradeService(SnapshotMapper snapshotMapper) {
        this.snapshotMapper = snapshotMapper;
    }

    public TradeResult trade(Village village, TradeRequest request) {
        if (request.from() == request.to()) {
            return fail(village, "Trade source and target must differ");
        }
        if (!village.getGrid().hasBuilding(BuildingType.MARKET)) {
            return fail(village, "Trade requires a Market");
        }

        // Cooldown check
        int cooldown = village.getMarketCooldownTicks();
        if (cooldown > 0) {
            return fail(village, "Market on cooldown. Please wait " + cooldown + " tick"
                    + (cooldown == 1 ? "" : "s") + ".");
        }

        // Capacity scales with number of Market buildings
        int marketCount = (int) village.getGrid().countBuildings(BuildingType.MARKET);
        int capacity = marketCount * CAPACITY_PER_MARKET;
        if (request.amount() <= 0 || request.amount() > capacity) {
            return fail(village, "Trade amount must be between 1 and " + capacity
                    + " (" + marketCount + " Market" + (marketCount == 1 ? "" : "s") + ")");
        }
        if (!village.getResources().has(request.from(), request.amount())) {
            return fail(village, "Insufficient " + request.from() + " for trade");
        }

        int received = exchanged(request.from(), request.to(), request.amount());
        village.getResources().consume(request.from(), request.amount());
        village.getResources().add(request.to(), received);
        village.setMarketCooldownTicks(MARKET_COOLDOWN_TICKS);

        String message = "Traded " + request.amount() + " " + displayName(request.from())
                + " for " + received + " " + displayName(request.to())
                + " (market locked " + MARKET_COOLDOWN_TICKS + " ticks)";
        village.addEvent(message);
        return new TradeResult(true, message, snapshotMapper.toSnapshot(village));
    }

    /**
     * Computes how many units of {@code to} are received when giving
     * {@code amount} units of {@code from}, using the asymmetric rate table:
     *
     * <pre>
     *           →Rice  →Timber  →Tools  →Luxury
     * Rice  →     –      ÷5     ÷15     ÷30
     * Timber→    ×5       –     ÷10     ÷30
     * Tools →   ×15     ×10      –      ÷20
     * Luxury→   ×30     ×30     ×20      –
     * </pre>
     */
    public static int exchanged(ResourceType from, ResourceType to, int amount) {
        return Math.max(1, switch (from) {
            case RICE -> switch (to) {
                case TIMBER       -> amount / 5;
                case TOOLS        -> amount / 15;
                case LUXURY_GOODS -> amount / 30;
                default           -> 0;
            };
            case TIMBER -> switch (to) {
                case RICE         -> amount * 5;
                case TOOLS        -> amount / 10;
                case LUXURY_GOODS -> amount / 30;
                default           -> 0;
            };
            case TOOLS -> switch (to) {
                case RICE         -> amount * 15;
                case TIMBER       -> amount * 10;
                case LUXURY_GOODS -> amount / 20;
                default           -> 0;
            };
            case LUXURY_GOODS -> switch (to) {
                case RICE         -> amount * 30;
                case TIMBER       -> amount * 30;
                case TOOLS        -> amount * 20;
                default           -> 0;
            };
        });
    }

    private TradeResult fail(Village village, String message) {
        return new TradeResult(false, message, snapshotMapper.toSnapshot(village));
    }

    private static String displayName(ResourceType type) {
        return switch (type) {
            case RICE         -> "Rice";
            case TIMBER       -> "Timber";
            case TOOLS        -> "Tools";
            case LUXURY_GOODS -> "Luxury Goods";
        };
    }
}
