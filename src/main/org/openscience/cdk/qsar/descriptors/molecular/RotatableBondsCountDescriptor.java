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
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 *  The number of rotatable bonds is given by the SMARTS specified by Daylight on
 *  <a href="http://www.daylight.com/dayhtml_tutorials/languages/smarts/smarts_examples.html#EXMPL">SMARTS tutorial</a><p>
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>includeTerminals</td>
 *     <td>false</td>
 *     <td>True if terminal bonds are included</td>
 *   </tr>
 * </table>
 *
 * Returns a single value named <i>nRotB</i>
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:rotatableBondsCount
 * 
 * @cdk.keyword bond count, rotatable
 * @cdk.keyword descriptor
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptorTest")
public class RotatableBondsCountDescriptor implements IMolecularDescriptor {
	private boolean includeTerminals = false;


	/**
	 *  Constructor for the RotatableBondsCountDescriptor object
	 */
	public RotatableBondsCountDescriptor() { }


	/**
	 *  Gets the specification attribute of the RotatableBondsCountDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#rotatableBondsCount",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the RotatableBondsCountDescriptor object
	 *
	 *@param  params            a boolean true means that terminal atoms must be included in the count
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length != 1) {
			throw new CDKException("RotatableBondsCount expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter must be of type Boolean");
		}
		// ok, all should be fine
		includeTerminals = (Boolean) params[0];
	}


	/**
	 *  Gets the parameters attribute of the RotatableBondsCountDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = includeTerminals;
		return params;
	}

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return new String[] { includeTerminals ? "nRotBt" : "nRotB"};
    }


    /**
	 *  The method calculates the number of rotatable bonds of an atom container.
	 *  If the boolean parameter is set to true, terminal bonds are included.
	 *
	 *@param  ac                AtomContainer
	 *@return                   number of rotatable bonds
	 */
	public DescriptorValue calculate(IAtomContainer ac) {
		int rotatableBondsCount = 0;
		int degree0;
		int degree1;
        IRingSet ringSet;
        try {
            ringSet = new SpanningTree(ac).getBasicRings();
        } catch (NoSuchAtomException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult((int) Double.NaN), getDescriptorNames(), e);
        }
        for (IBond bond : ac.bonds()) {
			if (ringSet.getRings(bond).getAtomContainerCount() > 0) {
				bond.setFlag(CDKConstants.ISINRING, true);
			}
		}
		for (IBond bond : ac.bonds()) {
			IAtom atom0 = bond.getAtom(0);
			IAtom atom1 = bond.getAtom(1);
			if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE) {
				if ((BondManipulator.isLowerOrder(ac.getMaximumBondOrder(atom0), IBond.Order.TRIPLE)) && 
					(BondManipulator.isLowerOrder(ac.getMaximumBondOrder(atom1), IBond.Order.TRIPLE))) {
					if (!bond.getFlag(CDKConstants.ISINRING)) {
						degree0 = ac.getConnectedBondsCount(atom0);
						degree1 = ac.getConnectedBondsCount(atom1);
						if ((degree0 == 1) || (degree1 == 1)) {
							if (includeTerminals) {
								rotatableBondsCount += 1;
							}
						} else {
							rotatableBondsCount += 1;
						}
					}
				}
			}
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(rotatableBondsCount), getDescriptorNames());

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
     *  Gets the parameterNames attribute of the RotatableBondsCountDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "includeTerminals";
        return params;
    }



	/**
	 *  Gets the parameterType attribute of the RotatableBondsCountDescriptor
	 *  object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return true;
	}
}

