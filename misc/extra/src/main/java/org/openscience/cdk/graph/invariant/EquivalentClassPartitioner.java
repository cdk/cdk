/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.graph.invariant;

import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * An algorithm for topological symmetry. This algorithm derived from the
 * algorithm {@cdk.cite Hu94}.
 *
 * @cdk.githash
 *
 * @author Junfeng Hao
 * @author Luis F. de Figueiredo
 * @cdk.created 2003-09-24
 * @cdk.dictref blue-obelisk:perceiveGraphSymmetry
 * @cdk.module extra
 */
public class EquivalentClassPartitioner {

    private double[][]          nodeMatrix;
    private double[][]          bondMatrix;
    private double[]            weight;
    private double[][]          adjaMatrix;
    private int[][]             apspMatrix;
    private int                 layerNumber;
    private int                 nodeNumber;
    private static double       LOST   = 0.000000000001;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(EquivalentClassPartitioner.class);

    /**
     * Constructor for the TopologicalEquivalentClass object.
     */
    public EquivalentClassPartitioner() {}

    /**
     * Constructor for the TopologicalEquivalentClass object.
     */
    public EquivalentClassPartitioner(IAtomContainer atomContainer) {
        adjaMatrix = ConnectionMatrix.getMatrix(atomContainer);
        apspMatrix = PathTools.computeFloydAPSP(adjaMatrix);
        layerNumber = 1;
        nodeNumber = atomContainer.getAtomCount();

        for (int i = 1; i < atomContainer.getAtomCount(); i++) {
            for (int j = 0; j < i; j++) {
                // define the number of layer equal to the longest path obtained
                // by calculating the all-pair-shortest path
                if (apspMatrix[i][j] > layerNumber) {
                    layerNumber = apspMatrix[i][j];
                }
                // correct adjacency matrix to consider aromatic bonds as such
                if (adjaMatrix[i][j] > 0) {
                    IBond bond = atomContainer.getBond(atomContainer.getAtom(i), atomContainer.getAtom(j));
                    boolean isArom = bond.getFlag(CDKConstants.ISAROMATIC);
                    adjaMatrix[i][j] = (isArom) ? 1.5 : adjaMatrix[i][j];
                    adjaMatrix[j][i] = adjaMatrix[i][j];
                }
            }

        }
        nodeMatrix = new double[nodeNumber][layerNumber + 1];
        bondMatrix = new double[nodeNumber][layerNumber];
        weight = new double[nodeNumber + 1];
    }

    /**
     * Get the topological equivalent class of the molecule.
     *
     * @param atomContainer
     *            atoms and bonds of the molecule
     * @return an array contains the automorphism partition of the molecule
     */
    public int[] getTopoEquivClassbyHuXu(IAtomContainer atomContainer) throws NoSuchAtomException {
        double nodeSequence[] = prepareNode(atomContainer);
        nodeMatrix = buildNodeMatrix(nodeSequence);
        bondMatrix = buildBondMatrix();
        weight = buildWeightMatrix(nodeMatrix, bondMatrix);
        return findTopoEquivClass(weight);
    }

    /**
     * Prepare the node identifier. The purpose of this is to increase the
     * differentiation of the nodes. Detailed information please see the
     * corresponding literature.
     *
     * @param atomContainer atoms and bonds of the molecule
     * @return an array of node identifier
     */
    public double[] prepareNode(IAtomContainer atomContainer) {
        double nodeSequence[] = new double[atomContainer.getAtomCount()];
        int i = 0;
        for (IAtom atom : atomContainer.atoms()) {
            String symbol = atom.getSymbol();

            List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
            if (bonds.size() == 1) {
                IBond bond0 = bonds.get(0);
                IBond.Order order = bond0.getOrder();
                if (symbol.equals("C")) {
                    if (order == IBond.Order.SINGLE)
                        nodeSequence[i] = 1;// CH3-
                    else if (order == IBond.Order.DOUBLE)
                        nodeSequence[i] = 3;// CH2=
                    else if (order == IBond.Order.TRIPLE) nodeSequence[i] = 6;// CH#
                } else if (symbol.equals("O")) {
                    if (order == IBond.Order.SINGLE)
                        nodeSequence[i] = 14;// HO-
                    else if (order == IBond.Order.DOUBLE) nodeSequence[i] = 16;// O=
                    // missing the case of an aromatic double bond
                } else if (symbol.equals("N")) {
                    if (order == IBond.Order.SINGLE)
                        nodeSequence[i] = 18;// NH2-
                    else if (order == IBond.Order.DOUBLE) {
                        if (atom.getCharge() == -1.0)
                            nodeSequence[i] = 27;// N= contains -1 charge
                        else
                            nodeSequence[i] = 20;// NH=
                    } else if (order == IBond.Order.TRIPLE) nodeSequence[i] = 23;// N#
                } else if (symbol.equals("S")) {
                    if (order == IBond.Order.SINGLE)
                        nodeSequence[i] = 31;// HS-
                    else if (order == IBond.Order.DOUBLE) nodeSequence[i] = 33;// S=
                } else if (symbol.equals("P"))
                    nodeSequence[i] = 38;// PH2-
                else if (symbol.equals("F"))
                    nodeSequence[i] = 42;// F-
                else if (symbol.equals("Cl"))
                    nodeSequence[i] = 43;// Cl-
                else if (symbol.equals("Br"))
                    nodeSequence[i] = 44;// Br-
                else if (symbol.equals("I"))
                    nodeSequence[i] = 45;// I-
                else {
                    logger.debug("in case of a new node, please " + "report this bug to cdk-devel@lists.sf.net.");
                }
            } else if (bonds.size() == 2) {
                IBond bond0 = (IBond) bonds.get(0);
                IBond bond1 = (IBond) bonds.get(1);
                IBond.Order order0 = bond0.getOrder();
                IBond.Order order1 = bond1.getOrder();
                if (symbol.equals("C")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE)
                        nodeSequence[i] = 2;// -CH2-
                    else if (order0 == IBond.Order.DOUBLE && order1 == IBond.Order.DOUBLE)
                        nodeSequence[i] = 10;// =C=
                    else if ((order0 == IBond.Order.SINGLE || bond1.getOrder() == IBond.Order.SINGLE)
                            && (order0 == IBond.Order.DOUBLE || bond1.getOrder() == IBond.Order.DOUBLE))
                        nodeSequence[i] = 5;// -CH=
                    else if ((order0 == IBond.Order.SINGLE || bond1.getOrder() == IBond.Order.TRIPLE)
                            && (order0 == IBond.Order.TRIPLE || bond1.getOrder() == IBond.Order.TRIPLE))
                        nodeSequence[i] = 9;// -C#
                    // case 3 would not allow to reach this statement as there
                    // is no aromatic bond order
                    if (bond0.getFlag(CDKConstants.ISAROMATIC) && bond1.getFlag(CDKConstants.ISAROMATIC))
                        nodeSequence[i] = 11;// ArCH
                } else if (symbol.equals("N")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE)
                        nodeSequence[i] = 19;// -NH-
                    else if (order0 == IBond.Order.DOUBLE && order1 == IBond.Order.DOUBLE)
                        nodeSequence[i] = 28;// =N= with charge=-1
                    else if ((order0 == IBond.Order.SINGLE || bond1.getOrder() == IBond.Order.SINGLE)
                            && (order0 == IBond.Order.DOUBLE || bond1.getOrder() == IBond.Order.DOUBLE))
                        nodeSequence[i] = 22;// -N=
                    else if ((order0 == IBond.Order.DOUBLE || bond1.getOrder() == IBond.Order.DOUBLE)
                            && (order0 == IBond.Order.TRIPLE || bond1.getOrder() == IBond.Order.TRIPLE))
                        nodeSequence[i] = 26;// =N#
                    else if ((order0 == IBond.Order.SINGLE || bond1.getOrder() == IBond.Order.SINGLE)
                            && (order0 == IBond.Order.TRIPLE || bond1.getOrder() == IBond.Order.TRIPLE))
                        nodeSequence[i] = 29;// -N# with charge=+1
                    // case 3 would not allow to reach this statement as there
                    // is no aromatic bond order
                    if (bond0.getFlag(CDKConstants.ISAROMATIC) && bond1.getFlag(CDKConstants.ISAROMATIC))
                        nodeSequence[i] = 30;// ArN
                    // there is no way to distinguish between ArNH and ArN as
                    // bonds to protons are not considered
                } else if (symbol.equals("O")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE)
                        nodeSequence[i] = 15;// -O-
                    else if (bond0.getFlag(CDKConstants.ISAROMATIC) && bond1.getFlag(CDKConstants.ISAROMATIC))
                        nodeSequence[i] = 17;// ArO
                } else if (symbol.equals("S")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE)
                        nodeSequence[i] = 32;// -S-
                    else if (order0 == IBond.Order.DOUBLE && order1 == IBond.Order.DOUBLE)
                        nodeSequence[i] = 35;// =S=
                    else if (bond0.getFlag(CDKConstants.ISAROMATIC) && bond1.getFlag(CDKConstants.ISAROMATIC))
                        nodeSequence[i] = 37;// ArS
                } else if (symbol.equals("P")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE) nodeSequence[i] = 39;// -PH-
                } else {
                    logger.debug("in case of a new node, " + "please report this bug to cdk-devel@lists.sf.net.");
                }
            } else if (bonds.size() == 3) {
                IBond bond0 = (IBond) bonds.get(0);
                IBond bond1 = (IBond) bonds.get(1);
                IBond bond2 = (IBond) bonds.get(2);
                IBond.Order order0 = bond0.getOrder();
                IBond.Order order1 = bond1.getOrder();
                IBond.Order order2 = bond2.getOrder();

                if (symbol.equals("C")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE && order2 == IBond.Order.SINGLE)
                        nodeSequence[i] = 4;// >C-
                    else if (order0 == IBond.Order.DOUBLE || order1 == IBond.Order.DOUBLE
                            || order2 == IBond.Order.DOUBLE) nodeSequence[i] = 8;// >C=
                    // case 2 would not allow to reach this statement because
                    // there is always a double bond (pi system) around an
                    // aromatic atom
                    if ((bond0.getFlag(CDKConstants.ISAROMATIC) || bond1.getFlag(CDKConstants.ISAROMATIC) || bond2
                            .getFlag(CDKConstants.ISAROMATIC))
                            && (order0 == IBond.Order.SINGLE || order1 == IBond.Order.SINGLE || bond2.getOrder() == IBond.Order.SINGLE))
                        nodeSequence[i] = 12;// ArC-
                    // case 3 would not allow to reach this statement
                    if (bond0.getFlag(CDKConstants.ISAROMATIC) && bond1.getFlag(CDKConstants.ISAROMATIC)
                            && bond2.getFlag(CDKConstants.ISAROMATIC)) nodeSequence[i] = 13;// ArC
                } else if (symbol.equals("N")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE && order2 == IBond.Order.SINGLE)
                        nodeSequence[i] = 21;// >N-
                    else if (order0 == IBond.Order.SINGLE || order1 == IBond.Order.SINGLE
                            || order2 == IBond.Order.SINGLE) nodeSequence[i] = 25;// -N(=)=
                } else if (symbol.equals("S")) {
                    if (order0 == IBond.Order.DOUBLE || order1 == IBond.Order.DOUBLE || order2 == IBond.Order.DOUBLE)
                        nodeSequence[i] = 34;// >S=
                } else if (symbol.equals("P")) {
                    if (order0 == IBond.Order.SINGLE && order1 == IBond.Order.SINGLE && order2 == IBond.Order.SINGLE)
                        nodeSequence[i] = 40;// >P-
                } else {
                    logger.debug("in case of a new node, " + "please report this bug to cdk-devel@lists.sf.net.");
                }
            } else if (bonds.size() == 4) {
                if (atom.getSymbol().equals("C"))
                    nodeSequence[i] = 7;// >C<
                else if (atom.getSymbol().equals("N"))
                    nodeSequence[i] = 24;// >N(=)-
                else if (atom.getSymbol().equals("S"))
                    nodeSequence[i] = 36;// >S(=)=
                else if (atom.getSymbol().equals("P"))
                    nodeSequence[i] = 41;// =P<-
                else {
                    logger.debug("in case of a new node, " + "please report this bug to cdk-devel@lists.sf.net.");
                }
            }
            i++;
        }
        return nodeSequence;
    }

    /**
     * Build node Matrix.
     *
     * @param nodeSequence an array contains node number for each atom
     * @return node Matrix
     */
    public double[][] buildNodeMatrix(double[] nodeSequence) {
        int i, j, k;
        for (i = 0; i < nodeNumber; i++) {
            nodeMatrix[i][0] = nodeSequence[i];
            for (j = 1; j <= layerNumber; j++) {
                nodeMatrix[i][j] = 0.0;
                for (k = 0; k < nodeNumber; k++) {
                    if (apspMatrix[i][k] == j) {
                        nodeMatrix[i][j] += nodeSequence[k];
                    }
                }
            }
        }
        return nodeMatrix;
    }

    /**
     * Build trial node Matrix.
     *
     * @param weight an array contains the weight of atom
     * @return trial node matrix.
     */
    public double[][] buildTrialNodeMatrix(double[] weight) {
        int i, j, k;
        for (i = 0; i < nodeNumber; i++) {
            nodeMatrix[i][0] = weight[i + 1];
            for (j = 1; j <= layerNumber; j++) {
                nodeMatrix[i][j] = 0.0;
                for (k = 0; k < nodeNumber; k++) {
                    if (apspMatrix[i][k] == j) {
                        nodeMatrix[i][j] += weight[k + 1];
                    }
                }
            }
        }
        return nodeMatrix;
    }

    /**
     * Build bond matrix.
     *
     * @return bond matrix.
     */
    public double[][] buildBondMatrix() {
        int i, j, k, m;
        for (i = 0; i < nodeNumber; i++) {
            for (j = 1; j <= layerNumber; j++) {
                bondMatrix[i][j - 1] = 0.0;
                for (k = 0; k < nodeNumber; k++) {
                    if (j == 1) {
                        if (apspMatrix[i][k] == j) {
                            bondMatrix[i][j - 1] += adjaMatrix[i][k];
                        }
                    } else {
                        if (apspMatrix[i][k] == j) {
                            for (m = 0; m < nodeNumber; m++) {
                                if (apspMatrix[i][m] == (j - 1)) {
                                    bondMatrix[i][j - 1] += adjaMatrix[k][m];
                                }
                            }
                        }
                    }
                }
            }
        }
        return bondMatrix;
    }

    /**
     * Build weight array for the given node matrix and bond matrix.
     *
     * @param nodeMatrix array contains node information
     * @param bondMatrix array contains bond information
     * @return weight array for the node
     */
    public double[] buildWeightMatrix(double[][] nodeMatrix, double[][] bondMatrix) {
        for (int i = 0; i < nodeNumber; i++) {
            weight[i + 1] = nodeMatrix[i][0];
            for (int j = 0; j < layerNumber; j++) {
                weight[i + 1] += nodeMatrix[i][j + 1] * bondMatrix[i][j] * Math.pow(10.0, (double) -(j + 1));
            }
        }
        weight[0] = 0.0;
        return weight;
    }

    /**
     * Get different number of the given number.
     *
     * @param weight array contains weight of the nodes
     * @return number of different weight
     */
    public int checkDiffNumber(double[] weight) {
        // Count the number of different weight
        double category[] = new double[weight.length];
        int i, j;
        int count = 1;
        double t;
        category[1] = weight[1];
        for (i = 2; i < weight.length; i++) {
            for (j = 1; j <= count; j++) {
                t = weight[i] - category[j];
                if (t < 0.0) t = -t;
                if (t < LOST) break;
            }
            if (j > count) {
                count += 1;
                category[count] = weight[i];
            }
        }
        return count;
    }

    /**
     * Get the final equivalent class.
     *
     * @param weight array contains weight of the nodes
     * @return an array contains the automorphism partition
     */
    public int[] getEquivalentClass(double[] weight) {
        double category[] = new double[weight.length];
        int equivalentClass[] = new int[weight.length];
        int i, j;
        int count = 1;
        double t;
        category[1] = weight[1];
        for (i = 2; i < weight.length; i++) {
            for (j = 1; j <= count; j++) {
                t = weight[i] - category[j];
                if (t < 0.0) {
                    t = -t;
                }
                if (t < LOST) {
                    break;
                }
            }
            if (j > count) {
                count += 1;
                category[count] = weight[i];
            }
        }

        for (i = 1; i < weight.length; i++) {
            for (j = 1; j <= count; j++) {
                t = weight[i] - category[j];
                if (t < 0.0) {
                    t = -t;
                }
                if (t < LOST) {
                    equivalentClass[i] = j;
                }
            }
        }
        equivalentClass[0] = count;
        return equivalentClass;
    }

    /**
     * Find the topological equivalent class for the given weight.
     *
     * @param weight array contains weight of the nodes
     * @return an array contains the automorphism partition
     */
    public int[] findTopoEquivClass(double[] weight) {
        int trialCount, i;
        int equivalentClass[] = new int[weight.length];
        int count = checkDiffNumber(weight);
        trialCount = count;
        if (count == nodeNumber) {
            for (i = 1; i <= nodeNumber; i++) {
                equivalentClass[i] = i;
            }
            equivalentClass[0] = count;
            return equivalentClass;
        }
        do {
            count = trialCount;
            double[][] trialNodeMatrix = buildTrialNodeMatrix(weight);
            double[] trialWeight = buildWeightMatrix(trialNodeMatrix, bondMatrix);
            trialCount = checkDiffNumber(trialWeight);
            if (trialCount == nodeNumber) {
                for (i = 1; i <= nodeNumber; i++) {
                    equivalentClass[i] = i;
                }
                equivalentClass[0] = count;
                return equivalentClass;
            }
            if (trialCount <= count) {
                equivalentClass = getEquivalentClass(weight);
                return equivalentClass;
            }
        } while (trialCount > count);
        return equivalentClass;
    }
}
