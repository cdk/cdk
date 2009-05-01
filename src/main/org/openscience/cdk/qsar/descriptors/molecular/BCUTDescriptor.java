/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
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

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Eigenvalue based descriptor noted for its utility in chemical diversity.
 * Described by Pearlman et al. {@cdk.cite PEA99}.
 * <p/>
 * <p>The descriptor is based on a weighted version of the Burden matrix {@cdk.cite BUR89, BUR97}
 * which takes into account both the connectivity as well as atomic
 * properties of a molecule. The weights are a variety of atom properties placed along the
 * diagonal of the Burden matrix. Currently three weighting schemes are employed
 * <ul>
 * <li>atomic weight
 * <li>partial charge (Gasteiger Marsilli)
 * <li>polarizability {@cdk.cite KJ81}
 * </ul>
 * <p>By default, the descriptor will return the highest and lowest eigenvalues for the three
 * classes of descriptor in a single ArrayList (in the order shown above). However it is also
 * possible to supply a parameter list indicating how many of the highest and lowest eigenvalues
 * (for each class of descriptor) are required. The descriptor works with the hydrogen depleted molecule.
 * <p/>
 * A side effect of specifying the number of highest and lowest eigenvalues is that it is possible
 * to get two copies of all the eigenvalues. That is, if a molecule has 5 heavy atoms, then specifying
 * the 5 highest eigenvalues returns all of them, and specifying the 5 lowest eigenvalues returns
 * all of them, resulting in two copies of all the eigenvalues.
 * <p/>
 * <p> Note that it is possible to
 * specify an arbitrarily large number of eigenvalues to be returned. However if the number
 * (i.e., nhigh or nlow) is larger than the number of heavy atoms, the remaining eignevalues
 * will be NaN.
 * <p/>
 * Given the above description, if the aim is to gt all the eigenvalues for a molecule, you should
 * set nlow to 0 and specify the number of heavy atoms (or some large number) for nhigh (or vice versa).
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>nhigh</td>
 * <td>1</td>
 * <td>The number of highest eigenvalue</td>
 * </tr>
 * <tr>
 * <td>nlow</td>
 * <td>1</td>
 * <td>The number of lowest eigenvalue</td>
 * </tr>
 * <tr>
 * <td>checkAromaticity</td>
 * <td>true</td>
 * <td>Whether aromaticity should be checked</td>
 * </tr>
 * </table>
 * <p/>
 * Returns an array of values in the following order
 * <ol>
 * <p/>
 * <li>BCUTw-1l, BCUTw-2l ... - <i>nhigh</i> lowest atom weighted BCUTS
 * <li>BCUTw-1h, BCUTw-2h ... - <i>nlow</i> highest atom weighted BCUTS
 * <li>BCUTc-1l, BCUTc-2l ... - <i>nhigh</i> lowest partial charge weighted BCUTS
 * <li>BCUTc-1h, BCUTc-2h ... - <i>nlow</i> highest partial charge weighted BCUTS
 * <li>BCUTp-1l, BCUTp-2l ... - <i>nhigh</i> lowest polarizability weighted BCUTS
 * <li>BCUTp-1h, BCUTp-2h ... - <i>nlow</i> highest polarizability weighted BCUTS
 *
 * @author Rajarshi Guha
 * @cdk.created 2004-11-30
 * @cdk.builddepends Jama-1.0.1.jar
 * @cdk.depends Jama-1.0.1.jar
 * @cdk.module qsarmolecular
 * @cdk.svnrev $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:BCUT
 * @cdk.keyword BCUT
 * @cdk.keyword descriptor
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptorTest")
public class BCUTDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;

    // the number of negative & positive eigenvalues
    // to return for each class of BCUT descriptor
    private int nhigh;
    private int nlow;
    private boolean checkAromaticity;

    public BCUTDescriptor() {
        logger = new LoggingTool(this);

        // set the default number of BCUT's
        this.nhigh = 1;
        this.nlow = 1;
        this.checkAromaticity = true;
    }

    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#BCUT",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    /**
     * Sets the parameters attribute of the BCUTDescriptor object.
     *
     * @param params The new parameter values. This descriptor takes 3 parameters: number of highest
     *               eigenvalues and number of lowest eigenvalues. If 0 is specified for either (the default)
     *               then all calculated eigenvalues are returned. The third parameter checkAromaticity is a boolean.
     *               If checkAromaticity is true, the method check the aromaticity, if false, means that the aromaticity has
     *               already been checked.
     * @throws CDKException if the parameters are of the wrong type
     * @see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        // we expect 3 parameters
        if (params.length != 3) {
            throw new CDKException("BCUTDescriptor requires 3 parameters");
        }
        if (!(params[0] instanceof Integer) || !(params[1] instanceof Integer)) {
            throw new CDKException("Parameters must be of type Integer");
        } else if (!(params[2] instanceof Boolean)) {
            throw new CDKException("The third parameter must be of type Boolean");
        }
        // ok, all should be fine

        this.nhigh = (Integer) params[0];
        this.nlow = (Integer) params[1];
        this.checkAromaticity = (Boolean) params[2];

        if (this.nhigh < 0 || this.nlow < 0) {
            throw new CDKException("Number of eigenvalues to return must be zero or more");
        }
    }

    /**
     * Gets the parameters attribute of the BCUTDescriptor object.
     *
     * @return Three element array of Integer and one boolean representing number of highest and lowest eigenvalues and the checkAromaticity flag
     *         to return respectively
     * @see #setParameters
     */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        Object params[] = new Object[3];
        params[0] = this.nhigh;
        params[1] = this.nlow;
        params[2] = this.checkAromaticity;
        return (params);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        String[] names;
        String[] suffix = {"w", "c", "p"};
        names = new String[3 * nhigh + 3 * nlow];
        int counter = 0;
        for (String aSuffix : suffix) {
            for (int i = 0; i < nhigh; i++) {
                names[counter++] = "BCUT" + aSuffix + "-" + (i + 1) + "l";
            }
            for (int i = 0; i < nlow; i++) {
                names[counter++] = "BCUT" + aSuffix + "-" + (i + 1) + "h";
            }
        }
        return names;
    }

    /**
     * Gets the parameterNames attribute of the BCUTDescriptor object.
     *
     * @return The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[3];
        params[0] = "nhigh";
        params[1] = "nlow";
        params[2] = "checkAromaticity";
        return (params);
    }


    /**
     * Gets the parameterType attribute of the BCUTDescriptor object.
     *
     * @param name Description of the Parameter (can be either 'nhigh' or 'nlow' or checkAromaticity)
     * @return The parameterType value
     */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        Object object = null;
        if (name.equals("nhigh")) object = 1;
        if (name.equals("nlow")) {
            object = 1;
        }
        if (name.equals("checkAromaticity")) object = true;
        return (object);
    }


    static private class BurdenMatrix {

        static double[][] evalMatrix(IAtomContainer atomContainer, double[] vsd) {
            IAtomContainer local = AtomContainerManipulator.removeHydrogens(atomContainer);
            
            int natom = local.getAtomCount();
            double[][] matrix = new double[natom][natom];
            for (int i = 0; i < natom; i++) {
                for (int j = 0; j < natom; j++) {
                    matrix[i][j] = 0.0;
                }
            }

            /* set the off diagonal entries */
            for (int i = 0; i < natom - 1; i++) {
                for (int j = i + 1; j < natom; j++) {
                    for (int k = 0; k < local.getBondCount(); k++) {
                        IBond bond = local.getBond(k);
                        if (bond.contains(local.getAtom(i)) && bond.contains(local.getAtom(j))) {
                            if (bond.getFlag(CDKConstants.ISAROMATIC)) matrix[i][j] = 0.15;
                            else if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE) matrix[i][j] = 0.1;
                            else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE) matrix[i][j] = 0.2;
                            else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE) matrix[i][j] = 0.3;

                            if (local.getConnectedBondsCount(i) == 1 || local.getConnectedBondsCount(j) == 1) {
                                matrix[i][j] += 0.01;
                            }
                            matrix[j][i] = matrix[i][j];
                        } else {
                            matrix[i][j] = 0.001;
                            matrix[j][i] = 0.001;
                        }
                    }
                }
            }

            /* set the diagonal entries */
            for (int i = 0; i < natom; i++) {
                if (vsd != null) matrix[i][i] = vsd[i];
                else matrix[i][i] = 0.0;
            }
            return (matrix);
        }
    }

    /**
     * Calculates the three classes of BCUT descriptors.
     *
     * @param container Parameter is the atom container.
     * @return An ArrayList containing the descriptors. The default is to return
     *         all calculated eigenvalues of the Burden matrices in the order described
     *         above. If a parameter list was supplied, then only the specified number
     *         of highest and lowest eigenvalues (for each class of BCUT) will be returned.     
     */
    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer container) {
        int counter;
        Molecule molecule;
        try {
            molecule = (Molecule) container.clone();
        } catch (CloneNotSupportedException e) {
            logger.debug("Error during clone");
            return getDummyDescriptorValue(new CDKException("Error occured during clone " + e));
        }

        // add H's in case they're not present
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);            
            CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(molecule.getBuilder());
            hAdder.addImplicitHydrogens(molecule);
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
        } catch (Exception e) {
            return getDummyDescriptorValue(new CDKException("Could not add hydrogens: " + e.getMessage(), e));
        }

        // do aromaticity detecttion for calculating polarizability later on
        if (this.checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            } catch (CDKException e) {
                return getDummyDescriptorValue(new CDKException("Error in atom typing: " + e.getMessage(), e));
            }
            try {
                CDKHueckelAromaticityDetector.detectAromaticity(molecule);
            } catch (CDKException e) {
                return getDummyDescriptorValue(new CDKException("Error in aromaticity perception: "+e.getMessage()));
            }
        }

        // find number of heavy atoms
        int nheavy = 0;
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            if (!molecule.getAtom(i).getSymbol().equals("H")) nheavy++;
        }

        if (nheavy == 0) return getDummyDescriptorValue(new CDKException("No heavy atoms in the molecule"));

        double[] diagvalue = new double[nheavy];

        // get atomic mass weighted BCUT
        counter = 0;
        try {
            for (int i = 0; i < molecule.getAtomCount(); i++) {
                if (molecule.getAtom(i).getSymbol().equals("H")) continue;
                diagvalue[counter] = IsotopeFactory.getInstance(molecule.getBuilder()).
                        getMajorIsotope(molecule.getAtom(i).getSymbol()).getExactMass();
                counter++;
            }
        } catch (Exception e) {
            return getDummyDescriptorValue(new CDKException("Could not calculate weight: " + e.getMessage(), e));
        }

        double[][] burdenMatrix = BurdenMatrix.evalMatrix(molecule, diagvalue);
        Matrix matrix = new Matrix(burdenMatrix);
        EigenvalueDecomposition eigenDecomposition = new EigenvalueDecomposition(matrix);
        double[] eval1 = eigenDecomposition.getRealEigenvalues();
        
        // get charge weighted BCUT
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        GasteigerPEPEPartialCharges pepe;
        GasteigerMarsiliPartialCharges peoe;
        try {
            lpcheck.saturate(molecule);
            double[] charges = new double[molecule.getAtomCount()];
//            pepe = new GasteigerPEPEPartialCharges();
//            pepe.calculateCharges(molecule);
//            for (int i = 0; i < molecule.getAtomCount(); i++) charges[i] = molecule.getAtom(i).getCharge();
            peoe = new GasteigerMarsiliPartialCharges();
            peoe.assignGasteigerMarsiliSigmaPartialCharges(molecule, true);
            for (int i = 0; i < molecule.getAtomCount(); i++) charges[i] += molecule.getAtom(i).getCharge();
            for (int i = 0; i < molecule.getAtomCount(); i++) {
                molecule.getAtom(i).setCharge(charges[i]);
            }
        } catch (Exception e) {
            return getDummyDescriptorValue(new CDKException("Could not calculate partial charges: " + e.getMessage(), e));
        }
        counter = 0;
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            if (molecule.getAtom(i).getSymbol().equals("H")) continue;
            diagvalue[counter] = 1.0; molecule.getAtom(i).getCharge();
            counter++;
        }
        burdenMatrix = BurdenMatrix.evalMatrix(molecule, diagvalue);
        matrix = new Matrix(burdenMatrix);
        eigenDecomposition = new EigenvalueDecomposition(matrix);
        double[] eval2 = eigenDecomposition.getRealEigenvalues();


        int[][] topoDistance = PathTools.computeFloydAPSP(AdjacencyMatrix.getMatrix(molecule));

        // get polarizability weighted BCUT
        Polarizability pol = new Polarizability();
        counter = 0;
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            if (molecule.getAtom(i).getSymbol().equals("H")) continue;            
            diagvalue[counter] = pol.calculateGHEffectiveAtomPolarizability(molecule, molecule.getAtom(i), false, topoDistance);
            counter++;
        }
        burdenMatrix = BurdenMatrix.evalMatrix(molecule, diagvalue);
        matrix = new Matrix(burdenMatrix);
        eigenDecomposition = new EigenvalueDecomposition(matrix);
        double[] eval3 = eigenDecomposition.getRealEigenvalues();

        String[] names;
        String[] suffix = {"w", "c", "p"};

        // return only the n highest & lowest eigenvalues
        int lnlow, lnhigh, enlow, enhigh;
        if (nlow > nheavy) {
            lnlow = nheavy;
            enlow = nlow - nheavy;
        } else {
            lnlow = nlow;
            enlow = 0;
        }

        if (nhigh > nheavy) {
            lnhigh = nheavy;
            enhigh = nhigh - nheavy;
        } else {
            lnhigh = nhigh;
            enhigh = 0;
        }

        DoubleArrayResult retval = new DoubleArrayResult( (lnlow+enlow+lnhigh+enhigh) * 3);

        for (int i = 0; i < lnlow; i++) retval.add(eval1[i]);
        for (int i = 0; i < enlow; i++) retval.add(Double.NaN);
        for (int i = 0; i < lnhigh; i++) retval.add(eval1[eval1.length - i - 1]);
        for (int i = 0; i < enhigh; i++) retval.add(Double.NaN);


        for (int i = 0; i < lnlow; i++) retval.add(eval2[i]);
        for (int i = 0; i < enlow; i++) retval.add(Double.NaN);
        for (int i = 0; i < lnhigh; i++) retval.add(eval2[eval2.length - i - 1]);
        for (int i = 0; i < enhigh; i++) retval.add(Double.NaN);


        for (int i = 0; i < lnlow; i++) retval.add(eval3[i]);
        for (int i = 0; i < enlow; i++) retval.add(Double.NaN);
        for (int i = 0; i < lnhigh; i++) retval.add(eval3[eval3.length - i - 1]);
        for (int i = 0; i < enhigh; i++) retval.add(Double.NaN);


        names = new String[3 * nhigh + 3 * nlow];
        counter = 0;
        for (String aSuffix : suffix) {
            for (int i = 0; i < nhigh; i++) {
                names[counter++] = "BCUT" + aSuffix + "-" + (i + 1) + "l";
            }
            for (int i = 0; i < nlow; i++) {
                names[counter++] = "BCUT" + aSuffix + "-" + (i + 1) + "h";
            }
        }


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
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(6);
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult results = new DoubleArrayResult(6);
        for (int i = 0; i < 6; i++) results.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), results, getDescriptorNames(), e);
    }
}


