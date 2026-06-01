package core.persistence.dto;

import java.util.ArrayList;
import java.util.List;

public final class GridDTO {
    public int width;
    public int height;
    public List<CellDTO> cells = new ArrayList<>();
}
