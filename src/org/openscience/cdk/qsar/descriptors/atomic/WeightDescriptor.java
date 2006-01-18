/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

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
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:weight
 */
public class WeightDescriptor implements IDescriptor {

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
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#weight",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    };

    /**
     *  Sets the parameters attribute of the WeightDescriptor object.
     *
     *@param  params            The new parameters value
     *@throws CDKException if more than 1 parameter is specified or if the parameter
     *is not of type String
     *@see #getParameters
     */
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
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = elementName;
        return params;
    }


    /**
     * Calculate the weight of specified element type in the supplied {@link IAtomContainer}.
     *
     * @param  container The AtomContainer for which this descriptor is to be calculated. If 'H'
     * is specified as the element symbol make sure that the AtomContainer has hydrogens.
     *@return The total weight of atoms of the specified element type
     */

    public DescriptorValue calculate(IAtomContainer container) {
        double weight = 0;
        org.openscience.cdk.interfaces.IAtom[] atoms = container.getAtoms();
        if (elementName.equals("*")) {
            try {
                for (int i = 0; i < atoms.length; i++) {
                    //System.out.println("WEIGHT: "+container.getAtomAt(i).getSymbol() +" " +IsotopeFactory.getInstance().getMajorIsotope( container.getAtomAt(i).getSymbol() ).getExactMass());
                    weight += IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope( container.getAtomAt(i).getSymbol() ).getExactMass();
                    weight += (container.getAtomAt(i).getHydrogenCount() * 1.00782504);
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        else if (elementName.equals("H")) {
            try {
                for (int i = 0; i < atoms.length; i++) {
                    if (container.getAtomAt(i).getSymbol().equals(elementName)) {
                        weight += IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope( container.getAtomAt(i).getSymbol() ).getExactMass();
                    }
                    else {
                        weight += (container.getAtomAt(i).getHydrogenCount() * 1.00782504);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        else {
            try {
                for (int i = 0; i < atoms.length; i++) {
                    if (container.getAtomAt(i).getSymbol().equals(elementName)) {
                        weight += IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope( container.getAtomAt(i).getSymbol() ).getExactMass();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(weight));
    }


    /**
     *  Gets the parameterNames attribute of the WeightDescriptor object.
     *
     *@return    The parameterNames value
     */
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
    public Object getParameterType(String name) {
        return "";
    }
}

