package core.domain;

import java.util.ArrayList;
import java.util.List;

public record Position(int x, int y) {
    public List<Position> neighbors(int range) {
        if (range <= 0) {
            throw new IllegalArgumentException("Range must be positive");
        }
        List<Position> result = new ArrayList<>();
        for (int dy = -range; dy <= range; dy++) {
            for (int dx = -range; dx <= range; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                result.add(new Position(x + dx, y + dy));
            }
        }
        return result;
    }

    public int chebyshevDistance(Position other) {
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }
}
