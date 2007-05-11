/*
 *  $RCSfile$
 *  $Author: $
 *  $Date: $
 *  $Revision: $
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

import org.openscience.cdk.interfaces.IAtom;

/**
 * It matches an atom using the atomic number
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-05-10
 * @cdk.module smarts
 * @cdk.keyword SMARTS AST
 */
public class AtomicNumberAtom extends SMARTSAtom {
	private static final long serialVersionUID = 4811205092161793129L;
	private int atomicNumber;
	
	public AtomicNumberAtom(int atomicNumber) {
		this.atomicNumber = atomicNumber;
	}
	
    public boolean matches(IAtom atom) {
    	// TODO: this is just a hack for a few
    	if (atom.getAtomicNumber() != 0) {
            return (atom.getAtomicNumber() == atomicNumber);
    	} 
    	if (atom.getSymbol().equals("C")) {
    		return atomicNumber == 6;
    	} else if (atom.getSymbol().equals("O")) {
    		return atomicNumber == 8;
    	} else if (atom.getSymbol().equals("N")) {
    		return atomicNumber == 7;
    	}
    	return false;
    }
}
