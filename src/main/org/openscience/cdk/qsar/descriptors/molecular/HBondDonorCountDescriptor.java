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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

/**
 * This descriptor calculates the number of hydrogen bond donors using a slightly simplified version of the
 * <a href="http://www.chemie.uni-erlangen.de/model2001/abstracts/rester.html">PHACIR atom types</a>.
 * The following groups are counted as hydrogen bond donors:
 * <ul>
 * <li>Any-OH where the formal charge of the oxygen is non-negative (i.e. formal charge >= 0)</li>
 * <li>Any-NH where the formal charge of the nitrogen is non-negative (i.e. formal charge >= 0)</li>
 * </ul>
 * <p>
 * This descriptor uses no parameters.
 * <p>
 * This descriptor works properly with AtomContainers whose atoms contain either <b>implicit</b> or <b>explicit
 * hydrogen</b> atoms. It does not work with atoms that contain neither implicit nor explicit hydrogens.
 *
 * Returns a single value named <i>nHBAcc</i>.
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
 * @author      ulif
 * @cdk.created 2005-22-07
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:hBondDonors
 */
public class HBondDonorCountDescriptor implements IMolecularDescriptor {
    private static final String[] names = {"nHBDon"};

    /**
     *  Constructor for the HBondDonorCountDescriptor object
     */
    public HBondDonorCountDescriptor() { }


    /**
     * Gets the specification attribute of the HBondDonorCountDescriptor
     * object
     *
     * @return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#hBondDonors",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     * Sets the parameter of this HBondDonorCountDescriptor instance.
     *
     * @param  params            this descriptor does not have any parameters
     * @exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
    // this descriptor has no parameters; nothing has to be done here
    }


    /**
     * Gets the parameters of the HBondDonorCountDescriptor instance.
     *
     * @return    null as this descriptor does not have any parameters
     */
    public Object[] getParameters() {
    // no parameters; thus we return null
        return null;
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
     * Calculates the number of H bond donors.
     *
     * @param  atomContainer               AtomContainer
     * @return                   number of H bond donors
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        int hBondDonors = 0;

        IAtomContainer ac;
        try {
            ac = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        //org.openscience.cdk.interfaces.IAtom[] atoms = ac.getAtoms();
        // iterate over all atoms of this AtomContainer; use label atomloop to allow for labelled continue
        atomloop:
        for (int atomIndex = 0; atomIndex < ac.getAtomCount(); atomIndex++) {
            IAtom atom = (IAtom) ac.getAtom(atomIndex);
            // checking for O and N atoms where the formal charge is >= 0
            if ((atom.getSymbol().equals("O") || atom.getSymbol().equals("N")) && atom.getFormalCharge() >= 0) {
                // implicit hydrogens
                Integer implicitH = atom.getHydrogenCount();
                if (implicitH == CDKConstants.UNSET) implicitH = 0;
                if (implicitH > 0) {
                    hBondDonors++;
                    continue atomloop; // we skip the explicit hydrogens part cause we found implicit hydrogens
                }
                // explicit hydrogens
                java.util.List neighbours = ac.getConnectedAtomsList(atom);
                for (Object neighbour : neighbours) {
                    if (((IAtom) neighbour).getSymbol().equals("H")) {
                        hBondDonors++;
                        continue atomloop;
                    }
                }
            }
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new IntegerResult(hBondDonors),
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
     * Gets the parameterNames of the HBondDonorCountDescriptor.
     *
     * @return    null as this descriptor does not have any parameters
     */
    public String[] getParameterNames() {
    // no parameters; thus we return null
        return null;
    }



    /**
     * Gets the parameterType of the HBondDonorCountDescriptor.
     *
     * @param  name  Description of the Parameter
     * @return       null as this descriptor does not have any parameters
     */
    public Object getParameterType(String name) {
    // no parameters; thus we return null
        return null;
    }
}

