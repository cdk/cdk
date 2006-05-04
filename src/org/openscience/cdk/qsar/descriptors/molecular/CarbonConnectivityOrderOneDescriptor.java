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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  CarbonConnectivity index (order 1):
 *  http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH
 *  returned value is:
 *  chi1C is the Carbon connectivity index (order 1);
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:chi1C
 */
public class CarbonConnectivityOrderOneDescriptor implements IMolecularDescriptor {

    /**
     *  Constructor for the CarbonConnectivityOrderOneDescriptor object
     */
    public CarbonConnectivityOrderOneDescriptor() { }


    /**
     *  Gets the specification attribute of the CarbonConnectivityOrderOneDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chi1C",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the CarbonConnectivityOrderOneDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the CarbonConnectivityOrderOneDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        return (null);
        // no parameters to return
    }


    /**
     *  Description of the Method
     *
     *@param  atomContainer                AtomContainer
     *@return                   chi1C
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) throws CDKException {
        double chi1C = 0;
        ArrayList degrees = new ArrayList(2);
        double val0 = 0;
        double val1 = 0;
        int atomDegree = 0;
        org.openscience.cdk.interfaces.IBond[] bonds = atomContainer.getBonds();
        IAtom[] atoms = null;
        for (int b = 0; b < bonds.length; b++) {
            atoms = bonds[b].getAtoms();
            if ((!atoms[0].getSymbol().equals("H")) || (!atoms[1].getSymbol().equals("H"))) {
                degrees.clear();
                for (int a = 0; a < atoms.length; a++) {
                    atomDegree = 0;
                    IAtom[] neighboors = atomContainer.getConnectedAtoms(atoms[a]);
                    for (int n = 0; n < neighboors.length; n++) {
                        if (!neighboors[n].getSymbol().equals("H")) {
                            atomDegree += 1;
                        }
                    }
                    if(atomDegree > 0) {
                        degrees.add(new Double(atomDegree));
                    }
                }
                val0 = ( (Double)degrees.get(0) ).doubleValue();
                val1 = ( (Double)degrees.get(1) ).doubleValue();
                if((atoms[0].getSymbol().equals("C")) && (atoms[1].getSymbol().equals("C"))) {
                    chi1C += 1/(Math.sqrt(val0 * val1));
                }
            }
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(chi1C));
    }


    /**
     *  Gets the parameterNames attribute of the CarbonConnectivityOrderOneDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }



    /**
     *  Gets the parameterType attribute of the CarbonConnectivityOrderOneDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }
}

