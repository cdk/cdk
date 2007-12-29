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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

import java.util.HashMap;
import java.util.Map;

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
 * @cdk.module qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomValence
 */
public class AtomValenceDescriptor implements IAtomicDescriptor {

    public Map<String,Integer> valencesTable;

    /**
     * Constructor for the AtomValenceDescriptor object
     */
    public AtomValenceDescriptor() {
        if (valencesTable == null) {
            valencesTable = new HashMap();
            valencesTable.put("H", 1);
            valencesTable.put("Li", 1);
            valencesTable.put("Be", 2);
            valencesTable.put("B", 3);
            valencesTable.put("C", 4);
            valencesTable.put("N", 5);
            valencesTable.put("O", 6);
            valencesTable.put("F", 7);
            valencesTable.put("Na", 1);
            valencesTable.put("Mg", 2);
            valencesTable.put("Al", 3);
            valencesTable.put("Si", 4);
            valencesTable.put("P", 5);
            valencesTable.put("S", 6);
            valencesTable.put("Cl", 7);
            valencesTable.put("K", 1);
            valencesTable.put("Ca", 2);
            valencesTable.put("Ga", 3);
            valencesTable.put("Ge", 4);
            valencesTable.put("As", 5);
            valencesTable.put("Se", 6);
            valencesTable.put("Br", 7);
            valencesTable.put("Rb", 1);
            valencesTable.put("Sr", 2);
            valencesTable.put("In", 3);
            valencesTable.put("Sn", 4);
            valencesTable.put("Sb", 5);
            valencesTable.put("Te", 6);
            valencesTable.put("I", 7);
            valencesTable.put("Cs", 1);
            valencesTable.put("Ba", 2);
            valencesTable.put("Tl", 3);
            valencesTable.put("Pb", 4);
            valencesTable.put("Bi", 5);
            valencesTable.put("Po", 6);
            valencesTable.put("At", 7);
            valencesTable.put("Fr", 1);
            valencesTable.put("Ra", 2);
            valencesTable.put("Cu", 2);
            valencesTable.put("Mn", 2);
            valencesTable.put("Co", 2);
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
        return null;
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
        int atomValence;
        String symbol = atom.getSymbol();
        atomValence = valencesTable.get(symbol);
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

