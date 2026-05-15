package it.unipd.daimyosimulator.core.app.result;

import java.nio.file.Path;

public record SaveResult(boolean success, String message, Path path) {
}
