package core.app.result;

import core.app.view.VillageSnapshot;

import java.nio.file.Path;

public record LoadResult(boolean success, String message, Path path, VillageSnapshot snapshot) {
}
