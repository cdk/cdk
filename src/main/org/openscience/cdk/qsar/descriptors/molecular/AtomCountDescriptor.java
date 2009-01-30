/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

/**
 * IDescriptor based on the number of atoms of a certain element type.
 *
 * It is
 * possible to use the wild card symbol '*' as element type to get the count of
 * all atoms.
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>elementName</td>
 *     <td>*</td>
 *     <td>Symbol of the element we want to count</td>
 *   </tr>
 * </table>
 *
 * Returns a single value with name <i>nX</i> where <i>X</i> is the atomic symbol.  If *
 * is specified then the name is <i>nAtom</i>
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomCount
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptorTest")
public class AtomCountDescriptor implements IMolecularDescriptor {

    private String elementName = "*";


    /**
     *  Constructor for the AtomCountDescriptor object.
     */
    public AtomCountDescriptor() {
        elementName = "*";
    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class. 
     *
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     *  this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomCount",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the AtomCountDescriptor object.
     *
     *@param  params            The new parameters value
     *@throws  CDKException  if the number of parameters is greater than 1
     *or else the parameter is not of type String
     *@see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("AtomCount only expects one parameter");
        }
        if (!(params[0] instanceof String)) {
            throw new CDKException("The parameter must be of type String");
        }
        elementName = (String) params[0];
    }


    /**
     *  Gets the parameters attribute of the AtomCountDescriptor object.
     *
     *@return    The parameters value
     *@see #setParameters
     */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = elementName;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        String name = "n";
        if (elementName.equals("*")) name = "nAtom";
        else name += elementName;
        return new String[]{name};
    }


    /**
     *  This method calculate the number of atoms of a given type in an {@link IAtomContainer}.
     *
     *@param  container  The atom container for which this descriptor is to be calculated
     *@return            Number of atoms of a certain type is returned.
     */

    // it could be interesting to accept as elementName a SMARTS atom, to get the frequency of this atom
    // this could be useful for other descriptors like polar surface area...
    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer container) {
        int atomCount = 0;

        if (container == null) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new IntegerResult((int) Double.NaN), getDescriptorNames(),
                    new CDKException("The supplied AtomContainer was NULL"));
        }

        if (container.getAtomCount() == 0) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new IntegerResult((int) Double.NaN), getDescriptorNames(),
                    new CDKException("The supplied AtomContainer did not have any atoms"));
        }

        if (elementName.equals("*")) {
            for (int i = 0; i < container.getAtomCount(); i++) {
                // we assume that UNSET is equivalent to 0 implicit H's
                Integer hcount = container.getAtom(i).getHydrogenCount();
                if (hcount != CDKConstants.UNSET) atomCount += hcount;
            }
            atomCount += container.getAtomCount();
        } else if (elementName.equals("H")) {
            for (int i = 0; i < container.getAtomCount(); i++) {
                if (container.getAtom(i).getSymbol().equals(elementName)) {
                    atomCount += 1;
                } else {
                    // we assume that UNSET is equivalent to 0 implicit H's
                    Integer hcount = container.getAtom(i).getHydrogenCount();
                    if (hcount != CDKConstants.UNSET) atomCount += hcount;
                }
            }
        }
        else {
            for (int i = 0; i < container.getAtomCount(); i++) {
                if (container.getAtom(i).getSymbol().equals(elementName)) {
                    atomCount += 1;
                }
            }
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(atomCount), getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerResult(1);
    }


    /**
     *  Gets the parameterNames attribute of the AtomCountDescriptor object.
     *
     *@return    The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "elementName";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the AtomCountDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object whose class is that of the parameter requested
     */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return "";
    }
}

