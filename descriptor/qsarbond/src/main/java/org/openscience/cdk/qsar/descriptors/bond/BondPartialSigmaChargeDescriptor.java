/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.bond;

import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.AbstractBondDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

import java.util.Iterator;

/**
 *  The calculation of bond-sigma Partial charge is calculated
 *  determining the difference the Partial Sigma Charge on atoms
 *  A and B of a bond. Based in Gasteiger Charge.
 *  <table border="1"><caption>Parameters for this descriptor:</caption>
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>bondPosition</td>
 *     <td>0</td>
 *     <td>The position of the target bond</td>
 *   </tr>
 * </table>
 *
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-05-08
 * @cdk.module  qsarbond
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:bondPartialSigmaCharge
 *
 * @see org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor
 */
public class BondPartialSigmaChargeDescriptor extends AbstractBondDescriptor {

    private GasteigerMarsiliPartialCharges peoe = null;
    /**Number of maximum iterations*/
    private int maxIterations;

    private static final String[] NAMES = {"peoeB"};

    /**
     *  Constructor for the BondPartialSigmaChargeDescriptor object.
     */
    public BondPartialSigmaChargeDescriptor() {
        peoe = new GasteigerMarsiliPartialCharges();
    }

    /**
     *  Gets the specification attribute of the BondPartialSigmaChargeDescriptor
     *  object.
     *
     *@return The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondPartialSigmaCharge", this
                .getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * This descriptor does have any parameter.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("PartialSigmaChargeDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter 1 must be of type Integer");
        }
        maxIterations = (Integer) params[0];
    }

    /**
     *  Gets the parameters attribute of the BondPartialSigmaChargeDescriptor object.
     *
     *@return The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = (Integer) maxIterations;
        return params;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                Double.NaN), NAMES, e);
    }

    /**
     *  The method calculates the bond-sigma Partial charge of a given bond
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  ac                AtomContainer
     *@return return the sigma electronegativity
     */
    @Override
    public DescriptorValue calculate(IBond bond, IAtomContainer ac) {
        // FIXME: for now I'll cache a few modified atomic properties, and restore them at the end of this method
        Double originalCharge1 = bond.getBegin().getCharge();
        Double originalCharge2 = bond.getEnd().getCharge();
        if (!isCachedAtomContainer(ac)) {
            IAtomContainer mol = ac.getBuilder().newInstance(IAtomContainer.class, ac);
            if (maxIterations != 0) peoe.setMaxGasteigerIters(maxIterations);
            try {
                peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);
                for (Iterator<IBond> it = ac.bonds().iterator(); it.hasNext();) {
                    IBond bondi = it.next();
                    double result = Math.abs(bondi.getBegin().getCharge() - bondi.getEnd().getCharge());
                    cacheDescriptorValue(bondi, ac, new DoubleResult(result));
                }
            } catch (Exception ex1) {
                return getDummyDescriptorValue(ex1);
            }
        }
        bond.getBegin().setCharge(originalCharge1);
        bond.getEnd().setCharge(originalCharge2);
        return getCachedDescriptorValue(bond) != null ? new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), getCachedDescriptorValue(bond), NAMES) : null;
    }

    /**
    * Gets the parameterNames attribute of the BondPartialSigmaChargeDescriptor object.
    *
    * @return    The parameterNames value
    */
    @Override
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }

    /**
     * Gets the parameterType attribute of the BondPartialSigmaChargeDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @Override
    public Object getParameterType(String name) {
        if ("maxIterations".equals(name)) return Integer.MAX_VALUE;
        return null;
    }
}
