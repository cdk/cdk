/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 *  
 */

package org.openscience.cdk.structgen;

import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.ConnectivityChecker;

/**
 * RandomGenerator is a generator of Constitutional Isomers. It needs to be 
 * provided with a starting constitution and it makes random moves in 
 * constitutional space from there. 
 * This generator was first suggested by J.-L. Faulon {@cdk.cite FAU96}.
 *
 * @cdk.keyword structure generator
 */
 
public class VicinitySampler
{
	private Molecule proposedStructure = null;
	private Molecule molecule = null;
	private Molecule trial = null;
	private ConnectivityChecker cc = null; 
	private Vector bonds = null;
	public boolean debug = false;
	private int[] correctBondOrderSums;
	int molCounter = 0;

	/**
	 * The empty contructor.
	 */
	public VicinitySampler()
	{
		cc = new ConnectivityChecker();
		trial = new Molecule();
	}


	/**
	 * Constructs a RandomGenerator with a given starting structure.
	 *
	 * @param   molecule  The starting structure
	 */
	public VicinitySampler(Molecule molecule)
	{
		this ();
		setMolecule(molecule);
	}

	/**
	 * Choose any possible quadruple of the set of atoms 
	 * in ac and establish all of the possible bonding schemes according to 
	 * Faulon's equations.
	 */
	public Vector sample(AtomContainer ac)
	{
		if(debug) if (debug) System.out.println("RandomGenerator->mutate() Start");
		Vector structures = new Vector();
		
		int nrOfAtoms = ac.getAtomCount();
		double a11 = 0, a12 = 0, a22 = 0, a21 = 0;
		double b11 = 0, lowerborder = 0, upperborder = 0;
		double b12 = 0; 
		double b21 = 0; 
		double b22 = 0;
		double[] cmax = new double[4]; 
		double[] cmin = new double[4];
		AtomContainer newAc = null;

		org.openscience.cdk.interfaces.IAtom ax1 = null, ax2 = null, ay1 = null, ay2  = null;
		org.openscience.cdk.interfaces.IBond b1 = null, b2 = null, b3 = null, b4 = null;
		int[] choices = new int[3];
		/* We need at least two non-zero bonds in order to be successful */
		int nonZeroBondsCounter = 0;
		for (int x1 = 0; x1 < nrOfAtoms; x1++)
		{
			for (int x2 = x1 + 1; x2 < nrOfAtoms; x2++)
			{
				for (int y1 = x2 + 1; y1 < nrOfAtoms; y1++)
				{
					for (int y2 = y1 + 1; y2 < nrOfAtoms; y2++)
					{
						nonZeroBondsCounter = 0;
						ax1 = ac.getAtomAt(x1);
						ay1 = ac.getAtomAt(y1);
						ax2 = ac.getAtomAt(x2);
						ay2 = ac.getAtomAt(y2);
												
						/* Get four bonds for these four atoms */
						
						b1 = ac.getBond(ax1, ay1);
						if (b1 != null)
						{
							a11 = b1.getOrder();
							nonZeroBondsCounter ++;				
						}
						else
						{
							a11 = 0;
						}
						
						b2 = ac.getBond(ax1, ay2);
						if (b2 != null)
						{
							a12 = b2.getOrder();
							nonZeroBondsCounter ++;				
						}
						else
						{
							a12 = 0;
						}
		
						b3 = ac.getBond(ax2, ay1);
						if (b3 != null)
						{
							a21 = b3.getOrder();
							nonZeroBondsCounter ++;				
						}
						else
						{
							a21 = 0;
						}
						
						b4 = ac.getBond(ax2, ay2);									
						if (b4 != null)
						{
							a22 = b4.getOrder();
							nonZeroBondsCounter ++;
						}
						else
						{
							a22 = 0;
						}
						if (nonZeroBondsCounter > 1)
						{
							/* Compute the range for b11 (see Faulons formulae for details) */
							
							cmax[0] = 0;
							cmax[1] = a11 - a22;
							cmax[2] = a11 + a12 - 3;
							cmax[3] = a11 + a21 - 3;
							cmin[0] = 3;
							cmin[1] = a11 + a12;
							cmin[2] = a11 + a21;
							cmin[3] = a11 - a22 + 3;
							lowerborder = max(cmax);
							upperborder = min(cmin);
							for (b11 = lowerborder; b11 <= upperborder; b11++)
							{
								if (b11 != a11)
								{
									
									b12 = a11 + a12 - b11;
									b21 = a11 + a21 - b11;
									b22 = a22 - a11 + b11;
									if (debug) System.out.println("Trying atom combination : " + x1 + ":" + x2 + ":"+ y1 + ":"+ y2);
									newAc = (AtomContainer)ac.clone();
									change(newAc, x1, y1, x2, y2, b11, b12, b21, b22);
									if (ConnectivityChecker.isConnected(newAc))
									{
										structures.add(newAc);
									}
									else
									{
										if (debug) System.out.println("not connected");	
									}
								}
							}
						}
					}
				}
			}
		}
		return structures;
	}

	/**
	 * Analog of Math.max that returns the largest int value in an array of ints.
	 *
	 * @param   values  the values to be searched for the largest value among them
	 * @return   the largest value among a set of given values  
	 */
	protected double max(double[] values)
	{
		double max = values[0];
		for (int f = 0; f < values.length; f++)
		{
			if (values[f] > max)
			{
				max = values[f];
			}
		}
		return max;
	}

	/**
	 * Analog of Math.min that returns the largest int value in an array of ints.
	 *
	 * @param   values  the values to be searched for the smallest value among them
	 * @return   the smallest value among a set of given values  
	 */
	protected double min(double[] values)
	{
		double min = values[0];
		for (int f = 0; f < values.length; f++)
		{
			if (values[f] < min)
			{
				min = values[f];
			}
		}
		return min;
	}

	private AtomContainer change(AtomContainer ac, int x1, int y1, int x2, int y2, double b11, double b12, double b21, double b22)
	{
		org.openscience.cdk.interfaces.IAtom ax1 = null, ax2 = null, ay1 = null, ay2 = null;
		org.openscience.cdk.interfaces.IBond b1 = null, b2 = null, b3 = null, b4 = null;
		if (debug) System.out.println("About to make modification " + molCounter);
		molCounter ++;
		if (debug) System.out.println("Changes for molecule no. " + molCounter);
		try
		{
			ax1 = ac.getAtomAt(x1);
			ax2 = ac.getAtomAt(x2);
			ay1 = ac.getAtomAt(y1);
			ay2 = ac.getAtomAt(y2);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();	
		}
		b1 = ac.getBond(ax1, ay1);
		b2 = ac.getBond(ax1, ay2);
		b3 = ac.getBond(ax2, ay1);
		b4 = ac.getBond(ax2, ay2);									
		if (b11 > 0)
		{
			if (b1 == null)
			{
				if (debug) System.out.println("no bond " + x1 + "-" + y1 + ". Adding it with order " + b11);
				b1 = new Bond(ax1, ay1, b11);
				ac.addBond(b1);
			}
			else
			{
				b1.setOrder(b11);
				if (debug) System.out.println("Setting bondorder for " + x1 + "-" + y1 + " to " + b11);
			}
		}
		else if (b1 != null)
		{
			ac.removeElectronContainer(b1);
			if (debug) System.out.println("removing bond " + x1 + "-" + y1);			
		}
		
		if (b12 > 0) 
		{
			if (b2 == null)
			{
				if (debug) System.out.println("no bond " + x1 + "-" + y2 + ". Adding it with order " + b12);				
				b2 = new Bond(ax1, ay2, b12);
				ac.addBond(b2);
			}
			else
			{
				b2.setOrder(b12);
				if (debug) System.out.println("Setting bondorder for " + x1 + "-" + y2 + " to " + b12);
			}
		}
		else if (b2 != null)
		{
			ac.removeElectronContainer(b2);
			if (debug) System.out.println("removing bond " + x1 + "-" + y2);			
		}
		
		if (b21 > 0) 
		{
			if (b3 == null)
			{
				if (debug) System.out.println("no bond " + x2 + "-" + y1 + ". Adding it with order " + b21);
				b3 = new Bond(ax2, ay1, b21);
				ac.addBond(b3);
			}
			else
			{
				b3.setOrder(b21);
				if (debug) System.out.println("Setting bondorder for " + x2 + "-" + y1 + " to " + b21);
			}
		}
		else if (b3 != null)
		{
			ac.removeElectronContainer(b3);
			if (debug) System.out.println("removing bond " + x2 + "-" + y1);
		}

		if (b22 > 0) 
		{
			if (b4 == null)
			{
				if (debug) System.out.println("no bond " + x2 + "-" + y2 + ". Adding it  with order " + b22);
				b4 = new Bond(ax2, ay2, b22);
				ac.addBond(b4);
			}
			else
			{
				b4.setOrder(b22);
				if (debug) System.out.println("Setting bondorder for " + x2 + "-" + y2 + " to " + b22);
			}
		}
		else if (b4 != null)
		{
			ac.removeElectronContainer(b4);
			if (debug) System.out.println("removing bond " + x2 + "-" + y2);
		}
		return ac;
	}
	
	
	/**
	 * Assigns a starting structure to this generator.
	 *
	 * @param   molecule  a starting structure for this generator
	 */
	public void setMolecule(Molecule molecule)
	{
		this.molecule = molecule;	
	}


	/**
	 * Returns the molecule which reflects the current state of this
	 * Stochastic Structure Generator.
	 *
	 * @return The molecule    
	 */
	public Molecule getMolecule()
	{
		return this.molecule;
	}
	
}
