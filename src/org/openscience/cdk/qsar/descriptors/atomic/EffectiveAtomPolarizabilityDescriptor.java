/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 * Effective polarizability of an heavy atom
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
 * @author      Miguel Rojas
 * @cdk.created 2006-05-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:sigmaElectronegativity
 * @see Polarizability
 */
public class EffectiveAtomPolarizabilityDescriptor implements IDescriptor {

	private int atomPosition = 0;
	private Polarizability pol;


    /**
     *  Constructor for the EffectiveAtomPolarizabilityDescriptor object
     */
    public EffectiveAtomPolarizabilityDescriptor() {
    	pol = new Polarizability();
  }


    /**
     *  Gets the specification attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#sigmaElectronegativity",
            this.getClass().getName(),
            "$Id: EffectiveAtomPolarizabilityDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@param  params            Atom position
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("EffectiveAtomPolarizabilityDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        atomPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(atomPosition);
        return params;
    }


    /**
     *  The method calculates the Effective Atom Polarizability of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  ac                AtomContainer
     *@return                   return the efective polarizability
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
        double polarizability = 0;
        Molecule mol = new Molecule(ac);
        try {
            polarizability = pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtomAt(atomPosition),100);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(polarizability));
        } catch (Exception ex1) {
        	ex1.printStackTrace();
            throw new CDKException("Problems with Polarizability due to " + ex1.toString(), ex1);
        }
    }


    /**
     *  Gets the parameterNames attribute of the EffectiveAtomPolarizabilityDescriptor
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
     *  Gets the parameterType attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Integer(0);
    }
}

/**
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
 * @author      Miguel Rojas
 * @cdk.created 2006-05-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:sigmaElectronegativity
 */
public class PolarizabilityDescriptor implements IDescriptor {

	private int atomPosition = 0;
	private Polarizability pol;


    /**
     *  Constructor for the PolarizabilityDescriptor object
     */
    public PolarizabilityDescriptor() {
    	pol = new Polarizability();
  }


    /**
     *  Gets the specification attribute of the PolarizabilityDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#sigmaElectronegativity",
            this.getClass().getName(),
            "$Id: PolarizabilityDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PolarizabilityDescriptor
     *  object
     *
     *@param  params            Atom position
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("PolarizabilityDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        atomPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the PolarizabilityDescriptor
     *  object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(atomPosition);
        return params;
    }


    /**
     *  The method calculates the PolarizabilityDescriptor of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  ac                AtomContainer
     *@return                   return the sigma electronegativity
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
        double polarizability = 0;
        Molecule mol = new Molecule(ac);
        try {
            polarizability = pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtomAt(atomPosition),100);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(polarizability));
        } catch (Exception ex1) {
        	ex1.printStackTrace();
            throw new CDKException("Problems with Polarizability due to " + ex1.toString(), ex1);
        }
    }


    /**
     *  Gets the parameterNames attribute of the PolarizabilityDescriptor
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
     *  Gets the parameterType attribute of the PolarizabilityDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Integer(0);
    }
}

