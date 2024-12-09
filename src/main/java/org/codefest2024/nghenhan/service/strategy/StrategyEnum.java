package org.codefest2024.nghenhan.service.strategy;

public enum StrategyEnum {
    FARM_STRATEGY("Farm Strategy"),
    SEA_DIRECT_ATTACK("Sea Direct Attack"),
    HIT_AND_RUN("Mountain Hit And Run"),
    DO_NOTHING("Do Nothing");

    private final String displayName;

    StrategyEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName; // To display the friendly name in the ComboBox
    }
}
