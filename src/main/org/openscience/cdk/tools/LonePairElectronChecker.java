/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *                    2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ILonePair;

/**
 * Provides methods for checking whether an atoms lone pair electrons are saturated 
 * with respect to a particular atom type.
 * 
 * @author         Miguel Rojas
 * @cdk.svnrev     $Revision$
 * @cdk.created    2006-04-01
 *
 * @cdk.keyword    saturation
 * @cdk.keyword    atom, valency
 * @cdk.module     standard
 */
public class LonePairElectronChecker {
	
	private LoggingTool logger;
	private static AtomTypeFactory factory;

	/**
	 * Constructor of the LonePairElectronChecker object.
	 */
	public LonePairElectronChecker() {
		logger = new LoggingTool(LonePairElectronChecker.class);
	}
	
	private void createAtomTypeFactory(IChemObjectBuilder builder) {
		if (factory == null) {
			factory = AtomTypeFactory.getInstance(
				"org/openscience/cdk/dict/data/cdk-atom-types.owl", builder
			);
		}
	}
	
    /**
     * Determines of all atoms on the AtomContainer have the
     * right number the lone pair electrons.
     */
	public boolean isSaturated(IAtomContainer container) throws CDKException {
        return allSaturated(container);
    }

	/**
     * Determines of all atoms on the AtomContainer have
     * the right number the lone pair electrons.
	 */
	public boolean allSaturated(IAtomContainer ac) throws CDKException
	{
        logger.debug("Are all atoms saturated?");
        for (int f = 0; f < ac.getAtomCount(); f++) {
        	if (!isSaturated(ac.getAtom(f), ac)) 
        		return false;
        }
        return true;
    }
	
	/**
	 * Checks if an Atom is saturated their lone pair electrons
	 * by comparing it with known AtomTypes.
	 * 
	 * @return       True, if it's right saturated
	 */
	public boolean isSaturated(IAtom atom, IAtomContainer ac) throws CDKException {
		createAtomTypeFactory(ac.getBuilder());
		IAtomType atomType = factory.getAtomType(atom.getAtomTypeName());
		int lpCount = (Integer)atomType.getProperty(CDKConstants.LONE_PAIR_COUNT);
		int foundLPCount = ac.getConnectedLonePairsCount(atom);
        return foundLPCount >= lpCount;
    }
	
	/**
	 * Saturates a molecule by setting appropriate number lone pair electrons.
	 */
	public void saturate(IAtomContainer atomContainer) throws CDKException {
        logger.info("Saturating atomContainer by adjusting lone pair electrons...");
        boolean allSaturated = allSaturated(atomContainer);
        if (!allSaturated) {
            for(int i=0 ; i < atomContainer.getAtomCount() ; i++ ){
            	saturate(atomContainer.getAtom(i), atomContainer);
            }
        }
    }
	
	/**
	 * Saturates an IAtom by adding the appropriate number lone pairs.
	 */
	public void saturate(IAtom atom, IAtomContainer ac) throws CDKException {
        logger.info("Saturating atom by adjusting lone pair electrons...");
		IAtomType atomType = factory.getAtomType(atom.getAtomTypeName());
		int lpCount = (Integer)atomType.getProperty(CDKConstants.LONE_PAIR_COUNT);
		int missingLPs = lpCount - ac.getConnectedLonePairsCount(atom);
		
		for (int j = 0; j < missingLPs; j++) {
			ILonePair lp = atom.getBuilder().newLonePair(atom);
			ac.addLonePair(lp);
		}
    }
	
}
