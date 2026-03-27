/*
 * Copyright (C) 2026 Uli Fechner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

/**
 * Represents the status of a reacting center in an MDL reaction.
 */
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

    /**
     * Returns the value of the enum.
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the enum value corresponding to the given value.
     * @param value the value
     * @return the enum value
     * @throws IllegalArgumentException if the value is not valid
     */
    public static MDLReactingCenterStatus fromValue(int value) {
        switch (value) {
            case -1:    return NOT_REACTING_CENTER;
            case 0:     return UNMARKED;
            case 1:     return GENERIC_REACTING_CENTER;
            case 2:     return NO_CHANGE;
            case 4:     return BOND_MADE_OR_BROKEN;
            case 5:     return GENERIC_REACTING_CENTER_AND_BOND_MADE_OR_BROKEN;
            case 8:     return BOND_ORDER_CHANGES;
            case 9:     return GENERIC_REACTING_CENTER_AND_BOND_ORDER_CHANGE;
            case 12:    return BOND_MADE_OR_BROKEN_AND_BOND_ORDER_CHANGES;
            case 13:    return GENERIC_REACTING_CENTER_AND_BOND_MADE_OR_BROKEN_AND_BOND_ORDER_CHANGES;
        }

        throw new IllegalArgumentException("Invalid value " + value);
    }
}
