/*  $Revision$ $Author$ $Date$    
 *
 *  Copyright (C) 1997-2007  The CDK project
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
 */
package org.openscience.cdk.structgen.stochastic;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Randomly generates a single, connected, correctly bonded structure for 
 * a given molecular formula.
 * To see it working run the graphical 
 * test org.openscience.cdk.test.SingleStructureRandomGeneratorTest
 * and add more structures to the panel using the "More" button. 
 * In order to use this class, use MFAnalyser to get an AtomContainer from 
 * a molecular formula string.
 * 
 * <p>Assign hydrogen counts to each heavy atom. The hydrogens should not be
 * in the atom pool but should be assigned implicitly to the heavy atoms in 
 * order to reduce computational cost.
 * 
 * <p>Assign this AtomContainer to the  
 * PartialFilledStructureMerger and retrieve a randomly generated, but correctly bonded
 * structure by using the generate() method. You can then repeatedly call
 * the generate() method in order to retrieve further structures. 
 * 
 * <p>Agenda:
 * <ul>
 *   <li>add a method for randomly adding hydrogens to the atoms
 *   <li>add a seed for random generator for reproducability
 * </ul>
 *
 * @author     steinbeck
 * @cdk.created    2001-09-04
 * @cdk.module     structgen
 * @cdk.svnrev  $Revision$
 */
public class PartialFilledStructureMerger {
	
	private LoggingTool logger = new LoggingTool(PartialFilledStructureMerger.class);
	
	IAtomContainer atomContainer;
	SaturationChecker satCheck;

	/**
	 * Constructor for the PartialFilledStructureMerger object.
	 */
	public PartialFilledStructureMerger() throws java.lang.Exception
	{
		satCheck = new SaturationChecker();
	}


	/**
	 *  Sets the AtomContainer attribute of the PartialFilledStructureMerger object
	 *
	 * @param  gc  The new AtomContainer value
	 */
	public void setAtomContainer(IAtomContainer gc)
	{
		this.atomContainer = gc;
	}

	public IAtomContainer getAtomContainer()
	{
		return this.atomContainer;
	}

	public IAtomContainer generate() throws CDKException
	{
		boolean structureFound = false;
		boolean bondFormed;
		double order;
		double max, cmax1, cmax2;
		int iteration = 0;
		IAtom partner;
		IAtom atom;
		IAtomContainer backup = atomContainer.getBuilder().newAtomContainer(atomContainer);
		do
		{
			iteration++;

			atomContainer = backup;
			do
			{
				bondFormed = false;
				for (int f = 0; f < atomContainer.getAtomCount(); f++)
				{
					atom = atomContainer.getAtom(f);

					if (!satCheck.isSaturated(atom, atomContainer))
					{
						partner = getAnotherUnsaturatedNode(atom);
						if (partner != null)
						{
							cmax1 = satCheck.getCurrentMaxBondOrder(atom, atomContainer);
							cmax2 = satCheck.getCurrentMaxBondOrder(partner, atomContainer);
							max = Math.min(cmax1, cmax2);
							order = Math.min(Math.max(1.0, (double)Math.round(Math.random() * max)), 3.0);
							logger.debug("cmax1, cmax2, max, order: " + cmax1 + ", " + cmax2 + ", "  + max + ", " + order);	

							atomContainer.addBond(
								atomContainer.getBuilder().newBond(
									atom, partner, BondManipulator.createBondOrder(order)
								)
							);
							bondFormed = true;
						}
                                     					}
				}
			} while (bondFormed);
			if (ConnectivityChecker.isConnected(atomContainer) && satCheck.allSaturated(atomContainer))
			{
				structureFound = true;
			}
		} while (!structureFound && iteration < 300);
		System.out.println("Structure found after " + iteration + " iterations.");	
		return atomContainer;
	}

	
	/**
	 *  Gets the AnotherUnsaturatedNode attribute of the PartialFilledStructureMerger object
	 *
	 * @return                The AnotherUnsaturatedNode value
	 */
	private IAtom getAnotherUnsaturatedNode(IAtom exclusionAtom) throws CDKException
	{
		IAtom atom;
		int next = (int) (Math.random() * atomContainer.getAtomCount());

		for (int f = next; f < atomContainer.getAtomCount(); f++)
		{
			atom = atomContainer.getAtom(f);
			if (!satCheck.isSaturated(atom, atomContainer) && exclusionAtom != atom && !atomContainer.getConnectedAtomsList(exclusionAtom).contains(atom))
			{
				return atom;
			}
		}
		for (int f = 0; f < next; f++)
		{
			atom = atomContainer.getAtom(f);
			if (!satCheck.isSaturated(atom, atomContainer) && exclusionAtom != atom && !atomContainer.getConnectedAtomsList(exclusionAtom).contains(atom))
			{
				return atom;
			}
		}
		return null;
	}

}

