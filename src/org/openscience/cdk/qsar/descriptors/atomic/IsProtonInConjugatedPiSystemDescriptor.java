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

import org.openscience.cdk.Molecule;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;

/**
 *  This class evaluates if a proton is joined to a conjugated system.
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
 *     <td>True is the aromaticity has to be checked</td>
 *   </tr>
 * </table>
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:isProtonInConjugatedPiSystem
 */
public class IsProtonInConjugatedPiSystemDescriptor  implements IAtomicDescriptor {

    private boolean checkAromaticity = false;
    private IAtomContainer acold=null;
    private AtomContainerSet acSet=null;


    /**
     *  Constructor for the IsProtonInConjugatedPiSystemDescriptor object
     */
    public IsProtonInConjugatedPiSystemDescriptor() { }


    /**
     *  Gets the specification attribute of the
     *  IsProtonInConjugatedPiSystemDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#isProtonInConjugatedPiSystem",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the IsProtonInConjugatedPiSystemDescriptor
     *  object
     *
     *@param  params            Parameters are an integer (heavy atom position) and a boolean (true if is needed a checkAromaticity)
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("IsProtonInConjugatedPiSystemDescriptor only expects one parameters");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The parameter must be of type Boolean");
        }
        checkAromaticity = ((Boolean) params[0]).booleanValue();
    }


    /**
     *  Gets the parameters attribute of the IsProtonInConjugatedPiSystemDescriptor
     *  object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Boolean(checkAromaticity);
        return params;
    }


    /**
     *  The method is a proton descriptor that evaluates if a proton is joined to a conjugated system.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   true if the proton is bonded to a conjugated system
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
            boolean isProtonInPiSystem = false;
            Molecule mol = new Molecule(ac);
            if (checkAromaticity) {
            	HueckelAromaticityDetector.detectAromaticity(mol);
            }
            IAtom target = atom;
            if(target.getSymbol().equals("H")) {
            	if(acold!=ac){
            		acold=ac;
            		acSet = ConjugatedPiSystemsDetector.detect(mol);
            	}
                IAtomContainer[] detected = acSet.getAtomContainers();
                IAtom[] neighboors = mol.getConnectedAtoms(target);
                for (int i = 0; i < neighboors.length; i++) {
                    for(int d = 0; d < detected.length; d++) {
                        if ((detected[d]!= null) && (detected[d].contains(neighboors[i]))) {
                            isProtonInPiSystem=true;
                            break;
                        }
                    }
                }
            }
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new BooleanResult(isProtonInPiSystem));
    }


    /**
     *  Gets the parameterNames attribute of the
     *  IsProtonInConjugatedPiSystemDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkAromaticity";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the
     *  IsProtonInConjugatedPiSystemDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Boolean(true);
    }
}

