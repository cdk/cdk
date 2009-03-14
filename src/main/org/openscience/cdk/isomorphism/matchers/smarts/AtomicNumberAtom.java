/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.config.Symbols;
import org.openscience.cdk.interfaces.IAtom;

/**
 * This matches an atom using the atomic number.
 *
 * @cdk.module smarts
 * @cdk.svnrev $Revision$
 * @cdk.keyword SMARTS
 */
public class AtomicNumberAtom extends SMARTSAtom {
    private static final long serialVersionUID = 4811205092161793129L;

    /**
     * Creates a new instance.
     *
     * @param atomicNumber
     */
    public AtomicNumberAtom(int atomicNumber) {
        this.setAtomicNumber(atomicNumber);
    }

    /* (non-Javadoc)
    * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
    */
    public boolean matches(IAtom atom) {
        Integer atNum = Symbols.getAtomicNumber(atom.getSymbol());
        return atNum != null && atNum.intValue() == getAtomicNumber().intValue();
    }
}
