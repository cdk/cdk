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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 *  This class returns the number of not-Hs substituents of an atom, also defined as "atom degree".
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>targetPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
 *   </tr>
 * </table>
 *
 *@author         mfe4
 *@cdk.created    2004-11-13
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomDegree
 */
public class AtomDegreeDescriptor implements IDescriptor {

    private int targetPosition = 0;

    /**
     *  Constructor for the AtomDegreeDescriptor object.
     */
    public AtomDegreeDescriptor() {}

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
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomDegree",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the AtomDegreeDescriptor object.
     *
     * This descriptor takes one parameter, which should be Integer to indicate
     * which atom the descriptor should be calculated for.
     * 
     * @param  params            The new parameters value
     * @exception  CDKException if more than one parameter or a non-Integer parameter is specified
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("AtomDegreeDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        targetPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the AtomDegreeDescriptor object.
     *
     *@return    The parameters value
     *@see #setParameters
     */
    public Object[] getParameters() {
        Object[] params = new Object[1];
        params[0] = new Integer(targetPosition);
        return params;
    }


    /**
     *  This method calculates the number of not-H substituents of an atom.
     *
     *@param  container     The {@link IAtomContainer} for which this descriptor is to be calculated for
     *@return   The number of bonds on the shortest path between two atoms
     *@throws  CDKException  NOT CLEAR
     */

    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        int atomDegree = 0;
        org.openscience.cdk.interfaces.IAtom target = container.getAtomAt(targetPosition);
        org.openscience.cdk.interfaces.IAtom[] neighboors = container.getConnectedAtoms(target);
        for (int i =0; i< neighboors.length;i++) {
            if(!neighboors[i].getSymbol().equals("H")) atomDegree+=1;
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(atomDegree));
    }


    /**
     *  Gets the parameterNames attribute of the AtomDegreeDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "targetPosition";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the AtomDegreeDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return new Integer(1);
    }
}

