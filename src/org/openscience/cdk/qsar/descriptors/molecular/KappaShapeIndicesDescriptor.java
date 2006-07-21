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
package org.openscience.cdk.qsar.descriptors.molecular;

import java.util.ArrayList;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

/**
 *  Kier and Hall kappa molecular shape indices compare the molecular graph with minimal and maximal molecular graphs;
 *  a description is given at: http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH :
 *  "they are intended to capture different aspects of molecular shape. 
 *  In the following description, n denotes the number of atoms in the hydrogen suppressed graph, 
 *  m is the number of bonds in the hydrogen suppressed graph. Also, let p2 denote the number of paths of length 2 
 *  and let p3 denote the number of paths of length 3". 
 *  Values kier1, kier2 and kier3 are returned as arrayList of doubles.
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
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:kierValues
 * 
 * @cdk.keyword Kappe shape index
 * @cdk.keyword descriptor
 */
public class KappaShapeIndicesDescriptor implements IMolecularDescriptor {

    private ArrayList singlePaths = null;
    private ArrayList doublePaths = null;
    private ArrayList triplePaths = null;
    /**
     *  Constructor for the KappaShapeIndicesDescriptor object
     */
    public KappaShapeIndicesDescriptor() { }


    /**
     *  Gets the specification attribute of the
     *  KappaShapeIndicesDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#kierValues",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the
     *  KappaShapeIndicesDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the
     *  KappaShapeIndicesDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }


    /**
     *  calculates the kier shape indices for an atom container
     *
     *@param  atomContainer                AtomContainer
     *@return                   kier1, kier2 and kier3 are returned as arrayList of doubles
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) throws CDKException {

        org.openscience.cdk.interfaces.IAtom[] atoms = atomContainer.getAtoms();
        org.openscience.cdk.interfaces.IAtom[] firstAtomNeighboors = null;
        org.openscience.cdk.interfaces.IAtom[] secondAtomNeighboors = null;
        org.openscience.cdk.interfaces.IAtom[] thirdAtomNeighboors = null;
        DoubleArrayResult kierValues = new DoubleArrayResult(3);
        double bond1 = 0;
        double bond2 = 0;
        double bond3 = 0;
        double kier1 = 0;
        double kier2 = 0;
        double kier3 = 0;
        double atomsCount = atoms.length;
        singlePaths = new ArrayList();
        doublePaths = new ArrayList();
        triplePaths = new ArrayList();
        double[] sorterFirst = new double[2];
        double[] sorterSecond = new double[3];
        String tmpbond2 = "";
        String tmpbond3 = "";

        for (int a1 = 0; a1 < atoms.length; a1 ++) {
            bond1 = 0;
            firstAtomNeighboors = atomContainer.getConnectedAtoms(atoms[a1]);
            for (int a2 = 0; a2 < firstAtomNeighboors.length; a2 ++) {
                bond1 = atomContainer.getBondNumber( atoms[a1], firstAtomNeighboors[a2] );
                if(!singlePaths.contains(new Double(bond1))) {
                    singlePaths.add(new Double(bond1));
                    java.util.Collections.sort(singlePaths);
                }
                secondAtomNeighboors = atomContainer.getConnectedAtoms(firstAtomNeighboors[a2]);
                for (int a3 = 0; a3 < secondAtomNeighboors.length; a3 ++) {
                    bond2 = atomContainer.getBondNumber( firstAtomNeighboors[a2], secondAtomNeighboors[a3] );
                    if(!singlePaths.contains(new Double(bond2))) {
                        singlePaths.add(new Double(bond2));
                    }
                    sorterFirst[0] = bond1;
                    sorterFirst[1] = bond2;
                    java.util.Arrays.sort(sorterFirst);

                    tmpbond2 = sorterFirst[0] + "+" + sorterFirst[1];

                    if(!doublePaths.contains(new String(tmpbond2)) && (bond1 != bond2)) {
                        doublePaths.add(new String(tmpbond2));
                    }
                    thirdAtomNeighboors = atomContainer.getConnectedAtoms(secondAtomNeighboors[a3]);
                    for (int a4 = 0; a4 < thirdAtomNeighboors.length; a4 ++) {
                        bond3 = atomContainer.getBondNumber( secondAtomNeighboors[a3], thirdAtomNeighboors[a4] );
                        if(!singlePaths.contains(new Double(bond3))) {
                            singlePaths.add(new Double(bond3));
                        }
                        sorterSecond[0] = bond1;
                        sorterSecond[1] = bond2;
                        sorterSecond[2] = bond3;
                        java.util.Arrays.sort(sorterSecond);

                        tmpbond3 = sorterSecond[0] + "+" + sorterSecond[1] + "+" + sorterSecond[2];
                        if(!triplePaths.contains(new String(tmpbond3))) {
                            if((bond1 != bond2) && (bond1 != bond3) && (bond2 != bond3)) {
                                triplePaths.add(new String(tmpbond3));
                            }
                        }
                    }
                }
            }
        }

        if(atoms.length == 1) {
            kier1 = 0;
            kier2 = 0;
            kier3 = 0;
        }
        else {
            kier1 = ( ( (atomsCount) * ( (atomsCount - 1) * (atomsCount - 1) ) ) / ( singlePaths.size() * singlePaths.size() ) );
            if(atoms.length == 2) {
                kier2 = 0;
                kier3 = 0;
            }
            else {
                kier2 = ( ( (atomsCount - 1) * ( (atomsCount - 2) * (atomsCount - 2) ) ) / ( doublePaths.size() * doublePaths.size() ) );
                if(atoms.length == 3) {
                    kier3 = 0;
                }
                else {
                    if(atomsCount % 2 != 0) {
                        kier3 = ( ( (atomsCount - 1) * ( (atomsCount - 3) * (atomsCount - 3) ) ) / ( triplePaths.size() * triplePaths.size() ) );
                    }
                    else {
                        kier3 = ( ( (atomsCount - 3) * ( (atomsCount - 2) * (atomsCount - 2) ) ) / ( triplePaths.size() * triplePaths.size() ) );
                    }
                }
            }
        }

        kierValues.add(kier1);
        kierValues.add(kier2);
        kierValues.add(kier3);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), kierValues);
    }


    /**
     *  Gets the parameterNames attribute of the
     *  KappaShapeIndicesDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }



    /**
     *  Gets the parameterType attribute of the
     *  KappaShapeIndicesDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }
}

