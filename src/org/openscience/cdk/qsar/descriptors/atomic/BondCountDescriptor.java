/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 *  IDescriptor based on the number of bonds of a certain bond order.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>order</td>
 *     <td>1.0</td>
 *     <td>The bond order</td>
 *   </tr>
 * </table>
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:bondCount
 */
public class BondCountDescriptor implements IDescriptor {

    private double order = 1.0;


    /**
     *  Constructor for the BondCountDescriptor object
     */
    public BondCountDescriptor() { }


    /**
     *  Gets the specification attribute of the BondCountDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondCount",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the BondCountDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("BondCount only expects one parameter");
        }
        if (!(params[0] instanceof Double)) {
            throw new CDKException("The parameter must be of type Double");
        }
        // ok, all should be fine
        order = ((Double) params[0]).doubleValue();
    }


    /**
     *  Gets the parameters attribute of the BondCountDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Double(order);
        return params;
    }


    /**
     *  This method calculate the number of bonds of a given type in an atomContainer
     *
     *@param  container  AtomContainer
     *@return            The number of bonds of a certain type.
     */
    public DescriptorValue calculate(IAtomContainer container) {
        int bondCount = 0;
        org.openscience.cdk.interfaces.IBond[] bonds = container.getBonds();
        for (int i = 0; i < bonds.length; i++) {
            if (container.getBondAt(i).getOrder() == order) {
                bondCount += 1;
            }
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(bondCount));
    }


    /**
     *  Gets the parameterNames attribute of the BondCountDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "order";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the BondCountDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Double(0.0);
    }
}

