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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.qsar.AbstractBondDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * The calculation of bond total Partial charge is calculated determining the difference the Partial
 * Total Charge on atoms A and B of a bond. Based in Gasteiger Charge.
 *
 * <table border="1"><caption>Parameters for this descriptor:</caption>
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
 * @author Miguel Rojas
 * @cdk.created 2006-05-18
 * @cdk.module qsarbond
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:bondPartialTCharge
 * @see org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptor
 * @see org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor
 */
public class BondPartialTChargeDescriptor extends AbstractBondDescriptor {

    private GasteigerMarsiliPartialCharges peoe = null;
    private GasteigerPEPEPartialCharges pepe = null;

    /** Number of maximum iterations */
    private int maxIterations = -1;
    /** Number of maximum resonance structures */
    private int maxResonStruc = -1;
    /** make a lone pair electron checker. Default true */
    private boolean lpeChecker = true;

    private static final String[] NAMES = {"pCB"};

    /** Constructor for the BondPartialTChargeDescriptor object. */
    public BondPartialTChargeDescriptor() {
        peoe = new GasteigerMarsiliPartialCharges();
        pepe = new GasteigerPEPEPartialCharges();
    }

    /**
     * Gets the specification attribute of the BondPartialTChargeDescriptor object.
     *
     * @return The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondPartialTCharge",
                this.getClass().getName(),
                "The Chemistry Development Kit");
    }

    /** This descriptor does have any parameter. */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 3)
            throw new CDKException("PartialPiChargeDescriptor only expects three parameter");

        if (!(params[0] instanceof Integer))
            throw new CDKException("The parameter must be of type Integer");
        maxIterations = (Integer) params[0];

        if (params.length > 1 && params[1] != null) {
            if (!(params[1] instanceof Boolean))
                throw new CDKException("The parameter must be of type Boolean");
            lpeChecker = (Boolean) params[1];
        }

        if (params.length > 2 && params[2] != null) {
            if (!(params[2] instanceof Integer))
                throw new CDKException("The parameter must be of type Integer");
            maxResonStruc = (Integer) params[2];
        }
    }

    /**
     * Gets the parameters attribute of the BondPartialTChargeDescriptor object.
     *
     * @return The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[3];
        params[0] = maxIterations;
        params[1] = lpeChecker;
        params[2] = maxResonStruc;
        return params;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(
                getSpecification(),
                getParameterNames(),
                getParameters(),
                new DoubleResult(Double.NaN),
                NAMES,
                e);
    }

    /**
     * The method calculates the bond total Partial charge of a given bond It is needed to call the
     * addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     * @param ac AtomContainer
     * @return return the sigma electronegativity
     */
    @Override
    public DescriptorValue calculate(IBond bond, IAtomContainer ac) {
        // FIXME: for now I'll cache a few modified atomic properties, and restore them at the end
        // of this method
        Double originalCharge1 = bond.getBegin().getCharge();
        String originalAtomtypeName1 = bond.getBegin().getAtomTypeName();
        Integer originalNeighborCount1 = bond.getBegin().getFormalNeighbourCount();
        IAtomType.Hybridization originalHybridization1 = bond.getBegin().getHybridization();
        Integer originalValency1 = bond.getBegin().getValency();
        Double originalCharge2 = bond.getEnd().getCharge();
        String originalAtomtypeName2 = bond.getEnd().getAtomTypeName();
        Integer originalNeighborCount2 = bond.getEnd().getFormalNeighbourCount();
        IAtomType.Hybridization originalHybridization2 = bond.getEnd().getHybridization();
        Integer originalValency2 = bond.getEnd().getValency();
        Double originalBondOrderSum1 = bond.getBegin().getBondOrderSum();
        Order originalMaxBondOrder1 = bond.getBegin().getMaxBondOrder();
        Double originalBondOrderSum2 = bond.getEnd().getBondOrderSum();
        Order originalMaxBondOrder2 = bond.getEnd().getMaxBondOrder();
        if (!isCachedAtomContainer(ac)) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
                if (lpeChecker) {
                    LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                    lpcheck.saturate(ac);
                }
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }

            if (maxIterations != -1) peoe.setMaxGasteigerIters(maxIterations);
            if (maxIterations != -1) pepe.setMaxGasteigerIters(maxIterations);
            if (maxResonStruc != -1) pepe.setMaxResoStruc(maxResonStruc);

            try {
                peoe.assignGasteigerMarsiliSigmaPartialCharges(ac, true);
                List<Double> peoeBond = new ArrayList<Double>();
                for (Iterator<IBond> it = ac.bonds().iterator(); it.hasNext(); ) {
                    IBond bondi = it.next();
                    double result =
                            Math.abs(bondi.getBegin().getCharge() - bondi.getEnd().getCharge());
                    peoeBond.add(result);
                }

                for (Iterator<IAtom> it = ac.atoms().iterator(); it.hasNext(); )
                    it.next().setCharge(0.0);

                pepe.assignGasteigerPiPartialCharges(ac, true);
                for (int i = 0; i < ac.getBondCount(); i++) {
                    IBond bondi = ac.getBond(i);
                    double result =
                            Math.abs(bondi.getBegin().getCharge() - bondi.getEnd().getCharge());
                    cacheDescriptorValue(bondi, ac, new DoubleResult(peoeBond.get(i) + result));
                }
            } catch (Exception e) {
                return getDummyDescriptorValue(e);
            }
        }
        bond.getBegin().setCharge(originalCharge1);
        bond.getBegin().setAtomTypeName(originalAtomtypeName1);
        bond.getBegin().setHybridization(originalHybridization1);
        bond.getBegin().setValency(originalValency1);
        bond.getBegin().setFormalNeighbourCount(originalNeighborCount1);
        bond.getEnd().setCharge(originalCharge2);
        bond.getEnd().setAtomTypeName(originalAtomtypeName2);
        bond.getEnd().setHybridization(originalHybridization2);
        bond.getEnd().setValency(originalValency2);
        bond.getEnd().setFormalNeighbourCount(originalNeighborCount2);
        bond.getBegin().setMaxBondOrder(originalMaxBondOrder1);
        bond.getBegin().setBondOrderSum(originalBondOrderSum1);
        bond.getEnd().setMaxBondOrder(originalMaxBondOrder2);
        bond.getEnd().setBondOrderSum(originalBondOrderSum2);

        return getCachedDescriptorValue(bond) != null
                ? new DescriptorValue(
                        getSpecification(),
                        getParameterNames(),
                        getParameters(),
                        getCachedDescriptorValue(bond),
                        NAMES)
                : null;
    }

    /**
     * Gets the parameterNames attribute of the BondPartialTChargeDescriptor object.
     *
     * @return The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        String[] params = new String[3];
        params[0] = "maxIterations";
        params[1] = "lpeChecker";
        params[2] = "maxResonStruc";
        return params;
    }

    /**
     * Gets the parameterType attribute of the BondPartialTChargeDescriptor object.
     *
     * @param name Description of the Parameter
     * @return An Object of class equal to that of the parameter being requested
     */
    @Override
    public Object getParameterType(String name) {
        if ("maxIterations".equals(name)) return Integer.MAX_VALUE;
        if ("lpeChecker".equals(name)) return Boolean.TRUE;
        if ("maxResonStruc".equals(name)) return Integer.MAX_VALUE;
        return null;
    }
}
