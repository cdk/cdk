/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  Matteo Floris <mfe4@users.sf.net>
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

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

/**
 * This class returns the valence of an atom.
 * <p/>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td></td>
 * <td>no parameters</td>
 * </tr>
 * </table>
 *
 * @author mfe4
 * @cdk.created 2004-11-13
 * @cdk.module qsar
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomValence
 */
public class AtomValenceDescriptor implements IAtomicDescriptor {

    public Map valencesTable;

    /**
     * Constructor for the AtomValenceDescriptor object
     */
    public AtomValenceDescriptor() {
        if (valencesTable == null) {
            valencesTable = new HashMap();
            valencesTable.put("H", new Integer(1));
            valencesTable.put("Li", new Integer(1));
            valencesTable.put("Be", new Integer(2));
            valencesTable.put("B", new Integer(3));
            valencesTable.put("C", new Integer(4));
            valencesTable.put("N", new Integer(5));
            valencesTable.put("O", new Integer(6));
            valencesTable.put("F", new Integer(7));
            valencesTable.put("Na", new Integer(1));
            valencesTable.put("Mg", new Integer(2));
            valencesTable.put("Al", new Integer(3));
            valencesTable.put("Si", new Integer(4));
            valencesTable.put("P", new Integer(5));
            valencesTable.put("S", new Integer(6));
            valencesTable.put("Cl", new Integer(7));
            valencesTable.put("K", new Integer(1));
            valencesTable.put("Ca", new Integer(2));
            valencesTable.put("Ga", new Integer(3));
            valencesTable.put("Ge", new Integer(4));
            valencesTable.put("As", new Integer(5));
            valencesTable.put("Se", new Integer(6));
            valencesTable.put("Br", new Integer(7));
            valencesTable.put("Rb", new Integer(1));
            valencesTable.put("Sr", new Integer(2));
            valencesTable.put("In", new Integer(3));
            valencesTable.put("Sn", new Integer(4));
            valencesTable.put("Sb", new Integer(5));
            valencesTable.put("Te", new Integer(6));
            valencesTable.put("I", new Integer(7));
            valencesTable.put("Cs", new Integer(1));
            valencesTable.put("Ba", new Integer(2));
            valencesTable.put("Tl", new Integer(3));
            valencesTable.put("Pb", new Integer(4));
            valencesTable.put("Bi", new Integer(5));
            valencesTable.put("Po", new Integer(6));
            valencesTable.put("At", new Integer(7));
            valencesTable.put("Fr", new Integer(1));
            valencesTable.put("Ra", new Integer(2));
            valencesTable.put("Cu", new Integer(2));
            valencesTable.put("Mn", new Integer(2));
            valencesTable.put("Co", new Integer(2));
        }
    }


    /**
     * Gets the specification attribute of the AtomValenceDescriptor object
     *
     * @return The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomValence",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return new Object[0];
    }

    /**
     * This method calculates the valence of an atom.
     *
     * @param atom          The IAtom for which the DescriptorValue is requested
     * @param container      Parameter is the atom container.
     * @return The valence of an atom
     * @throws CDKException Description of the Exception
     */

    public DescriptorValue calculate(IAtom atom, IAtomContainer container) throws CDKException {
        int atomValence = 0;
        String symbol = atom.getSymbol();
        atomValence = ((Integer) valencesTable.get(symbol)).intValue();
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(atomValence));
    }


    /**
     *  Gets the parameterNames attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the VdWRadiusDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}

