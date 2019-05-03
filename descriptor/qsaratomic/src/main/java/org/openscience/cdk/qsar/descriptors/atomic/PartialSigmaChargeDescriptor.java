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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  The calculation of sigma partial charges in sigma-bonded systems of an heavy atom
 *  was made by Marsilli-Gasteiger. It is implemented with the Partial Equalization
 *  of Orbital Electronegativity (PEOE).
 *
 * <table border="1"><caption>Parameters for this descriptor:</caption>
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>maxIterations</td>
 *     <td>0</td>
 *     <td>Number of maximum iterations</td>
 *   </tr>
 * </table>
 *
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-04-15
 * @cdk.module  qsaratomic
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:partialSigmaCharge
 * @see GasteigerMarsiliPartialCharges
 */
public class PartialSigmaChargeDescriptor extends AbstractAtomicDescriptor {

    private static final String[]          NAMES = {"partialSigmaCharge"};

    private GasteigerMarsiliPartialCharges peoe  = null;
    /**Number of maximum iterations*/
    private int                            maxIterations;

    /**
     *  Constructor for the PartialSigmaChargeDescriptor object
     */
    public PartialSigmaChargeDescriptor() {
        peoe = new GasteigerMarsiliPartialCharges();
    }

    /**
     *  Gets the specification attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialSigmaCharge", this
                        .getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@param  params            Number of maximum iterations
     *@exception  CDKException  Description of the Exception
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
     *  Gets the parameters attribute of the PartialSigmaChargeDescriptor object
     *
     *@return    The parameters value
     */
    @Override
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = maxIterations;
        return params;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     *  The method returns apha partial charges assigned to an heavy atom through Gasteiger Marsili
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *  For this method will be only possible if the heavy atom has single bond.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   Value of the alpha partial charge
     */
    @Override
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {
        if (!isCachedAtomContainer(ac)) {
            Double[] org = new Double[ac.getAtomCount()];
            for (int i = 0; i < org.length; i++)
                org[i] = ac.getAtom(i).getCharge();
            IAtomContainer mol = atom.getBuilder().newInstance(IAtomContainer.class, ac);
            if (maxIterations != 0) peoe.setMaxGasteigerIters(maxIterations);
            try {
                peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);
                for (int i = 0; i < ac.getAtomCount(); i++) {
                    // assume same order, so mol.getAtom(i) == ac.getAtom(i)
                    cacheDescriptorValue(ac.getAtom(i), ac, new DoubleResult(mol.getAtom(i).getCharge()));
                }
            } catch (Exception e) {
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                        Double.NaN), NAMES, e);
            }
            for (int i = 0; i < org.length; i++)
                mol.getAtom(i).setCharge(org[i]);
        }
        return getCachedDescriptorValue(atom) != null ? new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), getCachedDescriptorValue(atom), NAMES) : null;
    }

    /**
     *  Gets the parameterNames attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }

    /**
     *  Gets the parameterType attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @Override
    public Object getParameterType(String name) {
        if ("maxIterations".equals(name)) return Integer.MAX_VALUE;
        return null;
    }
}
