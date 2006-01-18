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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 *  Effective polarizability of an heavy atom and its protons
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>atomPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
 *   </tr>
 * </table>
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:effectivePolarizability
 */
public class EffectivePolarizabilityDescriptor implements IDescriptor {

    private int atomPosition = 0;
    private Polarizability pol = new Polarizability();


    /**
     *  Constructor for the EffectivePolarizabilityDescriptor object
     */
    public EffectivePolarizabilityDescriptor() { }


    /**
     *  Gets the specification attribute of the EffectivePolarizabilityDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#effectivePolarizability",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the EffectivePolarizabilityDescriptor
     *  object
     *
     *@param  params            The parameter is the atom position
     *@exception  CDKException  Possible Exceptions
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("EffectivePolarizabilityDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        atomPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the EffectivePolarizabilityDescriptor
     *  object
     *
     *@return    an arrayList with the effective polarizability of an heavy atom
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(atomPosition);
        return params;
    }


    /**
     *  The method returns effective polarizabilities assigned to an heavy atom by Polarizability class.
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  ac                AtomContainer
     *@return                   a double with polarizability of the heavy atom
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
        Molecule mol = new Molecule(ac);

        org.openscience.cdk.interfaces.IAtom target = mol.getAtomAt(atomPosition);
        double effectivePolarizability = 0;
        effectivePolarizability = pol.calculateGHEffectiveAtomPolarizability(mol, target, 1000);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(effectivePolarizability));
    }


    /**
     *  Gets the parameterNames attribute of the ProtonTotalPartialChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "atomPosition";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the ProtonTotalPartialChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Integer(0);
    }
}

