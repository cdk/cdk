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
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  <p>The calculation of total partial charges of an heavy atom is based on 
 *  Partial Equalization of Electronegativity method (PEOE-PEPE) from Gasteiger. </p>
 *  <p>They are obtained by summation of the results of the calculations on 
 *  sigma- and pi-charges. </p>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-04-11
 * @cdk.module  qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:PartialTChargePEOE
 *
 * @see         GasteigerMarsiliPartialCharges
 * @see         GasteigerPEPEPartialCharges
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.PartialTChargePEOEDescriptorTest")
public class PartialTChargePEOEDescriptor extends AbstractAtomicDescriptor {

    private static final String[] names = {"pepeT"};

    private GasteigerMarsiliPartialCharges peoe = null;
    private GasteigerPEPEPartialCharges pepe = null;
    
	/**Number of maximum iterations*/
	private int maxIterations = -1;
    /**Number of maximum resonance structures*/
	private int maxResonStruc = -1;
	/** make a lone pair electron checker. Default true*/
	private boolean lpeChecker = true;

    /**
     *  Constructor for the PartialTChargePEOEDescriptor object
     */
    public PartialTChargePEOEDescriptor() {
        peoe = new GasteigerMarsiliPartialCharges();
    	pepe = new GasteigerPEPEPartialCharges();
    }


    /**
     *  Gets the specification attribute of the PartialTChargePEOEDescriptor  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#PartialTChargePEOE",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     * This descriptor does not have any parameter to be set.
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
     *  Gets the parameters attribute of the PartialTChargePEOEDescriptor
     *  object
     *
     *@return    The parameters value
     *@see #setParameters
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


    /**
     *  The method returns partial total charges assigned to an heavy atom through PEOE method.
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     * @param  atom              The IAtom for which the DescriptorValue is requested
     * @param  ac                AtomContainer
     * @return                   an array of doubles with partial charges of [heavy, proton_1 ... proton_n]
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
                new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                        new DoubleResult(Double.NaN), names, e);
            }

            if (lpeChecker) {
                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                try {
                    lpcheck.saturate(ac);
                } catch (CDKException e) {
                    new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                            new DoubleResult(Double.NaN), names, e);
                }
            }
    		
        	if(maxIterations != -1) peoe.setMaxGasteigerIters(maxIterations);
        	if(maxIterations != -1)	pepe.setMaxGasteigerIters(maxIterations);
    		if(maxResonStruc != -1)	pepe.setMaxResoStruc(maxResonStruc);
    		
	        try {
				peoe.assignGasteigerMarsiliSigmaPartialCharges(ac, true);
				List<Double> peoeAtom = new ArrayList<Double>();
				for(Iterator<IAtom> it = ac.atoms().iterator(); it.hasNext();)
					peoeAtom.add(it.next().getCharge());
				
				for(Iterator<IAtom> it = ac.atoms().iterator(); it.hasNext();)
					it.next().setCharge(0.0);

				pepe.assignGasteigerPiPartialCharges(ac, true);
				for(int i = 0; i < ac.getAtomCount() ; i++)
                    cacheDescriptorValue(ac.getAtom(i), ac, new DoubleResult(peoeAtom.get(i) + ac.getAtom(i).getCharge()));

            } catch (Exception e) {
                new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                        new DoubleResult(Double.NaN), names, e);
            }
        }
    	// restore original props
    	atom.setCharge(originalCharge);
    	atom.setAtomTypeName(originalAtomtypeName);
    	atom.setFormalNeighbourCount(originalNeighborCount);
    	atom.setValency(originalValency);
    	atom.setHybridization(originalHybridization);

        return getCachedDescriptorValue(atom) != null
                ? new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                getCachedDescriptorValue(atom), names)
                : null;
    }


    /**
     *  Gets the parameterNames attribute of the PartialTChargePEOEDescriptor
     *  object
     *
     * @return    The parameterNames value
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
     *  Gets the parameterType attribute of the PartialTChargePEOEDescriptor
     *  object
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
    	if ("maxIterations".equals(name)) return Integer.MAX_VALUE;
    	if ("lpeChecker".equals(name)) return Boolean.TRUE;
    	if ("maxResonStruc".equals(name)) return Integer.MAX_VALUE;
        return null;
    }
}

