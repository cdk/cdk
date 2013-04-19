/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.hash;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A hash function which generates 64-bit hash codes for the atoms of a
 * molecule.
 *
 * @author John May
 * @cdk.module interfaces
 * @cdk.githash
 */
public interface AtomHashGenerator {

    /**
     * Generate invariant 64-bit hash codes for the atoms of the molecule.
     *
     * @param container a molecule
     * @return atomic hash codes
     */
    public long[] generate(IAtomContainer container);

}
