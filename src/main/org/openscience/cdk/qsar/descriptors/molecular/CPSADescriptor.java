/*
 *  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.geometry.surface.NumericalSurface;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Calculates 29 Charged Partial Surface Area (CPSA) descriptors.
 * <p/>
 * The CPSA's were developed by Stanton et al. ({@cdk.cite STA90}) and
 * are related to the Polar Surface Area descriptors. The original
 * implementation was in the ADAPT software package and the the definitions
 * of the individual descriptors are presented in the following table. This class
 * returns a <code>DoubleArrayResult</code> containing the 29 descriptors in the order
 * described in the table.
 * <table border=1 cellpadding=2>
 * <caption><a name="cpsa">A Summary of the 29 CPSA Descriptors</a></caption>
 * <thead>
 * <tr>
 * <th>IDescriptor</th><th>Meaning</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>PPSA-1</td><td> partial positive surface area -- sum of surface area on positive parts of molecule</td></tr><tr>
 * <td>PPSA-2</td><td> partial positive surface area * total positive charge on the molecule </td></tr><tr>
 * <td>PPSA-3</td><td> charge weighted partial positive surface area</td></tr><tr>
 * <td>PNSA-1</td><td> partial negative surface area -- sum of surface area on negative parts of molecule</td></tr><tr>
 * <td>PNSA-2</td><td> partial negative surface area * total negative charge on the molecule</td></tr><tr>
 * <td>PNSA-3</td><td> charge weighted partial negative surface area</td></tr><tr>
 * <td>    DPSA-1</td><td> difference of PPSA-1 and PNSA-1</td></tr><tr>
 * <td>    DPSA-2</td><td> difference of FPSA-2 and PNSA-2</td></tr><tr>
 * <td>    DPSA-3</td><td> difference of PPSA-3 and PNSA-3</td></tr><tr>
 * <td>    FPSA-1</td><td> PPSA-1 / total molecular surface area</td></tr><tr>
 * <td>    FFSA-2  </td><td>PPSA-2 / total molecular surface area</td></tr><tr>
 * <td>    FPSA-3</td><td> PPSA-3 / total molecular surface area</td></tr><tr>
 * <td>    FNSA-1</td><td> PNSA-1 / total molecular surface area</td></tr><tr>
 * <td>    FNSA-2</td><td> PNSA-2 / total molecular surface area</td></tr><tr>
 * <td>    FNSA-3</td><td> PNSA-3 / total molecular surface area</td></tr><tr>
 * <td>    WPSA-1</td><td> PPSA-1 *  total molecular surface area / 1000</td></tr><tr>
 * <td>WPSA-2</td><td>    PPSA-2 * total molecular surface area /1000</td></tr><tr>
 * <td>WPSA-3</td><td>  PPSA-3 * total molecular surface area / 1000</td></tr><tr>
 * <td>WNSA-1</td><td>  PNSA-1 *  total molecular surface area /1000</td></tr><tr>
 * <td>WNSA-2</td><td> PNSA-2 * total molecular surface area / 1000</td></tr><tr>
 * <td>WNSA-3</td><td> PNSA-3 * total molecular surface area / 1000</td></tr><tr>
 * <td>RPCG</td><td> relative positive charge --  most positive charge / total positive charge</td></tr><tr>
 * <td>    RNCG    </td><td>relative negative charge -- most negative charge / total negative charge</td></tr><tr>
 * <td>    RPCS    </td><td>relative positive charge surface area -- most positive surface area * RPCG</td></tr><tr>
 * <td>    RNCS    </td><td>relative negative charge surface area -- most negative surface area * RNCG</td></tr>
 * <tr>
 * <td>THSA</td>
 * <td>sum of solvent accessible surface areas of
 * atoms with absolute value of partial charges
 * less than 0.2
 * </td>
 * </tr>
 * <tr>
 * <td>TPSA</td>
 * <td>sum of solvent accessible surface areas of
 * atoms with absolute value of partial charges
 * greater than or equal 0.2
 * </td>
 * </tr>
 * <tr>
 * <td>RHSA</td>
 * <td>THSA / total molecular surface area
 * </td>
 * </tr>
 * <tr>
 * <td>RPSA</td>
 * <td>TPSA / total molecular  surface area
 * </td>
 * </tr>
 * </tbody>
 * </table>
 * <p/>
 * <b>NOTE</b>: The values calculated by this implementation will differ from those
 * calculated by the original ADAPT implementation of the CPSA descriptors. This
 * is because the original implementation used an analytical surface area algorithm
 * and used partial charges obtained from MOPAC using the AM1 Hamiltonian.
 * This implementation uses a numerical
 * algorithm to obtain surface areas (see {@link NumericalSurface}) and obtains partial
 * charges using the Gasteiger-Marsilli algorithm (see {@link GasteigerMarsiliPartialCharges}).
 * <p/>
 * However, a comparison of the values calculated by the two implementations indicates
 * that they are qualitatively the same.
 * <p/>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td></td>
 * <td>no parameters</td>
 * </tr>
 * </table>
 *
 * @author Rajarshi Guha
 * @cdk.created 2005-05-16
 * @cdk.module qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:CPSA
 */
public class CPSADescriptor implements IMolecularDescriptor {

    private static final String[] names = {
            "PPSA-1", "PPSA-2", "PPSA-3",
            "PNSA-1", "PNSA-2", "PNSA-3",
            "DPSA-1", "DPSA-2", "DPSA-3",
            "FPSA-1", "FPSA-2", "FPSA-3",
            "FNSA-1", "FNSA-2", "FNSA-3",
            "WPSA-1", "WPSA-2", "WPSA-3",
            "WNSA-1", "WNSA-2", "WNSA-3",
            "RPCG", "RNCG", "RPCS", "RNCS",
            "THSA", "TPSA", "RHSA", "RPSA"
    };

    private LoggingTool logger;

    public CPSADescriptor() {
        logger = new LoggingTool(this);
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#CPSA",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    /**
     * Sets the parameters attribute of the CPSADescriptor object.
     *
     * @param params The new parameters value
     * @throws CDKException Description of the Exception
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Gets the parameters attribute of the CPSADescriptor object.
     *
     * @return The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }

    /**
     * Gets the parameterNames attribute of the CPSADescriptor object.
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     * Gets the parameterType attribute of the CPSADescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }

    /**
     * Evaluates the 29 CPSA descriptors using Gasteiger-Marsilli charges.
     *
     * @param atomContainer Parameter is the atom container.
     * @return An ArrayList containing 29 elements in the order described above
     */

    public DescriptorValue calculate(IAtomContainer atomContainer) {
        DoubleArrayResult retval = new DoubleArrayResult();

        if (!GeometryTools.has3DCoordinates(atomContainer)) {
            for (int i = 0; i < 29; i++) retval.add(Double.NaN);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    retval, getDescriptorNames(), new CDKException("Molecule must have 3D coordinates"));
        }

        IAtomContainer container;
        try {
            container = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            logger.debug("Error during clone");
             for (int i = 0; i < 29; i++) retval.add(Double.NaN);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    retval, getDescriptorNames(), new CDKException("Error during clone"+e.getMessage()));
        }

//        IsotopeFactory factory = null;
//        try {
//            factory = IsotopeFactory.getInstance(container.getBuilder());
//        } catch (Exception e) {
//            logger.debug(e);
//        }

        GasteigerMarsiliPartialCharges peoe;
        try {
            peoe = new GasteigerMarsiliPartialCharges();
            peoe.assignGasteigerMarsiliSigmaPartialCharges(container, true);
        } catch (Exception e) {
            logger.debug("Error in assigning Gasteiger-Marsilli charges");
            for (int i = 0; i < 29; i++) retval.add(Double.NaN);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    retval, getDescriptorNames(), new CDKException("Error in getting G-M charges"));
        }

        NumericalSurface surface;
        try {
            surface = new NumericalSurface(container);
            surface.calculateSurface();
        } catch (NullPointerException npe) {
            logger.debug("Error in surface area calculation");
            for (int i = 0; i < 29; i++) retval.add(Double.NaN);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    retval, getDescriptorNames(),
                    new CDKException("Error in surface area calculation"));
        }

        //double molecularWeight = mfa.getMass();
        double[] atomSurfaces = surface.getAllSurfaceAreas();
        double totalSA = surface.getTotalSurfaceArea();

        double ppsa1 = 0.0;
        double ppsa3 = 0.0;
        double pnsa1 = 0.0;
        double pnsa3 = 0.0;
        double totpcharge = 0.0;
        double totncharge = 0.0;
        for (int i = 0; i < container.getAtomCount(); i++) {
            if (container.getAtom(i).getCharge() > 0) {
                ppsa1 += atomSurfaces[i];
                ppsa3 += container.getAtom(i).getCharge() * atomSurfaces[i];
                totpcharge += container.getAtom(i).getCharge();
            } else {
                pnsa1 += atomSurfaces[i];
                pnsa3 += container.getAtom(i).getCharge() * atomSurfaces[i];
                totncharge += container.getAtom(i).getCharge();
            }
        }

        double ppsa2 = ppsa1 * totpcharge;
        double pnsa2 = pnsa1 * totncharge;

        // fractional +ve & -ve SA
        double fpsa1 = ppsa1 / totalSA;
        double fpsa2 = ppsa2 / totalSA;
        double fpsa3 = ppsa3 / totalSA;
        double fnsa1 = pnsa1 / totalSA;
        double fnsa2 = pnsa2 / totalSA;
        double fnsa3 = pnsa3 / totalSA;

        // surface wtd +ve & -ve SA
        double wpsa1 = ppsa1 * totalSA / 1000;
        double wpsa2 = ppsa2 * totalSA / 1000;
        double wpsa3 = ppsa3 * totalSA / 1000;
        double wnsa1 = pnsa1 * totalSA / 1000;
        double wnsa2 = pnsa2 * totalSA / 1000;
        double wnsa3 = pnsa3 * totalSA / 1000;

        // hydrophobic and poalr surface area 
        double phobic = 0.0;
        double polar = 0.0;
        for (int i = 0; i < container.getAtomCount(); i++) {
            if (Math.abs(container.getAtom(i).getCharge()) < 0.2) {
                phobic += atomSurfaces[i];
            } else {
                polar += atomSurfaces[i];
            }
        }
        double thsa = phobic;
        double tpsa = polar;
        double rhsa = phobic / totalSA;
        double rpsa = polar / totalSA;

        // differential +ve & -ve SA
        double dpsa1 = ppsa1 - pnsa1;
        double dpsa2 = ppsa2 - pnsa2;
        double dpsa3 = ppsa3 - pnsa3;

        double maxpcharge = 0.0;
        double maxncharge = 0.0;
        int pidx = 0;
        int nidx = 0;
        for (int i = 0; i < container.getAtomCount(); i++) {
            double charge = container.getAtom(i).getCharge();
            if (charge > maxpcharge) {
                maxpcharge = charge;
                pidx = i;
            }
            if (charge < maxncharge) {
                maxncharge = charge;
                nidx = i;
            }
        }

        // relative descriptors
        double rpcg = maxpcharge / totpcharge;
        double rncg = maxncharge / totncharge;
        double rpcs = atomSurfaces[pidx] * rpcg;
        double rncs = atomSurfaces[nidx] * rncg;

        // fill in the values
        retval.add(ppsa1);
        retval.add(ppsa2);
        retval.add(ppsa3);
        retval.add(pnsa1);
        retval.add(pnsa2);
        retval.add(pnsa3);

        retval.add(dpsa1);
        retval.add(dpsa2);
        retval.add(dpsa3);

        retval.add(fpsa1);
        retval.add(fpsa2);
        retval.add(fpsa3);
        retval.add(fnsa1);
        retval.add(fnsa2);
        retval.add(fnsa3);

        retval.add(wpsa1);
        retval.add(wpsa2);
        retval.add(wpsa3);
        retval.add(wnsa1);
        retval.add(wnsa2);
        retval.add(wnsa3);

        retval.add(rpcg);
        retval.add(rncg);
        retval.add(rpcs);
        retval.add(rncs);

        retval.add(thsa);
        retval.add(tpsa);
        retval.add(rhsa);
        retval.add(rpsa);


        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                retval, getDescriptorNames());
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
        return new DoubleArrayResultType(29);
    }
}


