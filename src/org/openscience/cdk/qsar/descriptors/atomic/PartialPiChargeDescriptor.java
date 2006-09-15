/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.qsar.descriptors.atomic;

import java.io.IOException;

import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 *  <p>The calculation of pi partial charges in pi-bonded systems of an heavy 
 *  atom was made by Saller-Gasteiger. It is based on the qualitative concept of resonance and
 *  implemented with the Partal Equalization of Pi-Electronegativity (PEPE).</p>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>maxIterations</td>
 *     <td>0</td>
 *     <td>Number of maximum iterations</td>
 *   </tr>
 * </table>
 *
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-04-15
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:partialPiCharge
 * @see GasteigerPEPEPartialCharges
 */
public class PartialPiChargeDescriptor extends AbstractAtomicDescriptor {

    private GasteigerPEPEPartialCharges pepe = null;
    /**Number of maximum iterations*/
	private int maxIterations = -1;
	/** make a lone pair electron checker. Default true*/
	private boolean lpeChecker = true;


    /**
     *  Constructor for the PartialPiChargeDescriptor object
     */
    public PartialPiChargeDescriptor() { 
    	pepe = new GasteigerPEPEPartialCharges();
    }


    /**
     *  Gets the specification attribute of the PartialPiChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialPiCharge",
            this.getClass().getName(),
            "$Id: PartialPiChargeDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PartialPiChargeDescriptor
     *  object
     *
     *@param  params            1:Number of maximum iterations, 2: checking lone pair electrons
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 2) {
            throw new CDKException("PartialPiChargeDescriptor only expects two parameter");
        }
        if (!(params[0] instanceof Integer) ){
                throw new CDKException("The parameter must be of type Integer");
            }
    
        maxIterations = ((Integer) params[0]).intValue();
        if(params.length == 2){
        	if (!(params[1] instanceof Boolean) )
                throw new CDKException("The parameter must be of type Boolean");
        	lpeChecker = ((Boolean) params[1]).booleanValue();
        }
    }


    /**
     *  Gets the parameters attribute of the PartialPiChargeDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(maxIterations);
        return params;
    }


    /**
     *  The method returns apha partial charges assigned to an heavy atom through Gasteiger Marsili
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *  For this method will be only possible if the heavy atom has single bond.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   Value of the alpha partial charge
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
    	if(lpeChecker){
    		LonePairElectronChecker lpcheck;
			try {
				lpcheck = new LonePairElectronChecker();
	    		lpcheck.newSaturate(ac);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	if (!isCachedAtomContainer(ac)) {
    		if(maxIterations != -1)
    			pepe.setMaxGasteigerIters(maxIterations);
	    	try {
	        	IAtom[] atoms = ac.getAtoms();
	    		for (int i=0; i<atoms.length; i++)
	    			atoms[i].setCharge(0.0);
	        	pepe.assignGasteigerPiPartialCharges(ac, true);
				for (int i=0; i<atoms.length; i++) {
					// assume same order, so mol.getAtom(i) == ac.getAtom(i)
					cacheDescriptorValue(ac.getAtom(i), ac, new DoubleResult(ac.getAtom(i).getCharge()));
				}
	        } catch (Exception ex1) {
	            throw new CDKException("Problems with assignGasteigerPiPartialCharges due to " + ex1.toString(), ex1);
	        }
    	}
    	return getCachedDescriptorValue(atom) != null 
        	? new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), getCachedDescriptorValue(atom)) 
            : null;
    }


    /**
     *  Gets the parameterNames attribute of the PartialPiChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
    	String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the PartialPiChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
    	Integer[] object = {new Integer(0)};
        return object;
    }
}

