/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atompair;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomPairDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * This class checks if two atoms have pi-contact (this is true when there is
 * one and the same conjugated pi-system which contains both atoms, or directly
 * linked neighboors of the atoms).
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>firstAtom</td>
 *     <td>0</td>
 *     <td>The position of the first atom</td>
 *   </tr>
 *   <tr>
 *     <td>secondAtom</td>
 *     <td>0</td>
 *     <td>The position of the second atom</td>
 *   </tr>
 *   <tr>
 *     <td>checkAromaticity</td>
 *     <td>false</td>
 *     <td>True is the aromaticity has to be checked</td>
 *   </tr>
 * </table>
 *
 * @author         mfe4
 * @cdk.created    2004-11-03
 * @cdk.module     qsar
 * @cdk.svnrev  $Revision$
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:piContact
 */
public class PiContactDetectionDescriptor implements IAtomPairDescriptor {

    private static final String[] names = {"piContact"};

    private boolean checkAromaticity = false;
    IAtomContainerSet acSet = null;
    private IAtomContainer acold=null;

    /**
     * Constructor for the PiContactDetectionDescriptor object.
     */
    public PiContactDetectionDescriptor() { }

    /**
     * Gets the specification attribute of the PiContactDetectionDescriptor object.
     *
     * @return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#piContact",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the PiContactDetectionDescriptor object.
     *
     * @param  params Parameters contains a
     *                boolean (true if is needed a checkAromaticity)
     * @exception     CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) {
            throw new CDKException("PiContactDetectionDescriptor expects 1 parameters");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The first parameter must be of type Boolean");
        }
        checkAromaticity = (Boolean) params[0];
    }


    /**
     * Gets the parameters attribute of the PiContactDetectionDescriptor object.
     *
     * @return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = checkAromaticity;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }

     private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(
                getSpecification(), getParameterNames(),
                getParameters(), new BooleanResult(false),
                names, e);
    }

    /**
     * The method returns if two atoms have pi-contact.
     *
     * @param  atomContainer                AtomContainer
     * @return                   true if the atoms have pi-contact
     */
    public DescriptorValue calculate(IAtom first, IAtom second, IAtomContainer atomContainer)  {
        IAtomContainer ac;
        try {
            ac = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }
        IAtom clonedFirst = ac.getAtom(atomContainer.getAtomNumber(first));
        IAtom clonedSecond = ac.getAtom(atomContainer.getAtomNumber(first));

        Molecule mol = new Molecule(ac);
        if (checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
                CDKHueckelAromaticityDetector.detectAromaticity(mol);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }
        boolean piContact = false;
        int counter = 0;

        if(acold!=ac){
          acold=ac;
          acSet = ConjugatedPiSystemsDetector.detect(mol);
        }
        java.util.Iterator<IAtomContainer> detected = acSet.atomContainers().iterator();

        java.util.List<IAtom> neighboorsFirst = mol.getConnectedAtomsList(clonedFirst);
        java.util.List<IAtom> neighboorsSecond = mol.getConnectedAtomsList(clonedSecond);

        while (detected.hasNext()) {
        	IAtomContainer detectedAC = detected.next();
            if (detectedAC.contains(clonedFirst) && detectedAC.contains(clonedSecond)) {
                counter += 1;
                break;
            }
            if (isANeighboorsInAnAtomContainer(neighboorsFirst, detectedAC) && isANeighboorsInAnAtomContainer(neighboorsSecond, detectedAC)) {
                counter += 1;
                break;
            }
        }

        if (counter > 0) {
            piContact = true;
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new BooleanResult(piContact), getDescriptorNames());
    }


    /**
     * Gets if neighboors of an atom are in an atom container.
     *
     * @param  neighs  array of atoms
     * @param  ac      AtomContainer
     * @return         The boolean result
     */
    private boolean isANeighboorsInAnAtomContainer(java.util.List neighs, IAtomContainer ac) {
        boolean isIn = false;
        int count = 0;
        for (Object neigh : neighs) {
            if (ac.contains((IAtom) neigh)) {
                count += 1;
            }
        }
        if (count > 0) {
            isIn = true;
        }
        return isIn;
    }


    /**
     * Gets the parameterNames attribute of the PiContactDetectionDescriptor
     * object.
     *
     * @return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkAromaticity";
        return params;
    }


    /**
     * Gets the parameterType attribute of the PiContactDetectionDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       The parameterType value
     */
    public Object getParameterType(String name) {
        if (name.equals("checkAromaticity")) return true;
        return null;
    }
}

