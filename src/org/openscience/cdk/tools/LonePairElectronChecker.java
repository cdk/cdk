/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

import java.io.IOException;

import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ILonePair;

/**
* Provides methods for checking whether an atoms lone pair electrons are saturated 
* with respect to a particular atom type. You should ensure that you has checked first
* the atomcontainer ( with ValencyChecker or SaturationChecker). The hydrogen muss be
* also implicit.
* 
* @author         Miguel Rojas
* @cdk.created    2006-04-01
*
* @cdk.keyword    saturation
* @cdk.keyword    atom, valency
*/
public class LonePairElectronChecker {
	
	private LoggingTool logger;
	private String atomTypeList;
	private AtomTypeFactory structgenATF;

	/**
	 * Constructor of the SaturationChecker object
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public LonePairElectronChecker() throws IOException, ClassNotFoundException
	{
		 this("org/openscience/cdk/config/data/valency2_atomtypes.xml");
	}
	/**
	 * Constructor of the SaturationChecker object
	 * 
	 * @param atomTypeList Path of the atomtype to use
	 *  
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public LonePairElectronChecker(String atomTypeList) throws IOException, ClassNotFoundException {
		this.atomTypeList = atomTypeList;
		logger = new LoggingTool(this);
        logger.info("Using configuration file: ", atomTypeList);
	}
	/**
	 * Get the atom Type Factory
	 * 
     * @param builder the ChemObjectBuilder implementation used to construct the AtomType's.
     */
    protected AtomTypeFactory getAtomTypeFactory(IChemObjectBuilder builder) throws CDKException {
        if (structgenATF == null) {
            try {
                structgenATF = AtomTypeFactory.getInstance(atomTypeList, builder);
            } catch (Exception exception) {
                logger.debug(exception);
                throw new CDKException("Could not instantiate AtomTypeFactory!", exception);
            }
        }
        return structgenATF;
    }
    /**
     * Determines of all atoms on the AtomContainer have specified the right number the lone pair electrons.
     */
	public boolean isSaturated(IAtomContainer container) throws CDKException {
        return allSaturated(container);
    }

	/**
     * Determines of all atoms on the AtomContainer have specified the right number the lone pair electrons.
	 */
	public boolean allSaturated(IAtomContainer ac) throws CDKException
	{
        logger.debug("Are all atoms saturated?");
        for (int f = 0; f < ac.getAtomCount(); f++) {
//        	if (!ac.getAtomAt(f).getSymbol().equals("C"))
//        		if(!ac.getAtomAt(f).getSymbol().equals("H"))
        			if (!isSaturated(ac.getAtom(f), ac)) 
        				return false;
        }
        return true;
    }
	/**
	 * Checks wether an Atom is saturated their lone pair electrons by comparing it with known AtomTypes.
     
	 * @return       True, if it's right satured
	 */
	public boolean isSaturated(IAtom atom, IAtomContainer ac) throws CDKException {
		
		IAtomType atomType = getAtomTypeFactory(atom.getBuilder()).getAtomType(atom.getSymbol());
        double bondOrderSum = ac.getBondOrderSum(atom);
		int charge = atom.getFormalCharge();
		int hcount = atom.getHydrogenCount();
		if(hcount == 0){
			java.util.List atomsC = ac.getConnectedAtomsList(atom);
			for(int i = 0 ; i < atomsC.size() ; i++)
				if(((IAtom)atomsC.get(i)).getSymbol().equals("H"))
					hcount++;
			bondOrderSum -= hcount;
		}
		int valency = atomType.getValency();
		
		double nLonePair = (valency - (hcount + bondOrderSum) - charge) / 2;
        try {
        	logger.info("*** Checking saturation of atom "+ atom.getSymbol()+ "" + ac.getAtomNumber(atom) + " ***");
            logger.info("bondOrderSum: " + bondOrderSum);
            logger.info("valency: " + valency);
            logger.info("hcount: " + hcount);
            logger.info("cahrge: " + charge);
        } catch (Exception exc) {
            logger.debug(exc);
        }
        if((double)ac.getConnectedLonePairsCount(atom) != nLonePair){
        	logger.info("*** Bad ! ***");
        	return false;
        }else{
        	logger.info("*** Good ! ***");
            return true;
        }
    }
	/**
	 * Saturates a molecule by setting appropriate number lone pair electrons.
	 */
	public void newSaturate(IAtomContainer atomContainer) throws CDKException {
        logger.info("Saturating atomContainer by adjusting lone pair electrons...");
        boolean allSaturated = allSaturated(atomContainer);
        if (!allSaturated) {
            for(int i=0 ; i < atomContainer.getAtomCount() ; i++ ){
            	newSaturate(atomContainer.getAtom(i), atomContainer);
            }
        }
    }
	/**
	 * Saturates a atom by setting appropriate number lone pair electrons.
	 */
	public void newSaturate(IAtom atom, IAtomContainer ac) throws CDKException {
        logger.info("Saturating atom by adjusting lone pair electrons...");
        boolean isSaturated = isSaturated(atom, ac);
        if (!isSaturated) {
        	int nLonePairI = ac.getConnectedLonePairsCount(atom);
    		IAtomType atomType = getAtomTypeFactory(atom.getBuilder()).getAtomType(atom.getSymbol());
            
            double bondOrderSum = ac.getBondOrderSum(atom);
    		int charge = atom.getFormalCharge();
    		int hcount = atom.getHydrogenCount();
    		if(hcount == 0){
    			java.util.List atomsC = ac.getConnectedAtomsList(atom);
    			for(int i = 0 ; i < atomsC.size() ; i++)
    				if(((IAtom)atomsC.get(i)).getSymbol().equals("H"))
    					hcount++;
    			bondOrderSum -= hcount;
    		}
    		int valency = atomType.getValency();
    		
    		double nLonePair = (valency - (hcount + bondOrderSum) - charge) / 2;
    		ILonePair lp = atom.getBuilder().newLonePair(atom);
			for (int j = 0; j < nLonePair - nLonePairI; j++)
			{
				ac.addLonePair(lp);
			}
            
        }
    }
}
