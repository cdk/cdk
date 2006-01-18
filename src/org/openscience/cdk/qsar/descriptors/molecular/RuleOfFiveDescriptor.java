/*  $RCSfile$
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
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.*;
import org.openscience.cdk.qsar.descriptors.atomic.WeightDescriptor;

/**
 * This Class contains a method that returns the number failures of the
 * Lipinski's Rule Of 5.
 * See <a href="http://www.lifechemicals.com/eng/services/HTS/five/">http://www.lifechemicals.com/eng/services/HTS/five/</a>.
 *
  * <p>This descriptor uses these parameters:
 * <table border="1">
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
 * @cdk.dictref qsar-descriptors:lipinskifailures
 */
public class RuleOfFiveDescriptor implements IDescriptor {
    private boolean checkAromaticity = false;


    /**
     *  Constructor for the RuleOfFiveDescriptor object.
     */
    public RuleOfFiveDescriptor() { }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class. 
     *
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     *  this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#lipinskifailures",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the RuleOfFiveDescriptor object.
         *
         *  There is only one parameter, which should be a Boolean indicating whether
         *  aromaticity should be checked or has already been checked. The name of the paramete
         *  is checkAromaticity.
     *
     *@param  params            Parameter is only one: a boolean.
     *@throws  CDKException  if more than 1 parameter or a non-Boolean parameter is specified
         *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) {
            throw new CDKException("RuleOfFiveDescriptor expects one parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The first parameter must be of type Boolean");
        }
        // ok, all should be fine
        checkAromaticity = ((Boolean) params[0]).booleanValue();
    }


    /**
     *  Gets the parameters attribute of the RuleOfFiveDescriptor object.
     *
     *@return    The parameters value
         *@see #setParameters
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Boolean(checkAromaticity);
        return params;
    }


    /**
     *  the method take a boolean checkAromaticity: if the boolean is true, it means that
     *  aromaticity has to be checked.
     *
     *@param  mol   AtomContainer for which this descriptor is to be calculated
     *@return    The number of failures of the Lipinski rule
     *@throws  CDKException  if the following descriptors throw an exception:
         * {@link org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor},  {@link HBondAcceptorCountDescriptor}, {@link HBondDonorCountDescriptor},
         * {@link org.openscience.cdk.qsar.descriptors.atomic.WeightDescriptor}, {@link org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor},
     */
    public DescriptorValue calculate(IAtomContainer mol) throws CDKException {

        int lipinskifailures = 0;

        IDescriptor xlogP = new XLogPDescriptor();
        Object[] xlogPparams = {
        	new Boolean(checkAromaticity),
        	Boolean.TRUE,
        };
        xlogP.setParameters(xlogPparams);
        double xlogPvalue = ((DoubleResult)xlogP.calculate(mol).getValue()).doubleValue();

        IDescriptor acc = new HBondAcceptorCountDescriptor();
        Object[] hBondparams = { new Boolean(checkAromaticity) };
        acc.setParameters(hBondparams);
        int acceptors = ((IntegerResult)acc.calculate(mol).getValue()).intValue();

        IDescriptor don = new HBondDonorCountDescriptor();
        don.setParameters(hBondparams);
        int donors = ((IntegerResult)don.calculate(mol).getValue()).intValue();

        IDescriptor mw = new WeightDescriptor();
        Object[] mwparams = {new String("")};
        mw.setParameters(mwparams);
        double mwvalue = ((DoubleResult)mw.calculate(mol).getValue()).doubleValue();

        IDescriptor rotata = new RotatableBondsCountDescriptor();
        rotata.setParameters(hBondparams);
        int rotatablebonds = ((IntegerResult)rotata.calculate(mol).getValue()).intValue();

        if(xlogPvalue > 5.0) { lipinskifailures += 1; }
        if(acceptors > 10) { lipinskifailures += 1; }
        if(donors > 5) { lipinskifailures += 1; }
        if(mwvalue > 500.0) { lipinskifailures += 1; }
        if(rotatablebonds > 10.0) { lipinskifailures += 1; }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(lipinskifailures));
    }


    /**
     *  Gets the parameterNames attribute of the RuleOfFiveDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkAromaticity";
        return params;
    }



    /**
     *  Gets the parameterType attribute of the RuleOfFiveDescriptor object.
     *
     *@param  name  The name of the parameter. In this case it is 'checkAromaticity'.
     *@return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return new Boolean(true);
    }
}

