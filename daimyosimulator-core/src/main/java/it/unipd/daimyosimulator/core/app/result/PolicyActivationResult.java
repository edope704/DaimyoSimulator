package it.unipd.daimyosimulator.core.app.result;

import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;
import it.unipd.daimyosimulator.core.policy.PolicyType;

public record PolicyActivationResult(boolean success, String message, PolicyType activePolicy, DashboardViewModel dashboard) {
}
