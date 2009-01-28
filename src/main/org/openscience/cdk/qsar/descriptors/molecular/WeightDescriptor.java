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
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 *  IDescriptor based on the weight of atoms of a certain element type. 
 *
 *  If the wild-card symbol *
 *  is specified, the returned value is the molecular weight.
 *  If an invalid element symbol is specified, the return value is 
 *  0 and no exception is thrown
 *  <p>
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>elementSymbol</td>
 *     <td>*</td>
 *     <td>If *, returns the molecular weight, otherwise the weight for the given element</td>
 *   </tr>
 * </table>
 *
 * Returns a single value named <i>wX</i> where <i>X</i> is the chemical symbol
 * or <i>MW</i> if * is specified as a parameter.
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:weight
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptorTest")
public class WeightDescriptor implements IMolecularDescriptor {

    private String elementName = "*";

    /**
     *  Constructor for the WeightDescriptor object.
     */
    public WeightDescriptor() { }

    /**
     * Returns a <code>Map</code> which specifies which descriptor is implemented by this class. 
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
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#weight",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the WeightDescriptor object.
     *
     *@param  params            The new parameters value
     *@throws CDKException if more than 1 parameter is specified or if the parameter
     *is not of type String
     *@see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("weight only expects one parameter");
        }
        if (!(params[0] instanceof String)) {
            throw new CDKException("The parameter must be of type String");
        }
        // ok, all should be fine
        elementName = (String) params[0];
    }


    /**
     *  Gets the parameters attribute of the WeightDescriptor object.
     *
     * @return    The parameters value
     * @see #setParameters
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
        String name = "w";
        if (elementName.equals("*")) name = "MW";
        else name += elementName;
        return new String[]{name};
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
         return new DescriptorValue(getSpecification(), getParameterNames(),
                 getParameters(), new DoubleResult(Double.NaN), getDescriptorNames(), e);
     }


    /**
     * Calculate the weight of specified element type in the supplied {@link IAtomContainer}.
     *
     * @param  container The AtomContainer for which this descriptor is to be calculated. If 'H'
     * is specified as the element symbol make sure that the AtomContainer has hydrogens.
     *@return The total weight of atoms of the specified element type
     */
    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer container) {
        double weight = 0;
        if (elementName.equals("*")) {
            try {
                for (int i = 0; i < container.getAtomCount(); i++) {
                    //logger.debug("WEIGHT: "+container.getAtomAt(i).getSymbol() +" " +IsotopeFactory.getInstance().getMajorIsotope( container.getAtomAt(i).getSymbol() ).getExactMass());
                    weight += IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope( container.getAtom(i).getSymbol() ).getExactMass();
                    Integer hcount = container.getAtom(i).getHydrogenCount();
                    if (hcount == CDKConstants.UNSET) hcount = 0;
                    weight += (hcount * 1.00782504);
                }
            } catch (Exception e) {
                return getDummyDescriptorValue(e);
            }
        }
        else if (elementName.equals("H")) {
            try {
                IIsotope h=IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope("H");
                for (int i = 0; i < container.getAtomCount(); i++) {
                    if (container.getAtom(i).getSymbol().equals(elementName)) {
                        weight += IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope( container.getAtom(i).getSymbol() ).getExactMass();
                    }
                    else {
                        weight += (container.getAtom(i).getHydrogenCount() * h.getExactMass());
                    }
                }
            } catch (Exception e) {
                return getDummyDescriptorValue(e);
            }
        }
        else {
            try {
                for (int i = 0; i < container.getAtomCount(); i++) {
                    if (container.getAtom(i).getSymbol().equals(elementName)) {
                        weight += IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope( container.getAtom(i).getSymbol() ).getExactMass();
                    }
                }
            } catch (Exception e) {
                return getDummyDescriptorValue(e);
            }
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(weight), getDescriptorNames());

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
        return new DoubleResult(0.0);
    }


    /**
     *  Gets the parameterNames attribute of the WeightDescriptor object.
     *
     *@return    The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "elementSymbol";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the WeightDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object whose class is that of the parameter requested
     */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return "";
    }
}

