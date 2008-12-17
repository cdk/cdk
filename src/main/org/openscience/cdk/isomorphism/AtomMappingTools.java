/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.isomorphism;

import java.util.List;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module    standard
 * @cdk.svnrev    $Revision$
 */
public class AtomMappingTools {

	private static LoggingTool logger = new LoggingTool(AtomMappingTools.class);
	
    /**
     * Returns a Map with the AtomNumbers, the first number corresponds to the first (or the largest
     * AtomContainer) atomContainer.
     * <p/>
     * Only for similar and aligned molecules with coordinates!
     *
     * @param firstAtomContainer  the (largest) first aligned AtomContainer which is the reference
     * @param secondAtomContainer the second aligned AtomContainer
     * @return a Map of the mapped atoms
     * @throws CDKException if there is an error in the UniversalIsomorphismTester
     */
    public static Map<Integer,Integer> mapAtomsOfAlignedStructures(IAtomContainer firstAtomContainer, 
            IAtomContainer secondAtomContainer, Map<Integer,Integer> mappedAtoms) throws CDKException {
        //logger.debug("**** GT MAP ATOMS ****");
        //Map atoms onto each other
        if (firstAtomContainer.getAtomCount() < 1 & secondAtomContainer.getAtomCount() < 1) {
            return mappedAtoms;
        }
        RMap map;
        IAtom atom1;
        IAtom atom2;
        List list;
        try {
            list = UniversalIsomorphismTester.getSubgraphAtomsMap(firstAtomContainer, secondAtomContainer);
            //logger.debug("ListSize:"+list.size());
            for (int i = 0; i < list.size(); i++) {
                map = (RMap) list.get(i);
                atom1 = firstAtomContainer.getAtom(map.getId1());
                atom2 = secondAtomContainer.getAtom(map.getId2());
                if (checkAtomMapping(firstAtomContainer, secondAtomContainer, firstAtomContainer.getAtomNumber(atom1), secondAtomContainer.getAtomNumber(atom2)))
                {
                    mappedAtoms.put(Integer.valueOf(firstAtomContainer.getAtomNumber(atom1)), Integer.valueOf(secondAtomContainer.getAtomNumber(atom2)));
//                    logger.debug("#:"+countMappedAtoms+" Atom:"+firstAtomContainer.getAtomNumber(atom1)+" is mapped to Atom:"+secondAtomContainer.getAtomNumber(atom2));
                } else {
                    logger.error("Error: Atoms are not similar !!");
                }
            }
        } catch (CDKException e) {
            throw new CDKException("Error in UniversalIsomorphismTester due to:" + e.toString());
        }
        return mappedAtoms;
    }

	private static boolean checkAtomMapping(IAtomContainer firstAC, IAtomContainer secondAC, int posFirstAtom, int posSecondAtom){
		IAtom firstAtom=firstAC.getAtom(posFirstAtom);
		IAtom secondAtom=secondAC.getAtom(posSecondAtom);
		if (firstAtom.getSymbol().equals(secondAtom.getSymbol()) && firstAC.getConnectedAtomsList(firstAtom).size() == secondAC.getConnectedAtomsList(secondAtom).size() &&
				firstAtom.getBondOrderSum() == secondAtom.getBondOrderSum() &&
				firstAtom.getMaxBondOrder() == secondAtom.getMaxBondOrder() 
		        ){
			return true;
		}else {
			return false;
		}
	}
	
}
