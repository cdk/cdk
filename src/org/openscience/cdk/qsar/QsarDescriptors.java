/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;

/**
 *  Description of the mfe4
 *
 *@author     cubic
 *@cdk.created    November 13, 2004
 * @cdk.module qsar
 */
public class QsarDescriptors {

	/**
	 *  Constructor for the QsarDescriptors object
	 */
	public QsarDescriptors() { }


	/**
	 *  Gets the atomCount attribute of the QsarDescriptors object
	 *
	 *@param  atomContainer      Description of the Parameter
	 *@param  symbol  Description of the Parameter
	 *@return         The atomCount value
	 */
	public int getAtomCount(AtomContainer atomContainer, String symbol) {
		int atomCount = 0;
		Atom[] atoms = atomContainer.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atomContainer.getAtomAt(i).getSymbol().equals(symbol)) {
				atomCount += 1;
			}
		}
		return atomCount;
	}


	/**
	 *  Gets the bondCount attribute of the QsarDescriptors object
	 *
	 *@param  atomContainer         Description of the Parameter
	 *@param  bondOrder  Description of the Parameter
	 *@return            The bondCount value
	 */
	public int getBondCount(AtomContainer atomContainer, double bondOrder) {
		int bondCount = 0;
		Bond[] bonds = atomContainer.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			if (atomContainer.getBondAt(i).getOrder() == bondOrder) {
				bondCount += 1;
			}
		}
		return bondCount;
	}
}

