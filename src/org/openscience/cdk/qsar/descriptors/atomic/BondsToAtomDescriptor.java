/*  $RCSfile$
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

import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 *  This class returns the number of bonds on the shortest path between two atoms.
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
 *     <td>The position of the first atom</td>
 *   </tr>
 *   <tr>
 *     <td>focusPosition</td>
 *     <td>0</td>
 *     <td>The position of the second atom</td>
 *   </tr>
 * </table>
 *
 * @author         mfe4
 * @cdk.created    2004-11-13
 * @cdk.module     qsar
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:bondsToAtom
 */
public class BondsToAtomDescriptor implements IDescriptor {

    private int targetPosition = 0;
    private int focusPosition = 0;
    private org._3pq.jgrapht.Graph mygraph = null;
    java.util.List mylist = null;
    Object startVertex = null;
    Object endVertex = null;

    /**
     *  Constructor for the BondsToAtomDescriptor object
     */
    public BondsToAtomDescriptor() {}


    /**
     *  Gets the specification attribute of the BondsToAtomDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondsToAtom",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the BondsToAtomDescriptor object
     *
     *@param  params            The parameter is the atom position
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 2) {
            throw new CDKException("BondsToAtomDescriptor only expects two parameters");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        if (!(params[1] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        targetPosition = ((Integer) params[0]).intValue();
        focusPosition = ((Integer) params[1]).intValue();
    }


    /**
     *  Gets the parameters attribute of the BondsToAtomDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        Object[] params = new Object[2];
        params[0] = new Integer(targetPosition);
        params[1] = new Integer(focusPosition);
        return params;
    }


    /**
     *  This method calculate the number of bonds on the shortest path between two atoms.
     *
     *@param  container         Parameter is the atom container.
     *@return                   The number of bonds on the shortest path between two atoms
     *@exception  CDKException  Description of the Exception
     */

    public DescriptorValue calculate(AtomContainer container) throws CDKException {
        mygraph = MoleculeGraphs.getMoleculeGraph((Molecule)container);
        int bondsToAtom = 0;
        org.openscience.cdk.interfaces.Atom target = container.getAtomAt(targetPosition);
        org.openscience.cdk.interfaces.Atom focus = container.getAtomAt(focusPosition);
        startVertex = target;
        endVertex = focus;
        org._3pq.jgrapht.Edge edg = null;
        mylist = org.openscience.cdk.graph.BFSShortestPath.findPathBetween(mygraph,startVertex,endVertex);
        bondsToAtom = mylist.size();
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(bondsToAtom));

    }


    /**
     *  Gets the parameterNames attribute of the BondsToAtomDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[2];
        params[0] = "targetPosition";
        params[1] = "focusPosition";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the BondsToAtomDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Integer(0);
    }
}

