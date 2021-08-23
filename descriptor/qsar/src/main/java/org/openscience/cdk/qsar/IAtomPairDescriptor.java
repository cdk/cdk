/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.qsar;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Classes that implement this interface are QSAR descriptor calculators.
 *
 * @cdk.module qsar
 * @cdk.githash
 */
public interface IAtomPairDescriptor extends IDescriptor {

    /**
     * Calculates the descriptor value for the given IAtom.
     *
     * @param atom The first {@link IAtom} of the pair for which this descriptor should be
     *     calculated
     * @param atom2 The second {@link IAtom} of the pair for which this descriptor should be
     *     calculated
     * @param container TODO
     * @return An object of {@link DescriptorValue} that contain the calculated value as well as
     *     specification details
     */
    public DescriptorValue calculate(IAtom atom, IAtom atom2, IAtomContainer container);
}
