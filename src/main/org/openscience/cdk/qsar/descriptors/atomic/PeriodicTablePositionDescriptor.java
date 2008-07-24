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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

import java.util.Hashtable;
import java.util.Map;

/**
 *  This class returns the period in the periodic table of an atom belonging to an atom container
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
 *@author         mfe4
 *@cdk.created    2004-11-13
 *@cdk.module     qsaratomic
 * @cdk.svnrev  $Revision$
 *@cdk.set        qsar-descriptors
 *@cdk.dictref qsar-descriptors:period
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.PeriodicTablePositionDescriptorTest")
public class PeriodicTablePositionDescriptor implements IAtomicDescriptor {

    private static final String[] names = {"periodicTablePosition"};
    public Map<String, Integer> periodicTable;
	
	/**
	 *  Constructor for the PeriodicTablePositionDescriptor object
	 */
	public PeriodicTablePositionDescriptor() {
		//logger = new LoggingTool(this);
	    if (periodicTable == null) { 
		periodicTable = new Hashtable<String, Integer>();
		periodicTable.put("H", 1);
		periodicTable.put("Li", 2);
		periodicTable.put("Be", 2);
		periodicTable.put("B", 2);
		periodicTable.put("C", 2);
		periodicTable.put("N", 2);
		periodicTable.put("O", 2);
		periodicTable.put("F", 2);
		periodicTable.put("Na", 3);
		periodicTable.put("Mg", 3);
		periodicTable.put("Al", 3);
		periodicTable.put("Si", 3);
		periodicTable.put("P", 3);
		periodicTable.put("S", 3);
		periodicTable.put("Cl", 3);
		periodicTable.put("K", 4);
		periodicTable.put("Ca", 4);
		periodicTable.put("Ga", 4);
		periodicTable.put("Ge", 4);
		periodicTable.put("As", 4);
		periodicTable.put("Se", 4);
		periodicTable.put("Br", 4);
		periodicTable.put("Rb", 5);
		periodicTable.put("Sr", 5);
		periodicTable.put("In", 5);
		periodicTable.put("Sn", 5);
		periodicTable.put("Sb", 5);
		periodicTable.put("Te", 5);
		periodicTable.put("I", 5);
		periodicTable.put("Cs", 6);
		periodicTable.put("Ba", 6);
		periodicTable.put("Tl", 6);
		periodicTable.put("Pb", 6);
		periodicTable.put("Bi", 6);
		periodicTable.put("Po", 6);
		periodicTable.put("At", 6);
		periodicTable.put("Fr", 7);
		periodicTable.put("Ra", 7);
	    }
	}


	/**
	 *  Gets the specification attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@return    The specification value
	 */
	@TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#period",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
     * This descriptor does not have any parameter to be set.
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
    	// no parameters
    }


	/**
	 *  Gets the parameters attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@return    The parameters value
     *@see #setParameters
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    /**
	 *  This method calculates the period of an atom.
	 *
	 * @param  atom              The IAtom for which the DescriptorValue is requested
     * @param  container         Parameter is the atom container.
	 * @return                   The period
	 */

	@TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
		int period;
		String symbol = atom.getSymbol();
		period = periodicTable.get(symbol);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(period),
                names);
	}


	/**
	 *  Gets the parameterNames attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	@TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
	}


	/**
	 *  Gets the parameterType attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	@TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
	}
}

