/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 *  Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) Project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.aromaticity;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @author     Oliver Horlacher <oliver.horlacher@therastrat.com>
 * @cdk.created    2002-03-14
 *
 * @cdk.keyword aromaticity detector
 */
@TestClass("org.openscience.cdk.aromaticity.AromaticityCalculatorTest")
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
    @TestMethod("testIsAromatic_IRing_IAtomContainer")
    public static boolean isAromatic(IRing ring, IAtomContainer atomContainer)
	{
		
		java.util.Iterator<IAtom> ringAtoms = ring.atoms().iterator();
		int eCount = 0;
		java.util.List<IBond> conectedBonds;
		int numDoubleBond = 0;
		boolean allConnectedBondsSingle;
		
		while (ringAtoms.hasNext())
		{
			IAtom atom = ringAtoms.next();
			numDoubleBond = 0;
			allConnectedBondsSingle = true;
			conectedBonds = atomContainer.getConnectedBondsList(atom);
            for (IBond conectedBond : conectedBonds) {
                if (conectedBond.getOrder() == IBond.Order.DOUBLE && ring.contains(conectedBond)) {
                    numDoubleBond++;
                }

                // Count the Electron if bond order = 1.5
                else if (conectedBond.getFlag(CDKConstants.ISAROMATIC) && ring.contains(conectedBond)) {
                    numDoubleBond = 1;
                }

                if (conectedBond.getOrder() != IBond.Order.SINGLE) {
                    allConnectedBondsSingle = false;
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
			else if (allConnectedBondsSingle 
					&& atom.getSymbol().equals("C") 
					&& atom.getFormalCharge() == 1.0)
			{
				// This is for tropylium and kinds. 
				// Dependence on hybridisation would be better:
				// empty p-orbital is needed
				continue;
			}
			else
			{
				return false;	
			}
		}
        return eCount - 2 != 0 && (eCount - 2) % 4 == 0;
    }
}

