/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 *  Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) Project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.aromaticity;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Ring;

/**
 * @cdk.module standard
 *
 * @author     Oliver Horlacher <oliver.horlacher@therastrat.com>
 * @created    2002-03-14
 *
 * @cdk.keyword aromaticity detector
 */
public class AromaticityCalculator
{

	/**
	 *  Tests the <code>ring</code> in the <code>molecule</code> for aromaticity. Uses the 
     *  H&uuml;ckle rule (4n + 2) pie electrons. sp<sup>2</sup> hybridized C contibute 1 electron non 
     *  sp<sup>2</sup> hybridized heteroatoms contribute 2 electrons (N and O should never be sp in 
     *  or anything else in a ring and d electron elements get to complicated) 
     *  sp<sup>2</sup> hybridized heteroatoms contribute 1 electron hybridization is worked out by
     *  counting the number of bonds with order 2. Therefore sp<sup>2</sup> hybridization is assumed 
     *  if there is one bond of order 2. Otherwise sp<sup>3</sup> hybridization is assumed.
	 *
	 * @param  ring      the ring to test
	 * @param  atomContainer  the AtomContainer the ring is in
	 * @return           true if the ring is aromatic false otherwise.
	 */
	protected static boolean isAromatic(Ring ring, AtomContainer atomContainer)
	{
		
		Atom[] ringAtoms = ring.getAtoms();
		int eCount = 0;
		Bond[] conectedBonds;
		int numDoubleBond = 0;
		
		for (int i = 0; i < ringAtoms.length; i++)
		{
			Atom atom = ringAtoms[i];
			numDoubleBond = 0;
			conectedBonds = atomContainer.getConnectedBonds(atom);
			for (int j = 0; j < conectedBonds.length; j++)
			{
				Bond bond = conectedBonds[j];
				if (bond.getOrder() == 2 && ring.contains(bond))
				{
					numDoubleBond++;
				}
				
				// Count the Electron if bond order = 1.5
				if (bond.getOrder() == 1.5 && ring.contains(bond))
				{
					numDoubleBond = 1;
				}
			}
			if (numDoubleBond == 1)
			{
				//C or heteroatoms both contibute 1 electron in sp2 hybridized form
				eCount++;
			}
			else if (!atom.getSymbol().equals("C"))
			{
				//Heteroatom probably in sp3 hybrid therefore 2 electrons contributed.
				eCount = eCount + 2;
			}
			else if (atom.getFlag(CDKConstants.ISAROMATIC))
			{
				eCount++;
			}
			
			else
			{
				return false;	
			}
		}
		if (eCount - 2 != 0 && (eCount - 2) % 4 == 0)
		{
			return true;
		}
		return false;
	}
}

