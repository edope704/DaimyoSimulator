package it.unipd.daimyosimulator.core.event;

/** Structured description of one random event, used by the UI to build a modal pop-up. */
public record EventReport(String name, String explanation, String consequence) {
}
