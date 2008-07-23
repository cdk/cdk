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
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * This descriptor calculates the number of hydrogen bond acceptors using a slightly simplified version of the
 * <a href="http://www.chemie.uni-erlangen.de/model2001/abstracts/rester.html">PHACIR atom types</a>.
 * The following groups are counted as hydrogen bond acceptors:
 * <ul>
 * <li>any oxygen where the formal charge of the oxygen is non-positive (i.e. formal charge <= 0) <b>except</b></li>
 * <ol>
 * <li>an aromatic ether oxygen (i.e. an ether oxygen that is adjacent to at least one aromatic carbon)</li>
 * <li>an oxygen that is adjacent to a nitrogen</li>
 * </ol>
 * <li>any nitrogen where the formal charge of the nitrogen is non-positive (i.e. formal charge <= 0) <b>except</b></li>
 * <ol>
 * <li>a nitrogen that is adjacent to an oxygen</li>
 * </ol>
 * </ul>
 *
 * Returns a single value named <i>nHBAcc</i>.
 *
 * <p>This descriptor uses these parameters:
 * <table>
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>checkAromaticity</td>
 *     <td>false</td>
 *     <td>true if the aromaticity has to be checked</td>
 *   </tr>
 * </table>
 * <p>
 * This descriptor works properly with AtomContainers whose atoms contain <b>implicit hydrogens</b> or <b>explicit
 * hydrogens</b>.
 * 
 * @author      ulif
 * @cdk.created 2005-22-07
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:hBondacceptors
 */
public class HBondAcceptorCountDescriptor implements IMolecularDescriptor {
    // only parameter of this descriptor; true if aromaticity has to be checked prior to descriptor calculation, false otherwise
    private boolean checkAromaticity = false;
    private static final String[] names = {"nHBAcc"};

    /**
     *  Constructor for the HBondAcceptorCountDescriptor object
     */
    public HBondAcceptorCountDescriptor() { }

    /**
     * Gets the specification attribute of the HBondAcceptorCountDescriptor object.
     *
     * @return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#hBondacceptors",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }

    /**
     * Sets the parameters attribute of the HBondAcceptorCountDescriptor object.
     *
     * @param  params            a boolean true means that aromaticity has to be checked
     * @exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) {
            throw new CDKException("HBondAcceptorCountDescriptor expects a single parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The parameter must be of type Boolean");
        }
        // ok, all should be fine
        checkAromaticity = (Boolean) params[0];
    }

    /**
     * Gets the parameters attribute of the HBondAcceptorCountDescriptor object.
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
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), new IntegerResult((int) Double.NaN), getDescriptorNames(), e);
    }

    /**
     *  Calculates the number of H bond acceptors.
     *
     * @param  atomContainer             AtomContainer
     * @return                   number of H bond acceptors     
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        int hBondAcceptors = 0;

        IAtomContainer ac;
        try {
            ac = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        // aromaticity is detected prior to descriptor calculation if the respective parameter is set to true

        if (checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
                CDKHueckelAromaticityDetector.detectAromaticity(ac);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }

        //org.openscience.cdk.interfaces.IAtom[] atoms = ac.getAtoms();
    // labelled for loop to allow for labelled continue statements within the loop
    atomloop:
    for (int atomIndex = 0; atomIndex < ac.getAtomCount(); atomIndex++) {
        // looking for suitable nitrogen atoms
        if (ac.getAtom(atomIndex).getSymbol().equals("N") && ac.getAtom(atomIndex).getFormalCharge() <= 0) {
            // excluding nitrogens that are adjacent to an oxygen
            java.util.List neighbours = ac.getConnectedAtomsList(ac.getAtom(atomIndex));
            for (Object neighbour : neighbours)
                if (((IAtom) neighbour).getSymbol().equals("O"))
                    continue atomloop;
            hBondAcceptors++;
        }
        // looking for suitable oxygen atoms
        if (ac.getAtom(atomIndex).getSymbol().equals("O") && ac.getAtom(atomIndex).getFormalCharge() <= 0) {
            //excluding oxygens that are adjacent to a nitrogen or to an aromatic carbon
            java.util.List neighbours = ac.getConnectedAtomsList(ac.getAtom(atomIndex));
            for (Object neighbour : neighbours)
                if (((IAtom) neighbour).getSymbol().equals("N") ||
                        (((IAtom) neighbour).getSymbol().equals("C") && ((IAtom) neighbour).getFlag(CDKConstants.ISAROMATIC)))
                    continue atomloop;
            hBondAcceptors++;
        }
    }

    return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
            new IntegerResult(hBondAcceptors),
            getDescriptorNames());
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
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerResult(1);
    }

    /**
     * Gets the parameterNames attribute of the HBondAcceptorCountDescriptor object.
     *
     * @return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkAromaticity";
        return params;
    }

    /**
     * Gets the parameterType attribute of the HBondAcceptorCountDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       The parameterType value
     */
    public Object getParameterType(String name) {
        return false;
    }
}

