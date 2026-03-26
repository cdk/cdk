package org.openscience.cdk.io;

import java.util.Arrays;

public enum MDLReactingCenterStatus {
    NOT_REACTING_CENTER(-1),
    UNMARKED(0),
    GENERIC_REACTING_CENTER(1),
    NO_CHANGE(2),
    BOND_MADE_OR_BROKEN(4),
    GENERIC_REACTING_CENTER_AND_BOND_MADE_OR_BROKEN(5),
    BOND_ORDER_CHANGES(8),
    GENERIC_REACTING_CENTER_AND_BOND_ORDER_CHANGE(9),
    BOND_MADE_OR_BROKEN_AND_BOND_ORDER_CHANGES(12),
    GENERIC_REACTING_CENTER_AND_BOND_MADE_OR_BROKEN_AND_BOND_ORDER_CHANGES(13);

    private final int value;

    MDLReactingCenterStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MDLReactingCenterStatus fromValue(int value) {
        return Arrays.stream(values())
                .filter(status -> status.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid value " + value));
    }
}
