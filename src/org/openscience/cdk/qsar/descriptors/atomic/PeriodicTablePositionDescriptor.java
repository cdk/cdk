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

import java.util.Hashtable;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

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
 *     <td>targetPosition</td>
 *     <td>1</td>
 *     <td>The position of the target atom</td>
 *   </tr>
 * </table>
 *
 *@author         mfe4
 *@cdk.created    2004-11-13
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 *@cdk.dictref qsar-descriptors:period
 */
public class PeriodicTablePositionDescriptor implements IAtomicDescriptor {

	public Hashtable periodicTable;
	
	/**
	 *  Constructor for the PeriodicTablePositionDescriptor object
	 */
	public PeriodicTablePositionDescriptor() {
		//logger = new LoggingTool(this);
	    if (periodicTable == null) { 
		periodicTable = new Hashtable();
		periodicTable.put("H", new Integer(1));
		periodicTable.put("Li", new Integer(2));
		periodicTable.put("Be", new Integer(2));
		periodicTable.put("B", new Integer(2));
		periodicTable.put("C", new Integer(2));
		periodicTable.put("N", new Integer(2));
		periodicTable.put("O", new Integer(2));
		periodicTable.put("F", new Integer(2));
		periodicTable.put("Na", new Integer(3));
		periodicTable.put("Mg", new Integer(3));
		periodicTable.put("Al", new Integer(3));
		periodicTable.put("Si", new Integer(3));
		periodicTable.put("P", new Integer(3));
		periodicTable.put("S", new Integer(3));
		periodicTable.put("Cl", new Integer(3));
		periodicTable.put("K", new Integer(4));
		periodicTable.put("Ca", new Integer(4));
		periodicTable.put("Ga", new Integer(4));
		periodicTable.put("Ge", new Integer(4));
		periodicTable.put("As", new Integer(4));
		periodicTable.put("Se", new Integer(4));
		periodicTable.put("Br", new Integer(4));
		periodicTable.put("Rb", new Integer(5));
		periodicTable.put("Sr", new Integer(5));
		periodicTable.put("In", new Integer(5));
		periodicTable.put("Sn", new Integer(5));
		periodicTable.put("Sb", new Integer(5));
		periodicTable.put("Te", new Integer(5));
		periodicTable.put("I", new Integer(5));
		periodicTable.put("Cs", new Integer(6));
		periodicTable.put("Ba", new Integer(6));
		periodicTable.put("Tl", new Integer(6));
		periodicTable.put("Pb", new Integer(6));
		periodicTable.put("Bi", new Integer(6));
		periodicTable.put("Po", new Integer(6));
		periodicTable.put("At", new Integer(6));
		periodicTable.put("Fr", new Integer(7));
		periodicTable.put("Ra", new Integer(7));	    
	    }
	}


	/**
	 *  Gets the specification attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@return    The specification value
	 */
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
    public void setParameters(Object[] params) throws CDKException {
    	// no parameters
    }


	/**
	 *  Gets the parameters attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@return    The parameters value
     *@see #setParameters
     */
    public Object[] getParameters() {
        return new Object[0];
    }


	/**
	 *  This method calculates the period of an atom.
	 *
	 * @param  atom              The IAtom for which the DescriptorValue is requested
     * @param  container         Parameter is the atom container.
	 * @return                   The period
	 *@exception  CDKException   Description of the Exception
	 */

	public DescriptorValue calculate(IAtom atom, IAtomContainer container) throws CDKException {
		int period = 0;
		String symbol = atom.getSymbol();
		period = ((Integer)periodicTable.get(symbol)).intValue();
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(period));
	}


	/**
	 *  Gets the parameterNames attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
        return new String[0];
	}


	/**
	 *  Gets the parameterType attribute of the PeriodicTablePositionDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
        return null;
	}
}

