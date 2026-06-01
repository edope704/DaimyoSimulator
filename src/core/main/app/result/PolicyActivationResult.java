package core.app.result;

import core.app.view.DashboardViewModel;
import core.policy.PolicyType;

public record PolicyActivationResult(boolean success, String message, PolicyType activePolicy, DashboardViewModel dashboard) {
}
