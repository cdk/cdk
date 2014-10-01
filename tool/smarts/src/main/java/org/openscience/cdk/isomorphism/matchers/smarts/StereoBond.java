/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * (or see http://www.gnu.org/copyleft/lesser.html)
 */
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * This query bond indicates a particular geometric stereo configuration.
 *
 * @cdk.module  smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
public class StereoBond extends SMARTSBond {

    private final boolean   unspecified;
    private final Direction direction;

    public enum Direction {
        UP, DOWN
    }

    public StereoBond(IChemObjectBuilder builder, Direction direction, boolean unspecified) {
        super(builder);
        this.unspecified = unspecified;
        this.direction = direction;
    }

    @Override
    public boolean matches(IBond bond) {
        return Order.SINGLE.equals(bond.getOrder());
    }

    public boolean unspecified() {
        return unspecified;
    }

    public Direction direction(IAtom atom) {
        if (atom == getAtom(0))
            return direction;
        else if (atom == getAtom(1)) return inv(direction);
        throw new IllegalArgumentException("atom is not a memeber of this bond");
    }

    private Direction inv(Direction direction) {
        return direction == Direction.UP ? Direction.DOWN : Direction.UP;
    }
}
