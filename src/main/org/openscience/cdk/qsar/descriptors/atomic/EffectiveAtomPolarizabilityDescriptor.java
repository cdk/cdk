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
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

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
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-05-03
 * @cdk.module  qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:effectivePolarizability
 * @see Polarizability
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.EffectiveAtomPolarizabilityDescriptorTest")
public class EffectiveAtomPolarizabilityDescriptor implements IAtomicDescriptor {

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
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#effectivePolarizability",
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
     *  Gets the parameters attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     * @return    The parameters value
     * @see #setParameters
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return new String[]{"effAtomPol"};
    }


    /**
     *  The method calculates the Effective Atom Polarizability of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   return the efective polarizability
     */
    @TestMethod(value = "testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {
        double polarizability;
        try {
        	// FIXME: for now I'll cache a few modified atomic properties, and restore them at the end of this method
        	String originalAtomtypeName = atom.getAtomTypeName();
        	Integer originalNeighborCount = atom.getFormalNeighbourCount();
        	Integer originalHCount = atom.getHydrogenCount();
        	Integer originalValency = atom.getValency();
        	IAtomType.Hybridization originalHybridization = atom.getHybridization();
        	boolean originalFlag = atom.getFlag(4);
            polarizability = pol.calculateGHEffectiveAtomPolarizability(ac, atom, 100, true);
        	// restore original props
        	atom.setAtomTypeName(originalAtomtypeName);
        	atom.setFormalNeighbourCount(originalNeighborCount);
        	atom.setValency(originalValency);
        	atom.setHydrogenCount(originalHCount);
        	atom.setFlag(4, originalFlag);
        	atom.setHybridization(originalHybridization);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(polarizability),
                    getDescriptorNames());
        } catch (Exception ex1) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN),
                    getDescriptorNames(), ex1);
        }
    }


    /**
     *  Gets the parameterNames attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     *  Gets the parameterType attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}


