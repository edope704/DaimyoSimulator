package core.service;

import java.util.List;

public record BirthDeathResult(int births, int deaths, List<String> messages) {
    public BirthDeathResult {
        messages = List.copyOf(messages);
    }
}
