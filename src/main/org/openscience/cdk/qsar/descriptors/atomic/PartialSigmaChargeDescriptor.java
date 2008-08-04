/* $Revision$ $Author$ $Date$
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  The calculation of sigma partial charges in sigma-bonded systems of an heavy atom
 *  was made by Marsilli-Gasteiger. It is implemented with the Partial Equalization
 *  of Orbital Electronegativity (PEOE).
 *  
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
 * @cdk.dictref qsar-descriptors:partialSigmaCharge
 * @see GasteigerMarsiliPartialCharges
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptorTest")
public class PartialSigmaChargeDescriptor extends AbstractAtomicDescriptor {

    private static final String[] names = {"partialSigmaCharge"};

    private GasteigerMarsiliPartialCharges peoe = null;
    /**Number of maximum iterations*/
	private int maxIterations;

    /**
     *  Constructor for the PartialSigmaChargeDescriptor object
     */
    public PartialSigmaChargeDescriptor() { 
        peoe = new GasteigerMarsiliPartialCharges();
    }
    
    /**
     *  Gets the specification attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialSigmaCharge",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@param  params            Number of maximum iterations
     *@exception  CDKException  Description of the Exception
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("PartialSigmaChargeDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter 1 must be of type Integer");
        }
        maxIterations = (Integer) params[0];
    }


    /**
     *  Gets the parameters attribute of the PartialSigmaChargeDescriptor object
     *
     *@return    The parameters value
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = maxIterations;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
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
    	// FIXME: for now I'll cache the original charges, and restore them at the end of this method
    	Double originalCharge = atom.getCharge();
        if (!isCachedAtomContainer(ac)) {
            IMolecule mol = atom.getBuilder().newMolecule(ac);
            if (maxIterations != 0) peoe.setMaxGasteigerIters(maxIterations);
            try {
                peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);

                for (int i = 0; i < ac.getAtomCount(); i++) {
                    // assume same order, so mol.getAtom(i) == ac.getAtom(i)
                    cacheDescriptorValue(ac.getAtom(i), ac, new DoubleResult(mol.getAtom(i).getCharge()));
                }
            } catch (Exception e) {
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                        new DoubleResult(Double.NaN),
                        names, e);
            }
        }
        atom.setCharge(originalCharge);

        return getCachedDescriptorValue(atom) != null
                ? new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                getCachedDescriptorValue(atom),
                names)
                : null;
    }


    /**
     *  Gets the parameterNames attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
    	if ("maxIterations".equals(name)) return Integer.MAX_VALUE;
        return null;
    }
}

