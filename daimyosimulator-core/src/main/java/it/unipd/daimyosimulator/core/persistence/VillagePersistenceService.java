package it.unipd.daimyosimulator.core.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.persistence.dto.VillageDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VillagePersistenceService {
    private final ObjectMapper objectMapper;
    private final VillageMapper villageMapper;

    public VillagePersistenceService(VillageMapper villageMapper) {
        this.villageMapper = villageMapper;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void save(Village village, Path path) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        objectMapper.writeValue(path.toFile(), villageMapper.toDTO(village));
    }

    public Village load(Path path, GameConfig config) throws IOException {
        VillageDTO dto = objectMapper.readValue(path.toFile(), VillageDTO.class);
        return villageMapper.fromDTO(dto, config);
    }
}
