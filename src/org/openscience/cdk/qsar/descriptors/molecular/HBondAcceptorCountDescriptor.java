/*
 *  $RCSfile$
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

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
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:hBondacceptors
 */
public class HBondAcceptorCountDescriptor implements IDescriptor {
    // only parameter of this descriptor; true if aromaticity has to be checked prior to descriptor calculation, false otherwise
    private boolean checkAromaticity = false;

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
        checkAromaticity = ((Boolean) params[0]).booleanValue();
    }

    /**
     * Gets the parameters attribute of the HBondAcceptorCountDescriptor object.
     *
     * @return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Boolean(checkAromaticity);
        return params;
    }

    /**
     *  Calculates the number of H bond acceptors.
     *
     * @param  ac                AtomContainer
     * @return                   number of H bond acceptors
     * @exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(AtomContainer ac) throws CDKException {
        int hBondAcceptors = 0;

    // aromaticity is detected prior to descriptor calculation if the respective parameter is set to true
        if (checkAromaticity)
            HueckelAromaticityDetector.detectAromaticity(ac);

        org.openscience.cdk.interfaces.Atom[] atoms = ac.getAtoms();
    // labelled for loop to allow for labelled continue statements within the loop
    atomloop:
        for (int atomIndex = 0; atomIndex < atoms.length; atomIndex++)
    {
      // looking for suitable nitrogen atoms
      if(atoms[atomIndex].getSymbol().equals("N") && atoms[atomIndex].getFormalCharge() <= 0)
      {
        // excluding nitrogens that are adjacent to an oxygen
          org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atoms[atomIndex]);
        for(int neighbourIndex = 0; neighbourIndex < neighbours.length; neighbourIndex++)
          if(neighbours[neighbourIndex].getSymbol().equals("O"))
            continue atomloop;
        hBondAcceptors++;
      }
      // looking for suitable oxygen atoms
      if(atoms[atomIndex].getSymbol().equals("O") && atoms[atomIndex].getFormalCharge() <= 0)
      {
        //excluding oxygens that are adjacent to a nitrogen or to an aromatic carbon
          org.openscience.cdk.interfaces.Atom[] neighbours = ac.getConnectedAtoms(atoms[atomIndex]);
        for(int neighbourIndex = 0; neighbourIndex < neighbours.length; neighbourIndex++)
          if(neighbours[neighbourIndex].getSymbol().equals("N") ||
              (neighbours[neighbourIndex].getSymbol().equals("C") && neighbours[neighbourIndex].getFlag(CDKConstants.ISAROMATIC)))
            continue atomloop;
        hBondAcceptors++;
      }
        }

    return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(hBondAcceptors));
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
        return new Boolean(false);
    }
}

