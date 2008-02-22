/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

/**
 * Represents the concept of an atom parity identifying the stereochemistry
 * around an atom, given four neighbouring atoms.
 *
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      egonw
 * @cdk.created 2005-08-24
 * @cdk.keyword atom parity
 * @cdk.keyword stereochemistry
 */
public interface IAtomParity extends Cloneable {
    
    /**
     * Returns the atom for which this parity is defined.
     *
     * @return The atom for which this parity is defined
     */
    public IAtom getAtom();
    
    /**
     * Returns the four atoms that define the stereochemistry for
     * this parity.
     *
     * @return The four atoms that define the stereochemistry for
     *         this parity
     */
    public IAtom[] getSurroundingAtoms();
    
    /**
     * Returns the parity value.
     *
     * @return The parity value
     */
    public int getParity();
    
}





