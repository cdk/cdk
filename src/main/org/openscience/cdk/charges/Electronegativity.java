/*  $Revision: 10199 $ $Author: rajarshi $ $Date: 2008-02-21 19:19:31 +0100 (Thu, 21 Feb 2008) $
 *
 *  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.charges;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Calculation of the electronegativity of orbitals of a molecule 
 * by the method Gasteiger based on electronegativity is given by X = a + bq + c(q*q).
 *
 * @author         Miguel Rojas Cherto
 * @cdk.created    2008-104-31
 * @cdk.module  charges
 * @cdk.keyword electronegativity
 */
@TestClass("org.openscience.cdk.charges.ElectronegativityTest")
public class Electronegativity {
	
    private GasteigerMarsiliPartialCharges peoe = null;
    
	/**Number of maximum iterations*/
	private int maxI = 6;
    /**Number of maximum resonance structures*/
	private int maxRS = 50;

	private IMolecule molSigma;
	private IAtomContainer acOldS;
	private double[] marsiliFactors;
	
    /**
     * Constructor for the PiElectronegativity object.
     */
    public Electronegativity() {
    	this(6, 50);
    }

    /**
     * Constructor for the Electronegativity object.
     * 
     * @param maxIterations         The maximal number of Iteration  
     * @param maxResonStruc         The maximal number of Resonance Structures
     */
    public Electronegativity( int maxIterations, int maxResonStruc) {
    	peoe = new GasteigerMarsiliPartialCharges();
    	maxI = maxIterations;
    	maxRS = maxResonStruc;
    }
    /**
     * calculate the electronegativity of orbitals sigma.
     *
     * @param ac                    IAtomContainer
     * @param atom                  atom for which effective atom electronegativity should be calculated     
     * 
     * @return piElectronegativity
     */
	@TestMethod("testCalculateSigmaElectronegativity_IAtomContainer_IAtom")
    public double calculateSigmaElectronegativity(IAtomContainer ac,
                                               IAtom atom){

    	return calculateSigmaElectronegativity(ac, atom, maxI, maxRS);
	}
    /**
     * calculate the electronegativity of orbitals sigma.
     *
     * @param ac                    IAtomContainer
     * @param atom                  atom for which effective atom electronegativity should be calculated     
     * @param maxIterations         The maximal number of Iteration  
     * @param maxResonStruc         The maximal number of Resonance Structures
     * 
     * @return piElectronegativity
     */
	@TestMethod("testCalculateSigmaElectronegativity_IAtomContainer_IAtom_Int_Int")
    public double calculateSigmaElectronegativity(IAtomContainer ac,
                                               IAtom atom,
                                               int maxIterations,
                                               int maxResonStruc) {
        maxI = maxIterations;
    	maxRS = maxResonStruc;
    	
    	double electronegativity = 0;

        try {
        	if(!ac.equals(acOldS)){
        		molSigma = ac.getBuilder().newMolecule(ac);
        		peoe.setMaxGasteigerIters(maxI);
	    		peoe.assignGasteigerMarsiliSigmaPartialCharges(molSigma, true);
	    		marsiliFactors = peoe.assignGasteigerSigmaMarsiliFactors(molSigma);
	        	
	    		acOldS = ac;
        	}        	
        	int stepSize = peoe.getStepSize();
            int atomPosition =ac.getAtomNumber(atom);
            int start = (stepSize * (atomPosition) + atomPosition);
            
            electronegativity = ((marsiliFactors[start]) + (molSigma.getAtom(atomPosition).getCharge() * marsiliFactors[start + 1]) + (marsiliFactors[start + 2] * ((molSigma.getAtom(atomPosition).getCharge() * molSigma.getAtom(atomPosition).getCharge()))));
            return electronegativity;
            
        } catch (Exception e) {
			e.printStackTrace();
		}

		return electronegativity;        
    }
    /**
     * set the maximal number of Iterations.
     * 
     * @param maxIterations The number maximal of iterations
     */
	@TestMethod("testSetMaxIterations_Int")
    public void setMaxIterations(int maxIterations){
    	maxI = maxIterations;
    }

    /**
     * set the maximal number of resonance structures.
     * 
     * @param maxResonStruc The number maximal of resonance structures
     */
	@TestMethod("testSetMaxResonStruc_Int")
    public void setMaxResonStruc(int maxResonStruc){
    	maxRS = maxResonStruc;
    }
    
    /**
     * get the maximal number of Iterations.
     * 
     * @return The number maximal of iterations
     */
	@TestMethod("testGetMaxIterations")
    public int getMaxIterations(){
    	return maxI;
    }

    /**
     * get the maximal number of resonance structures.
     * 
     * @return The number maximal of resonance structures
     */
	@TestMethod("testGetMaxResonStruc")
    public int getMaxResonStruc(){
    	return maxRS;
    }
    
    
}

