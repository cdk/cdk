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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  Carbon Connectivity index (order 0):
 *  http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH
 *  returned value is:
 *  chi0C is the Carbon connectivity index (order 0);
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
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:chi0C
 */
public class CarbonConnectivityOrderZeroDescriptor implements IMolecularDescriptor {

    /**
     *  Constructor for the CarbonConnectivityOrderZeroDescriptor object
     */
    public CarbonConnectivityOrderZeroDescriptor() { }


    /**
     *  Gets the specification attribute of the CarbonConnectivityOrderZeroDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chi0C",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the CarbonConnectivityOrderZeroDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the CarbonConnectivityOrderZeroDescriptor object
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
     *@param  atomContainer     AtomContainer
     *@return                   Carbon connectivity index (order 0)
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) throws CDKException {
        double chi0C = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            int atomDegree = 0;
            java.util.List neighboors = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
            for (int a = 0; a < neighboors.size(); a++) {
                if (!((IAtom)neighboors.get(a)).getSymbol().equals("H")) {
                    atomDegree += 1;
                }
            }
            if(atomDegree > 0) {
                if(atomContainer.getAtom(i).getSymbol().equals("C")) {
                    chi0C += 1/(Math.sqrt(atomDegree));
                }
            }
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(chi0C), new String[] {"chi0C"});
    }


    /**
     *  Gets the parameterNames attribute of the CarbonConnectivityOrderZeroDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }



    /**
     *  Gets the parameterType attribute of the CarbonConnectivityOrderZeroDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }
}

