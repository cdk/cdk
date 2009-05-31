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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.math.MathTools;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * The VicinitySampler is a generator of Constitutional Isomers. It needs to be 
 * provided with a starting constitution and it makes random moves in 
 * constitutional space from there. This generator was first suggested by 
 * Faulon {@cdk.cite FAU96}.
 *
 * @cdk.keyword  structure generator
 * @cdk.module   structgen
 * @cdk.githash
 * @cdk.bug      1632610
 */
public class VicinitySampler {
	
	private final static LoggingTool logger = new LoggingTool(VicinitySampler.class);
	
	int molCounter = 0;

	/**
	 * Choose any possible quadruple of the set of atoms 
	 * in ac and establish all of the possible bonding schemes according to 
	 * Faulon's equations.
	 */
	public static List sample(IMolecule ac) {
		logger.debug("RandomGenerator->mutate() Start");
		List structures = new ArrayList();
		
		int nrOfAtoms = ac.getAtomCount();
		double a11 = 0, a12 = 0, a22 = 0, a21 = 0;
		double b11 = 0, lowerborder = 0, upperborder = 0;
		double b12 = 0; 
		double b21 = 0; 
		double b22 = 0;
		double[] cmax = new double[4]; 
		double[] cmin = new double[4];
		IAtomContainer newAc = null;

		IAtom ax1 = null, ax2 = null, ay1 = null, ay2  = null;
		IBond b1 = null, b2 = null, b3 = null, b4 = null;
		//int[] choices = new int[3];
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
							lowerborder = MathTools.max(cmax);
							upperborder = MathTools.min(cmin);
							for (b11 = lowerborder; b11 <= upperborder; b11++)
							{
								if (b11 != a11)
								{
									
									b12 = a11 + a12 - b11;
									b21 = a11 + a21 - b11;
									b22 = a22 - a11 + b11;
									logger.debug("Trying atom combination : " + x1 + ":" + x2 + ":"+ y1 + ":"+ y2);
									try {
										newAc = (IAtomContainer)ac.clone();
										change(newAc, x1, y1, x2, y2, b11, b12, b21, b22);
										if (ConnectivityChecker.isConnected(newAc))
										{
											structures.add(newAc);
										}
										else
										{
											logger.debug("not connected");	
										}
									} catch (CloneNotSupportedException e) {
										logger.error("Cloning exception: " + e.getMessage());
										logger.debug(e);
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

	private static IAtomContainer change(IAtomContainer ac, int x1, int y1, int x2, int y2, double b11, double b12, double b21, double b22)
	{
		IAtom ax1 = null, ax2 = null, ay1 = null, ay2 = null;
		IBond b1 = null, b2 = null, b3 = null, b4 = null;
		try {
			ax1 = ac.getAtom(x1);
			ax2 = ac.getAtom(x2);
			ay1 = ac.getAtom(y1);
			ay2 = ac.getAtom(y2);
		} catch(Exception exc) {
			logger.debug(exc);	
		}
		b1 = ac.getBond(ax1, ay1);
		b2 = ac.getBond(ax1, ay2);
		b3 = ac.getBond(ax2, ay1);
		b4 = ac.getBond(ax2, ay2);									
		if (b11 > 0)
		{
			if (b1 == null)
			{
				logger.debug("no bond " + x1 + "-" + y1 + ". Adding it with order " + b11);
				b1 = ac.getBuilder().newBond(ax1, ay1, BondManipulator.createBondOrder(b11));
				ac.addBond(b1);
			}
			else
			{
				b1.setOrder(BondManipulator.createBondOrder(b11));
				logger.debug("Setting bondorder for " + x1 + "-" + y1 + " to " + b11);
			}
		}
		else if (b1 != null)
		{
			ac.removeBond(b1);
			logger.debug("removing bond " + x1 + "-" + y1);			
		}
		
		if (b12 > 0) 
		{
			if (b2 == null)
			{
				logger.debug("no bond " + x1 + "-" + y2 + ". Adding it with order " + b12);				
				b2 = ac.getBuilder().newBond(
					ax1, ay2, BondManipulator.createBondOrder(b12)
				);
				ac.addBond(b2);
			}
			else
			{
				b2.setOrder(BondManipulator.createBondOrder(b12));
				logger.debug("Setting bondorder for " + x1 + "-" + y2 + " to " + b12);
			}
		}
		else if (b2 != null)
		{
			ac.removeBond(b2);
			logger.debug("removing bond " + x1 + "-" + y2);			
		}
		
		if (b21 > 0) 
		{
			if (b3 == null)
			{
				logger.debug("no bond " + x2 + "-" + y1 + ". Adding it with order " + b21);
				b3 = ac.getBuilder().newBond(ax2, ay1, BondManipulator.createBondOrder(b21));
				ac.addBond(b3);
			}
			else
			{
				b3.setOrder(BondManipulator.createBondOrder(b21));
				logger.debug("Setting bondorder for " + x2 + "-" + y1 + " to " + b21);
			}
		}
		else if (b3 != null)
		{
			ac.removeBond(b3);
			logger.debug("removing bond " + x2 + "-" + y1);
		}

		if (b22 > 0) 
		{
			if (b4 == null)
			{
				logger.debug("no bond " + x2 + "-" + y2 + ". Adding it  with order " + b22);
				b4 = ac.getBuilder().newBond(ax2, ay2, BondManipulator.createBondOrder(b22));
				ac.addBond(b4);
			}
			else
			{
				b4.setOrder(BondManipulator.createBondOrder(b22));
				logger.debug("Setting bondorder for " + x2 + "-" + y2 + " to " + b22);
			}
		}
		else if (b4 != null)
		{
			ac.removeBond(b4);
			logger.debug("removing bond " + x2 + "-" + y2);
		}
		return ac;
	}
	
}
