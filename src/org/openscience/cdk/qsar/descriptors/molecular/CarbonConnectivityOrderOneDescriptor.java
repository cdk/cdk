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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *  CarbonConnectivity index (order 1):
 *  http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH
 *  returned value is:
 *  chi1C is the Carbon connectivity index (order 1);
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
     *  Evaluate the descriptor for the method.
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
        Iterator bonds = atomContainer.bonds();
        IBond bond = null;
        while (bonds.hasNext()) {
            bond = (IBond)bonds.next();
            if ((!bond.getAtom(0).getSymbol().equals("H")) || (!bond.getAtom(1).getSymbol().equals("H"))) {
                degrees.clear();
                Iterator iter = bond.atoms();
                while (iter.hasNext()) {
                    atomDegree = 0;
                    java.util.List neighboors = atomContainer.getConnectedAtomsList((IAtom)iter.next());
                    for (int n = 0; n < neighboors.size(); n++) {
                        if (!((IAtom)neighboors.get(n)).getSymbol().equals("H")) {
                            atomDegree += 1;
                        }
                    }
                    if(atomDegree > 0) {
                        degrees.add(new Double(atomDegree));
                    }
                }
                val0 = ( (Double)degrees.get(0) ).doubleValue();
                val1 = ( (Double)degrees.get(1) ).doubleValue();
                if((bond.getAtom(0).getSymbol().equals("C")) && (bond.getAtom(1).getSymbol().equals("C"))) {
                    chi1C += 1/(Math.sqrt(val0 * val1));
                }
            }
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(chi1C),
                new String[] {"chi1C"});
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
        return new DoubleResult(0.0);
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

