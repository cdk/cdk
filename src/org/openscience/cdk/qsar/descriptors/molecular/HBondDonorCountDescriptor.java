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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

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
 * @author      ulif
 * @cdk.created 2005-22-07
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:hBondDonors
 */
public class HBondDonorCountDescriptor implements IDescriptor {

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


    /**
     * Calculates the number of H bond donors.
     *
     * @param  ac                AtomContainer
     * @return                   number of H bond donors
     * @exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
        int hBondDonors = 0;

        org.openscience.cdk.interfaces.IAtom[] atoms = ac.getAtoms();
    // iterate over all atoms of this AtomContainer; use label atomloop to allow for labelled continue
    atomloop:
    for(int atomIndex = 0; atomIndex < atoms.length; atomIndex++)
    {
      // checking for O and N atoms where the formal charge is >= 0
      if((atoms[atomIndex].getSymbol().equals("O") || atoms[atomIndex].getSymbol().equals("N")) && atoms[atomIndex].getFormalCharge() >= 0)
      {
        // implicit hydrogens
        if(atoms[atomIndex].getHydrogenCount() > 0)
        {
          hBondDonors++;
          continue atomloop; // we skip the explicit hydrogens part cause we found implicit hydrogens
        }
        // explicit hydrogens
        org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atoms[atomIndex]);
        for(int neighbourIndex = 0; neighbourIndex < neighbours.length; neighbourIndex++)
        {
          if(neighbours[neighbourIndex].getSymbol().equals("H"))
          {
            hBondDonors++;
            continue atomloop;
          }
        }
      }
    }

    return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(hBondDonors));
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

