package core.resource;

import java.util.EnumMap;
import java.util.Map;

public final class ResourceStock {
    private final EnumMap<ResourceType, Integer> amounts = new EnumMap<>(ResourceType.class);

    public ResourceStock() {
        this(0, 0, 0, 0);
    }

    public ResourceStock(int rice, int timber, int tools, int luxuryGoods) {
        set(ResourceType.RICE, rice);
        set(ResourceType.TIMBER, timber);
        set(ResourceType.TOOLS, tools);
        set(ResourceType.LUXURY_GOODS, luxuryGoods);
    }

    public int get(ResourceType type) {
        return amounts.getOrDefault(type, 0);
    }

    public int getRice() {
        return get(ResourceType.RICE);
    }

    public int getTimber() {
        return get(ResourceType.TIMBER);
    }

    public int getTools() {
        return get(ResourceType.TOOLS);
    }

    public int getLuxuryGoods() {
        return get(ResourceType.LUXURY_GOODS);
    }

    public boolean has(ResourceType type, int amount) {
        requireNonNegative(amount);
        return get(type) >= amount;
    }

    public void add(ResourceType type, int amount) {
        requireNonNegative(amount);
        set(type, get(type) + amount);
    }

    public boolean consume(ResourceType type, int amount) {
        requireNonNegative(amount);
        if (!has(type, amount)) {
            return false;
        }
        set(type, get(type) - amount);
        return true;
    }

    public int consumeUpTo(ResourceType type, int requested) {
        requireNonNegative(requested);
        int actual = Math.min(get(type), requested);
        set(type, get(type) - actual);
        return actual;
    }

    public void set(ResourceType type, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Resource amount cannot be negative");
        }
        amounts.put(type, amount);
    }

    public void addAll(ResourceStock delta) {
        for (ResourceType type : ResourceType.values()) {
            add(type, delta.get(type));
        }
    }

    public Map<ResourceType, Integer> asMap() {
        return Map.copyOf(amounts);
    }

    public ResourceStock copy() {
        return new ResourceStock(getRice(), getTimber(), getTools(), getLuxuryGoods());
    }

    private static void requireNonNegative(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
}
