package it.unipd.daimyosimulator.core.event;

/**
 * Structured description of one random event, used by the UI to build a modal pop-up.
 * {@code positive} is true for resource-granting events (rendered green) and false
 * for resource-taking events (rendered red with the alert icon).
 */
public record EventReport(String name, String explanation, String consequence, boolean positive) {
}
