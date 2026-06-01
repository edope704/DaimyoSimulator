package core.app.view;

import core.resource.ResourceType;

public record ResourceViewModel(int rice, int timber, int tools, int luxuryGoods) {
    public int amount(ResourceType type) {
        return switch (type) {
            case RICE -> rice;
            case TIMBER -> timber;
            case TOOLS -> tools;
            case LUXURY_GOODS -> luxuryGoods;
        };
    }
}
