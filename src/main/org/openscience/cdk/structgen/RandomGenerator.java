/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.structgen;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.math.MathTools;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * RandomGenerator is a generator of Constitutional Isomers. It needs to be 
 * provided with a starting constitution and it makes random moves in 
 * constitutional space from there. 
 * This generator was first suggested by J.-L. Faulon {@cdk.cite FAU96}.
 *
 * <p>Unlike the VicinitySampler, this methods does not sample
 * the full Faulon vicinity.
 * 
 * @see         org.openscience.cdk.structgen.VicinitySampler
 * 
 * @cdk.githash
 * @cdk.keyword structure generator
 */
public class RandomGenerator {
	
	LoggingTool logger = new LoggingTool(RandomGenerator.class);
	
	private IMolecule proposedStructure = null;
	private IMolecule molecule = null;
	private IMolecule trial = null;

	/**
	 * Constructs a RandomGenerator with a given starting structure.
	 *
	 * @param   molecule  The starting structure
	 */
	public RandomGenerator(IMolecule molecule)
	{
		setMolecule(molecule);
	}


	/**
	 * Proposes a structure which can be accepted or rejected by an external 
	 * entity. If rejected, the structure is not used as a starting point 
	 * for the next random move in structure space.
	 *
	 * @return A proposed molecule    
	 */
	public IMolecule proposeStructure()
	{
		logger.debug("RandomGenerator->proposeStructure() Start");
		do
		{
			try {
				trial = (IMolecule)molecule.clone();
			} catch (CloneNotSupportedException e) {
				logger.error("Could not clone IAtomContainer!" + e.getMessage());
				trial = null;
			}
			mutate(trial);
			if(logger.isDebugEnabled()) {
				String s = "BondCounts:    ";
				for (int f = 0; f < trial.getAtomCount(); f++) {
					s += trial.getConnectedBondsCount(trial.getAtom(f)) + " ";
				}
				logger.debug(s);
				s = "BondOrderSums: ";
				for (int f = 0; f < trial.getAtomCount(); f++) {
					s += trial.getBondOrderSum(trial.getAtom(f)) + " ";
				}
				logger.debug(s);
			}
		}
		while(trial == null || !ConnectivityChecker.isConnected(trial));
		proposedStructure = trial;
		
		return proposedStructure;
	}

	/**
	 * Tell the RandomGenerator to accept the last structure that had been proposed.
	 */
	public void acceptStructure()
	{
		if (proposedStructure != null)
		{
			molecule = proposedStructure;
		}
	}
	
	
	/**
	 * Randomly chooses four atoms and alters the bonding
	 * pattern between them according to rules described 
	 * in "Faulon, JCICS 1996, 36, 731".
	 */
	public void mutate(IAtomContainer ac)
	{
		logger.debug("RandomGenerator->mutate() Start");
		int nrOfAtoms = ac.getAtomCount();
		int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
		double a11 = 0, a12 = 0, a22 = 0, a21 = 0;
		double b11 = 0, lowerborder = 0, upperborder = 0;

		IAtom ax1 = null, ax2 = null, ay1 = null, ay2  = null;
		IBond b1 = null, b2 = null, b3 = null, b4 = null;
		int[] choices = new int[3];
		int choiceCounter  = 0;
		/* We need at least two non-zero bonds in order to be successful */
		int nonZeroBondsCounter = 0;
		do
		{
			do
			{
				nonZeroBondsCounter = 0;
				/* Randomly choose four distinct atoms */
				do
				{
					// this yields numbers between 0 and (nrOfAtoms - 1)
					x1 = (int)(Math.random() * nrOfAtoms);
					x2 = (int)(Math.random() * nrOfAtoms);
					y1 = (int)(Math.random() * nrOfAtoms);
					y2 = (int)(Math.random() * nrOfAtoms);
					logger.debug("RandomGenerator->mutate(): x1, x2, y1, y2: " + x1 + ", " + x2 + ", " + y1 + ", " + y2);
				}
				while (!(x1 != x2 && x1 != y1 && x1 != y2 && x2 != y1 && x2 != y2 && y1 != y2));
				ax1 = ac.getAtom(x1);
				ay1 = ac.getAtom(y1);
				ax2 = ac.getAtom(x2);
				ay2 = ac.getAtom(y2);
				/* Get four bonds for these four atoms */
				
				b1 = ac.getBond(ax1, ay1);
				if (b1 != null)
				{
					a11 = BondManipulator.destroyBondOrder(b1.getOrder());
					nonZeroBondsCounter ++;				
				}
				else
				{
					a11 = 0;
				}
				
				b2 = ac.getBond(ax1, ay2);
				if (b2 != null)
				{
					a12 = BondManipulator.destroyBondOrder(b2.getOrder());
					nonZeroBondsCounter ++;				
				}
				else
				{
					a12 = 0;
				}

				b3 = ac.getBond(ax2, ay1);
				if (b3 != null)
				{
					a21 = BondManipulator.destroyBondOrder(b3.getOrder());
					nonZeroBondsCounter ++;				
				}
				else
				{
					a21 = 0;
				}
				
				b4 = ac.getBond(ax2, ay2);									
				if (b4 != null)
				{
					a22 = BondManipulator.destroyBondOrder(b4.getOrder());
					nonZeroBondsCounter ++;
				}
				else
				{
					a22 = 0;
				}
				logger.debug("RandomGenerator->mutate()->The old bond orders: a11, a12, a21, a22: " +  + a11 + ", " + a12 + ", " + a21 + ", " + a22);
			}while(nonZeroBondsCounter < 2);
			
					
			/* Compute the range for b11 (see Faulons formulae for details) */
			double[] cmax = {0, a11 - a22, a11 + a12 - 3, a11 + a21 - 3};
			double[] cmin = {3, a11 + a12, a11 + a21, a11 - a22 + 3};
			lowerborder = MathTools.max(cmax);
			upperborder = MathTools.min(cmin);
			/* Randomly choose b11 != a11 in the range max > r > min */
			logger.debug("*** New Try ***");
			logger.debug("a11 = ", a11);
			logger.debug("upperborder = ", upperborder);
			logger.debug("lowerborder = ", lowerborder);
			choiceCounter = 0;
			for (double f = lowerborder; f <= upperborder; f++)
			{
				if (f != a11)
				{
					choices[choiceCounter] = (int)f;
					choiceCounter ++;
				}
			}
			if (choiceCounter > 0)
			{
				b11 = choices[(int)(Math.random() * choiceCounter)];
			}

			logger.debug("b11 = " + b11);
		}
		while (!(b11 != a11 && (b11 >= lowerborder && b11 <= upperborder)));

		double b12 = a11 + a12 - b11;
		double b21 = a11 + a21 - b11;
		double b22 = a22 - a11 + b11;
		
		
		if (b11 > 0)
		{
			if (b1 == null)
			{
				b1 = ac.getBuilder().newBond(
					ax1, ay1, BondManipulator.createBondOrder(b11)
				);
				ac.addBond(b1);
			}
			else
			{
				b1.setOrder(BondManipulator.createBondOrder(b11));
			}
		}
		else if (b1 != null)
		{
			ac.removeBond(b1);
		}
		
		if (b12 > 0) 
		{
			if (b2 == null)
			{
				b2 = ac.getBuilder().newBond(
					ax1, ay2, BondManipulator.createBondOrder(b12)
				);
				ac.addBond(b2);
			}
			else
			{
				b2.setOrder(BondManipulator.createBondOrder(b12));
			}
		}
		else if (b2 != null)
		{
			ac.removeBond(b2);
		}
		
		if (b21 > 0) 
		{
			if (b3 == null)
			{
				b3 = ac.getBuilder().newBond(
					ax2, ay1, BondManipulator.createBondOrder(b21)
				);
				ac.addBond(b3);
			}
			else
			{
				b3.setOrder(BondManipulator.createBondOrder(b21));
			}
		}
		else if (b3 != null)
		{
			ac.removeBond(b3);
		}

		if (b22 > 0) 
		{
			if (b4 == null)
			{
				b4 = ac.getBuilder().newBond(
					ax2, ay2, BondManipulator.createBondOrder(b22)
				);
				ac.addBond(b4);
			}
			else
			{
				b4.setOrder(BondManipulator.createBondOrder(b22));
			}
		}
		else if (b4 != null)
		{
			ac.removeBond(b4);
		}
		
		logger.debug("a11 a12 a21 a22: " + a11 + " " + a12 + " " + a21 + " " + a22);
		logger.debug("b11 b12 b21 b22: " + b11 + " " + b12 + " " + b21 + " " + b22);
	}

	
	/**
	 * Assigns a starting structure to this generator.
	 *
	 * @param   molecule  a starting structure for this generator
	 */
	public void setMolecule(IMolecule molecule)
	{
		this.molecule = molecule;	
	}


	/**
	 * Returns the molecule which reflects the current state of this
	 * stochastic structure generator.
	 *
	 * @return The molecule    
	 */
	public IMolecule getMolecule()
	{
		return this.molecule;
	}
	
}
