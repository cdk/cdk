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
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

/**
 *  This class returns the hybridization of an atom.
 *
 *  <p>This class try to find a SIMPLE WAY the molecular geometry for following from
 *    Valence Shell Electron Pair Repulsion or VSEPR model and at the same time its
 *    hybridization of atoms in a molecule.
 *
 *  <p>The basic premise of the model is that the electrons are paired in a molecule 
 *    and that the molecule geometry is determined only by the repulsion between the pairs. 
 *    The geometry adopted by a molecule is then the one in which the repulsions are minimized.
 *
 *  <p>It counts the number of electron pairs in the Lewis dot diagram which
 *   are attached to an atom. Then uses the following table.
 * <pre>
 * <table border="1">
 *   <tr>  
 * 	  <td>pairs on an atom</td>
 *    <td>hybridization of the atom</td>
 *    <td>geometry</td>
 *    <td>number for CDK.Constants</td> 
 *   </tr>   
 *   <tr><td>2</td><td>sp</td><td>linear</td><td>1</td></tr>
 *   <tr><td>3</td><td>sp^2</td><td>trigonal planar</td><td>2</td></tr>
 *   <tr><td>4</td><td>sp^3</td><td>tetrahedral</td><td>3</td></tr>
 *   <tr><td>5</td><td>sp^3d</td><td>trigonal bipyramid</td><td>4</td></tr>
 *   <tr><td>6</td><td>sp^3d^2</td><td>octahedral</td><td>5</td></tr>
 *   <tr><td>7</td><td>sp^3d^3</td><td>pentagonal bipyramid</td><td>6</td></tr>
 *   <tr><td>8</td><td>sp^3d^4</td><td>square antiprim</td><td>7</td></tr>
 *   <tr><td>9</td><td>sp^3d^5</td><td>tricapped trigonal prism</td><td>8</td></tr>
 * </table>
 * </pre>
 *
 *  <p>This table only works if the central atom is a p-block element 
 *   (groups IIA through VIIIA), not a transition metal.
 *
 *
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
 *@author         Miguel Rojas
 *@cdk.created    2005-03-24
 *@cdk.module     qsaratomic
 * @cdk.svnrev  $Revision$
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomHybridizationVSEPR
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationVSEPRDescriptorTest")
public class AtomHybridizationVSEPRDescriptor implements IAtomicDescriptor {

	/**
	 *  Constructor for the AtomHybridizationVSEPRDescriptor object
	 */
	public AtomHybridizationVSEPRDescriptor() {}

	/**
	 *  Gets the specification attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@return    The specification value
	 */
	@TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomHybridizationVSEPR",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
     * This descriptor does have any parameter.
     */
	@TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
	}


	/**
	 *  Gets the parameters attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 * @return    The parameters value
     * @see       #setParameters
	 */
	@TestMethod(value="testGetParameters")
    public Object[] getParameters() {
		return null;
	}

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return new String[]{"hybr"};
    }


    /**
	 *  This method calculates the hybridization of an atom.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  container         Parameter is the atom container.
	 *@return                   The hybridization
	 */

	@TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
        IAtomType atomType;
        try {
            atomType = CDKAtomTypeMatcher.getInstance(atom.getBuilder()).findMatchingAtomType(container, atom);
        } catch (CDKException e) {
            return new DescriptorValue(
                    getSpecification(), getParameterNames(), getParameters(),
                    new IntegerResult((int) Double.NaN), // does that work??
                    getDescriptorNames(), new CDKException("Atom type was null"));
        }
        if (atomType == null) {
            return new DescriptorValue(
                    getSpecification(), getParameterNames(), getParameters(),
                    new IntegerResult((int) Double.NaN), // does that work??
                    getDescriptorNames(), new CDKException("Atom type was null"));

        }

        if (atomType.getHybridization() == null) {
            return new DescriptorValue(
                    getSpecification(), getParameterNames(), getParameters(),
                    new IntegerResult((int) Double.NaN), // does that work??
                    getDescriptorNames(), new CDKException("Hybridization was null"));
        }
        int hybridizationCDK = atomType.getHybridization().ordinal();

        return new DescriptorValue(
                getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(hybridizationCDK),
                getDescriptorNames());
    }

    /**
     *  Gets the parameterNames attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	@TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
	}


	/**
	 *  Gets the parameterType attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
	 */
	@TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
		return null;
	}
}

