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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates the Molecular Distance Edge descriptor described in {@cdk.cite LIU98}.
 * This class evaluates the 10 MDE descriptors described by Liu et al. and
 * in addition it calculates variants where O and N are considered (as found in the DEDGE routine
 * from ADAPT).
 * <p/>
 * * The variants are described below:
 * <center>
 * <table border=1>
 * <p/>
 * <tr>
 * <td>MDEC-11</td><td> molecular distance edge between all primary carbons</td></tr><tr>
 * <td>MDEC-12</td><td> molecular distance edge between all primary and secondary carbons</td></tr><tr>
 * <p/>
 * <td>MDEC-13</td><td> molecular distance edge between all primary and tertiary carbons</td></tr><tr>
 * <td>MDEC-14</td><td> molecular distance edge between all primary and quaternary carbons </td></tr><tr>
 * <td>MDEC-22</td><td> molecular distance edge between all secondary carbons </td></tr><tr>
 * <td>MDEC-23</td><td> molecular distance edge between all secondary and tertiary carbons</td></tr><tr>
 * <p/>
 * <td>MDEC-24</td><td> molecular distance edge between all secondary and quaternary carbons </td></tr><tr>
 * <td>MDEC-33</td><td> molecular distance edge between all tertiary carbons</td></tr><tr>
 * <td>MDEC-34</td><td> molecular distance edge between all tertiary and quaternary carbons </td></tr><tr>
 * <td>MDEC-44</td><td> molecular distance edge between all quaternary carbons </td></tr><tr>
 * <p/>
 * <td>MDEO-11</td><td> molecular distance edge between all primary oxygens </td></tr><tr>
 * <td>MDEO-12</td><td> molecular distance edge between all primary and secondary oxygens </td></tr><tr>
 * <td>MDEO-22</td><td> molecular distance edge between all secondary oxygens </td></tr><tr>
 * <td>MDEN-11</td><td> molecular distance edge between all primary nitrogens</td></tr><tr>
 * <p/>
 * <td>MDEN-12</td><td> molecular distance edge between all primary and secondary nitrogens </td></tr><tr>
 * <td>MDEN-13</td><td> molecular distance edge between all primary and tertiary niroqens </td></tr><tr>
 * <td>MDEN-22</td><td> molecular distance edge between all secondary nitroqens </td></tr><tr>
 * <td>MDEN-23</td><td> molecular distance edge between all secondary and tertiary nitrogens </td></tr><tr>
 * <p/>
 * <td>MDEN-33</td><td> molecular distance edge between all tertiary nitrogens</td></tr>
 * </table>
 * </center>
 * <p/>
 *
 * @author Rajarshi Guha
 * @cdk.created 2006-09-18
 * @cdk.module qsarmolecular
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:mde
 */
public class MDEDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private static final String[] NAMES  = {"MDEC-11", "MDEC-12", "MDEC-13", "MDEC-14", "MDEC-22", "MDEC-23",
            "MDEC-24", "MDEC-33", "MDEC-34", "MDEC-44", "MDEO-11", "MDEO-12", "MDEO-22", "MDEN-11", "MDEN-12",
            "MDEN-13", "MDEN-22", "MDEN-23", "MDEN-33"};

    public static final int       MDEC11 = 0;
    public static final int       MDEC12 = 1;
    public static final int       MDEC13 = 2;
    public static final int       MDEC14 = 3;
    public static final int       MDEC22 = 4;
    public static final int       MDEC23 = 5;
    public static final int       MDEC24 = 6;
    public static final int       MDEC33 = 7;
    public static final int       MDEC34 = 8;
    public static final int       MDEC44 = 9;

    public static final int       MDEO11 = 10;
    public static final int       MDEO12 = 11;
    public static final int       MDEO22 = 12;

    public static final int       MDEN11 = 13;
    public static final int       MDEN12 = 14;
    public static final int       MDEN13 = 15;
    public static final int       MDEN22 = 16;
    public static final int       MDEN23 = 17;
    public static final int       MDEN33 = 18;

    private static final int      C_1    = 1;
    private static final int      C_2    = 2;
    private static final int      C_3    = 3;
    private static final int      C_4    = 4;

    private static final int      O_1    = 1;
    private static final int      O_2    = 2;

    private static final int      N_1    = 1;
    private static final int      N_2    = 2;
    private static final int      N_3    = 3;

    public MDEDescriptor() {

    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor is implemented by this class.
     * <p/>
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     * this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#mde",
                this.getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * Sets the parameters attribute of the WeightDescriptor object.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          if more than 1 parameter is specified or if the parameter
     *          is not of type String
     * @see #getParameters
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        // none
    }

    /**
     * Gets the parameters attribute of the WeightDescriptor object.
     *
     * @return The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     * Calculate the weight of specified element type in the supplied {@link org.openscience.cdk.interfaces.IAtomContainer}.
     *
     * @param container The AtomContainer for which this descriptor is to be calculated. If 'H'
     *                  is specified as the element symbol make sure that the AtomContainer has hydrogens.
     * @return The total weight of atoms of the specified element type
     */

    @Override
    public DescriptorValue calculate(IAtomContainer container) {

        IAtomContainer local = AtomContainerManipulator.removeHydrogens(container);

        DoubleArrayResult retval = new DoubleArrayResult(19);
        for (int i = 0; i < 19; i++) {
            retval.add(dedge(local, i));
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval,
                getDescriptorNames());
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
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(19);
    }

    private double dedge(IAtomContainer atomContainer, int which) {
        int[][] adjMatrix = AdjacencyMatrix.getMatrix(atomContainer);
        int[][] tdist = PathTools.computeFloydAPSP(adjMatrix);

        int[][] atypes = null;

        switch (which) {
            case MDEC11:
            case MDEC12:
            case MDEC13:
            case MDEC14:
            case MDEC22:
            case MDEC23:
            case MDEC24:
            case MDEC33:
            case MDEC34:
            case MDEC44:
                atypes = evalATable(atomContainer, 6);
                break;
            case MDEO11:
            case MDEO12:
            case MDEO22:
                atypes = evalATable(atomContainer, 8);
                break;
            case MDEN11:
            case MDEN12:
            case MDEN13:
            case MDEN22:
            case MDEN23:
            case MDEN33:
                atypes = evalATable(atomContainer, 7);
                break;
        }
        double retval = 0;
        switch (which) {
            case MDEC11:
                retval = evalCValue(tdist, atypes, C_1, C_1);
                break;
            case MDEC12:
                retval = evalCValue(tdist, atypes, C_1, C_2);
                break;
            case MDEC13:
                retval = evalCValue(tdist, atypes, C_1, C_3);
                break;
            case MDEC14:
                retval = evalCValue(tdist, atypes, C_1, C_4);
                break;
            case MDEC22:
                retval = evalCValue(tdist, atypes, C_2, C_2);
                break;
            case MDEC23:
                retval = evalCValue(tdist, atypes, C_2, C_3);
                break;
            case MDEC24:
                retval = evalCValue(tdist, atypes, C_2, C_4);
                break;
            case MDEC33:
                retval = evalCValue(tdist, atypes, C_3, C_3);
                break;
            case MDEC34:
                retval = evalCValue(tdist, atypes, C_3, C_4);
                break;
            case MDEC44:
                retval = evalCValue(tdist, atypes, C_4, C_4);
                break;

            case MDEO11:
                retval = evalCValue(tdist, atypes, O_1, O_1);
                break;
            case MDEO12:
                retval = evalCValue(tdist, atypes, O_1, O_2);
                break;
            case MDEO22:
                retval = evalCValue(tdist, atypes, O_2, O_2);
                break;

            case MDEN11:
                retval = evalCValue(tdist, atypes, N_1, N_1);
                break;
            case MDEN12:
                retval = evalCValue(tdist, atypes, N_1, N_2);
                break;
            case MDEN13:
                retval = evalCValue(tdist, atypes, N_1, N_3);
                break;
            case MDEN22:
                retval = evalCValue(tdist, atypes, N_2, N_2);
                break;
            case MDEN23:
                retval = evalCValue(tdist, atypes, N_2, N_3);
                break;
            case MDEN33:
                retval = evalCValue(tdist, atypes, N_3, N_3);
                break;
        }

        return retval;
    }

    private int[][] evalATable(IAtomContainer atomContainer, int atomicNum) {
        //IAtom[] atoms = atomContainer.getAtoms();
        int natom = atomContainer.getAtomCount();
        int[][] atypes = new int[natom][2];
        for (int i = 0; i < natom; i++) {
            IAtom atom = atomContainer.getAtom(i);
            int numConnectedBonds = atomContainer.getConnectedBondsCount(atom);
            atypes[i][1] = i;
            if (atomicNum == (atom.getAtomicNumber() == null ? 0 : atom.getAtomicNumber()))
                atypes[i][0] = numConnectedBonds;
            else
                atypes[i][0] = -1;
        }
        return atypes;
    }

    private double evalCValue(int[][] distmat, int[][] codemat, int type1, int type2) {
        double lambda = 1;
        double n = 0;

        List<Integer> v1 = new ArrayList<Integer>();
        List<Integer> v2 = new ArrayList<Integer>();
        for (int i = 0; i < codemat.length; i++) {
            if (codemat[i][0] == type1) v1.add(codemat[i][1]);
            if (codemat[i][0] == type2) v2.add(codemat[i][1]);
        }

        for (int i = 0; i < v1.size(); i++) {
            for (int j = 0; j < v2.size(); j++) {
                int a = v1.get(i);
                int b = v2.get(j);
                if (a == b) continue;
                double distance = distmat[a][b];
                lambda = lambda * distance;
                n++;
            }
        }

        if (type1 == type2) {
            lambda = Math.sqrt(lambda);
            n = n / 2;
        }
        if (n == 0)
            return 0.0;
        else
            return n / Math.pow(Math.pow(lambda, 1.0 / (2.0 * n)), 2);
    }

    /**
     * Gets the parameterNames attribute of the WeightDescriptor object.
     *
     * @return The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        return null;
    }

    /**
     * Gets the parameterType attribute of the WeightDescriptor object.
     *
     * @param name Description of the Parameter
     * @return An Object whose class is that of the parameter requested
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }
}
