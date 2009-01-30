/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  <p>The calculation of pi partial charges in pi-bonded systems of an heavy 
 *  atom was made by Saller-Gasteiger. It is based on the qualitative concept of resonance and
 *  implemented with the Partial Equalization of Pi-Electronegativity (PEPE).</p>
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
 * @cdk.module  qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:partialPiCharge
 * @see         GasteigerPEPEPartialCharges
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptorTest")
public class PartialPiChargeDescriptor extends AbstractAtomicDescriptor {

    private static final String[] names = {"pepe"};

    private GasteigerPEPEPartialCharges pepe = null;
    /**Number of maximum iterations*/
	private int maxIterations = -1;
    /**Number of maximum resonance structures*/
	private int maxResonStruc = -1;
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
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialPiCharge",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PartialPiChargeDescriptor
     *  object
     *
     *@param  params            1:Number of maximum iterations, 2: checking lone pair electrons, 3: 
     *							number of maximum resonance structures to be searched.
     *@exception  CDKException  Description of the Exception
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 3) 
            throw new CDKException("PartialPiChargeDescriptor only expects three parameter");
        
        if (!(params[0] instanceof Integer) )
                throw new CDKException("The parameter must be of type Integer");
	        maxIterations = (Integer) params[0];
	        
	    if(params.length > 1 && params[1] != null){
        	if (!(params[1] instanceof Boolean) )
                throw new CDKException("The parameter must be of type Boolean");
        	lpeChecker = (Boolean) params[1];
        }
	    
	    if(params.length > 2 && params[2] != null){
        	if (!(params[2] instanceof Integer) )
                throw new CDKException("The parameter must be of type Integer");
        	maxResonStruc = (Integer) params[2];
        }
    }


    /**
     *  Gets the parameters attribute of the PartialPiChargeDescriptor object
     *
     *@return    The parameters value
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[3];
        params[0] = maxIterations;
        params[1] = lpeChecker;
        params[2] = maxResonStruc;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(Double.NaN),
                names, e);
    }

    /**
     *  The method returns apha partial charges assigned to an heavy atom through Gasteiger Marsili
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *  For this method will be only possible if the heavy atom has single bond.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   Value of the alpha partial charge
     */
    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {
    	// FIXME: for now I'll cache a few modified atomic properties, and restore them at the end of this method
    	Double originalCharge = atom.getCharge();
    	String originalAtomtypeName = atom.getAtomTypeName();
    	Integer originalNeighborCount = atom.getFormalNeighbourCount();
    	Integer originalValency = atom.getValency();
    	IAtomType.Hybridization originalHybridization = atom.getHybridization();
    	if (!isCachedAtomContainer(ac)) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }

            if(lpeChecker){
    			LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                try {
                    lpcheck.saturate(ac);
                } catch (CDKException e) {
                    return getDummyDescriptorValue(e);
                }
            }
        	
    		if(maxIterations != -1)
    			pepe.setMaxGasteigerIters(maxIterations);
    		if(maxResonStruc != -1)
    			pepe.setMaxResoStruc(maxResonStruc);
	    	try {
	    		for (int i=0; i<ac.getAtomCount(); i++)
	    			ac.getAtom(i).setCharge(0.0);
	        	pepe.assignGasteigerPiPartialCharges(ac, true);
				for (int i=0; i<ac.getAtomCount(); i++) {
					// assume same order, so mol.getAtom(i) == ac.getAtom(i)
					cacheDescriptorValue(ac.getAtom(i), ac, new DoubleResult(ac.getAtom(i).getCharge()));
				}
	        } catch (Exception exception) {
	            return getDummyDescriptorValue(exception);
	        }
    	}
    	// restore original props
    	atom.setCharge(originalCharge);
    	atom.setAtomTypeName(originalAtomtypeName);
    	atom.setFormalNeighbourCount(originalNeighborCount);
    	atom.setValency(originalValency);
    	atom.setHybridization(originalHybridization);

    	return getCachedDescriptorValue(atom) != null 
        	? new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), getCachedDescriptorValue(atom),
                names)
            : null;
    }


    /**
     *  Gets the parameterNames attribute of the PartialPiChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
    	String[] params = new String[3];
        params[0] = "maxIterations";
        params[1] = "lpeChecker";
        params[2] = "maxResonStruc";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the PartialPiChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
    	if ("maxIterations".equals(name)) return Integer.MAX_VALUE;
    	if ("lpeChecker".equals(name)) return Boolean.TRUE;
    	if ("maxResonStruc".equals(name)) return Integer.MAX_VALUE;
        return null;
    }
}

