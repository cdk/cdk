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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * An encoder for invariant atom properties. The encoders are used to seed the
 * the generation of atomic hash codes.
 *
 * @author John May
 * @cdk.module interfaces
 * @see org.openscience.cdk.hash.AtomHashGenerator
 * @cdk.githash
 */
public interface AtomEncoder {

    /**
     * Encode an invariant attribute of the given atom. The atom and container
     * should not to be null.
     *
     * @param atom      non-null atom belonging to the <i>container</i>
     * @param container non-null container
     * @return encoding of an invariant atom attribute
     */
    public int encode(IAtom atom, IAtomContainer container);

}
