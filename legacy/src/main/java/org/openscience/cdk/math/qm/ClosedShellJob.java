/* Copyright (C) 2001-2007  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 *
 * Contact: cdk-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.math.qm;

import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.Vector;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Calculates the orbitals and orbital energies of electron systems
 * with closed shells
 *
 * @author Stephan Michels &lt;stephan@vern.chem.tu-berlin.de&gt;
 * @cdk.githash
 * @cdk.created 2001-06-14
 * @cdk.module  qm
 */
public class ClosedShellJob {

    private Orbitals            orbitals;
    private Vector              E;

    private static ILoggingTool log        = LoggingToolFactory.createLoggingTool(ClosedShellJob.class);

    private int                 iterations = 0;

    public ClosedShellJob(Orbitals orbitals) {
        this.orbitals = orbitals;
    }

    public Vector getEnergies() {
        return E.duplicate();
    }

    /**
     * Sorts the orbitals by their energies
     */
    private void sort(Matrix matrixC, Vector E) {
        int i, j;
        double value;
        boolean changed;
        do {
            changed = false;
            for (i = 1; i < E.size; i++)
                if (E.vector[i - 1] > E.vector[i]) {
                    value = E.vector[i];
                    E.vector[i] = E.vector[i - 1];
                    E.vector[i - 1] = value;

                    for (j = 0; j < matrixC.rows; j++) {
                        value = matrixC.matrix[j][i];
                        matrixC.matrix[j][i] = matrixC.matrix[j][i - 1];
                        matrixC.matrix[j][i - 1] = value;
                    }
                    changed = true;
                }
        } while (changed);
    }

    private Matrix calculateS(IBasis basis) {
        int size = basis.getSize();
        Matrix matrixS = new Matrix(size, size);
        int i, j;
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                matrixS.matrix[i][j] = basis.calcS(i, j);

        return matrixS;
    }

    /**
     * Calculates the matrix for the kinetic energy
     *
     * T_i,j = (1/2) * -<d^2/dx^2 chi_i | chi_j>
     */
    private Matrix calculateT(IBasis basis) {
        int size = basis.getSize();
        Matrix matrixJ = new Matrix(size, size);
        int i, j;
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                // (1/2) * -<d^2/dx^2 chi_i | chi_j>
                matrixJ.matrix[i][j] = basis.calcJ(j, i) / 2; // Vorsicht indizies sind vertauscht

        return matrixJ;
    }

    /**
     * Calculates the matrix for the potential matrix
     *
     * V_i,j = <chi_i | 1/r | chi_j>
     */
    private Matrix calculateV(IBasis basis) {
        int size = basis.getSize();
        Matrix matrixV = new Matrix(size, size);
        int i, j;
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                matrixV.matrix[i][j] = basis.calcV(i, j);

        return matrixV;
    }

    /**
     * Calculates thes values for the 2 electron interactions
     */
    private double[][][][] calculateI(IBasis basis) {
        int i, j, k, l;
        int size = basis.getSize();

        double[][][][] result = new double[size][][][];
        for (i = 0; i < size; i++) {
            result[i] = new double[i + 1][][];
            for (j = 0; j <= i; j++) {
                result[i][j] = new double[size][];
                for (k = 0; k < size; k++) {
                    result[i][j][k] = new double[k + 1];
                    for (l = 0; l <= k; l++) {
                        result[i][j][k][l] = basis.calcI(i, j, k, l);
                        //log.println("("+(i+1)+" "+(j+1)+"|"+(k+1)+" "+(l+1)+")="+result[i][j][k][l]);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Calculates the density matrix
     */
    private Matrix calculateD(IBasis basis, Matrix matrixC, int count_electrons) {
        int i, j, k;
        int size = basis.getSize();
        int orbitals = matrixC.getColumns();
        int occ = count_electrons / 2;
        int locc = count_electrons % 2;
        Matrix matrixD = new Matrix(size, size);
        log.debug("D:occ=" + occ + " locc=" + locc);

        //    if (locc!=0)
        //      logger.debug("This class work only correct for closed shells");

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                matrixD.matrix[i][j] = 0d;
                for (k = 0; (k < orbitals) && (k < occ); k++)
                    matrixD.matrix[i][j] += 2d * matrixC.matrix[i][k] * matrixC.matrix[j][k];

                if ((locc == 1) && (k + 1 < orbitals)) matrixD.matrix[i][j] += matrixC.matrix[i][k + 1] * matrixC.matrix[j][k + 1];
            }
        return matrixD;
    }

    private Matrix calculateJ(IBasis basis, double[][][][] I, Matrix matrixD) {
        int i, j, k, l;
        int size = basis.getSize();
        Matrix matrixJ = new Matrix(size, size);
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                matrixJ.matrix[i][j] = 0;
                for (k = 0; k < size; k++)
                    for (l = 0; l < size; l++) {
                        if (i >= j) {
                            if (k >= l)
                                matrixJ.matrix[i][j] += matrixD.matrix[k][l] * I[i][j][k][l];
                            else
                                matrixJ.matrix[i][j] += matrixD.matrix[k][l] * I[i][j][l][k];
                        } else {
                            if (k >= l)
                                matrixJ.matrix[i][j] += matrixD.matrix[k][l] * I[j][i][k][l];
                            else
                                matrixJ.matrix[i][j] += matrixD.matrix[k][l] * I[j][i][l][k];
                        }
                    }
                matrixJ.matrix[i][j] *= 2d;
            }
        return matrixJ;
    }

    private Matrix calculateK(IBasis basis, double[][][][] I, Matrix matrixD) {
        int i, j, k, l;
        int size = basis.getSize();
        Matrix matrixK = new Matrix(size, size);
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                matrixK.matrix[i][j] = 0;
                for (k = 0; k < size; k++)
                    for (l = 0; l < size; l++) {
                        if (i >= j) {
                            if (k >= l)
                                matrixK.matrix[i][j] += matrixD.matrix[k][l] * I[i][j][k][l];
                            else
                                matrixK.matrix[i][j] += matrixD.matrix[k][l] * I[i][j][l][k];
                        } else {
                            if (k >= l)
                                matrixK.matrix[i][j] += matrixD.matrix[k][l] * I[j][i][k][l];
                            else
                                matrixK.matrix[i][j] += matrixD.matrix[k][l] * I[j][i][l][k];
                        }
                    }
            }
        return matrixK;
    }

    private double contraction(Matrix matrixA, Matrix matrixB) {
        int i, j;
        double result = 0;
        for (i = 0; i < matrixA.rows; i++)
            for (j = 0; j < matrixA.columns; j++)
                result += matrixA.matrix[i][j] * matrixB.matrix[i][j];
        return result;
    }

    public Orbitals calculate() {
        long time = System.currentTimeMillis();

        Matrix matrixC, matricS, matrixT, matrixV, HAO, matrixH, matrixD, matrixJ, matrixK, matrixF, matrixU;
        double[][][][] matrixI;
        double energy;
        IBasis basis = orbitals.getBasis();

        int countElectrons = orbitals.getCountElectrons();

        matrixC = orbitals.getCoefficients().duplicate();

        matricS = calculateS(basis);

        log.debug("S = \n" + matricS + "\n");

        log.debug("C = \n" + matrixC + "\n");

        matrixC = matrixC.orthonormalize(matricS);
        log.debug("C' = \n" + matrixC + "\n");
        log.debug("C't * S * C' = \n" + matricS.similar(matrixC) + "\n");

        matrixT = calculateT(basis);
        log.debug("T = \n" + matrixT + "\n");

        matrixV = calculateV(basis);
        log.debug("V = \n" + matrixV + "\n");

        HAO = matrixT.add(matrixV);
        log.debug("HAO = \n" + HAO + "\n");

        matrixH = HAO.similar(matrixC);
        log.debug("H = C't * HAO * C' = \n" + matrixH.similar(matrixC) + "\n");

        matrixU = matrixH.diagonalize(50);
        E = matrixH.similar(matrixU).getVectorFromDiagonal();
        matrixC = matrixC.mul(matrixU);
        sort(matrixC, E);
        log.debug("C(neu) = \n" + matrixC + "\n");
        log.debug("E = \n" + E + "\n");

        for (int j = 0; j < E.size; j++)
            log.debug("E(" + (j + 1) + ".Orbital)=" + (E.vector[j] * 27.211) + " eV");

        time = System.currentTimeMillis() - time;
        log.debug("Time = " + time + " ms");
        time = System.currentTimeMillis();

        if (iterations > 0)
            matrixI = calculateI(basis);
        else
            matrixI = null;

        for (int i = 0; i < iterations; i++) {
            log.debug((i + 1) + ".Durchlauf\n");

            time = System.currentTimeMillis();

            log.debug("C't * S * C' = \n" + matricS.similar(matrixC) + "\n");

            log.debug("count of electrons = " + countElectrons + "\n");

            matrixD = calculateD(basis, matrixC, countElectrons);
            log.debug("D = \n" + matrixD + "\n");

            //log.println("2*contraction(D*S) = "+(D.mul(S)).contraction()*2+"\n");
            log.debug("2*contraction(D*S) = " + contraction(matrixD, matricS) * 2 + "\n");

            //J = calculateJ(basis, D);
            matrixJ = calculateJ(basis, matrixI, matrixD);
            log.debug("J = \n" + matrixJ + "\n");

            //K = calculateK(basis, D);
            matrixK = calculateK(basis, matrixI, matrixD);
            log.debug("K = \n" + matrixK + "\n");

            matrixF = HAO.add(matrixJ).sub(matrixK);
            log.debug("F = H+J-K = \n" + matrixF + "\n");

            matrixH = matrixF.similar(matrixC);
            log.debug("H = C't * F * C' = \n" + matrixH + "\n");

            matrixU = matrixH.diagonalize(50);
            E = matrixH.similar(matrixU).getVectorFromDiagonal();
            matrixC = matrixC.mul(matrixU);
            sort(matrixC, E);
            log.debug("C(neu) = \n" + matrixC + "\n");
            log.debug("E = \n" + E + "\n");

            for (int j = 0; j < E.size; j++)
                log.debug("E(" + (j + 1) + ".Orbital)=" + (E.vector[j] * 27.211) + " eV");

            energy = contraction(matrixD, HAO.add(matrixF));
            log.debug("Gesamtenergie = " + energy + " (" + energy * 27.211 + " eV)\n");

            time = System.currentTimeMillis() - time;
            log.debug("Time = " + time + " ms");

            System.gc();
        }

        return new Orbitals(basis, matrixC);
    }
}
