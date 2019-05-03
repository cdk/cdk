/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.layout;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;

/**
 * Measure and update a score of congestion in a molecule layout 
 * {@cdk.cite HEL99}, {@cdk.cite Clark06}. This can be tuned in
 * several ways but currently uses a basic '1/(dist^2)'.
 */
final class Congestion {

    // lower bound on scores
    private static final double MIN_SCORE = 0.00001;

    double[][] contribution;
    double     score;
    IAtom[]    atoms;

    Congestion(IAtomContainer mol, int[][] adjList) {
        final int numAtoms = mol.getAtomCount();
        this.contribution = new double[numAtoms][numAtoms];
        this.atoms = AtomContainerManipulator.getAtomArray(mol);
        for (int v = 0; v < numAtoms; v++)
            for (int w : adjList[v])
                contribution[v][v] = contribution[v][w] = -1;
        this.score = initScore();
    }

    /**
     * Calculate the initial score.
     *
     * @return congestion score
     */
    private double initScore() {
        double score = 0;
        final int n = atoms.length;
        for (int i = 0; i < n; i++) {
            final Point2d p1 = atoms[i].getPoint2d();
            for (int j = i + 1; j < n; j++) {
                if (contribution[i][j] < 0) continue;
                final Point2d p2 = atoms[j].getPoint2d();
                final double x = p1.x - p2.x;
                final double y = p1.y - p2.y;
                final double len2 = x * x + y * y;
                score += contribution[j][i] = contribution[i][j] = 1 / Math.max(len2, MIN_SCORE);
            }
        }
        return score;
    }

    /**
     * Update the score considering that some atoms have moved. We only
     * need to update the score of atom that have moved vs those that haven't
     * since all those that moved did so together.
     * 
     * @param visit visit flags
     * @param vs visit list
     * @param n number of visited in visit list
     */
    void update(boolean[] visit, int[] vs, int n) {
        int len = atoms.length;
        double subtract = 0;
        for (int i = 0; i < n; i++) {
            final int v = vs[i];
            final Point2d p1 = atoms[v].getPoint2d();
            for (int w = 0; w < len; w++) {
                if (visit[w] || contribution[v][w] < 0) continue;
                subtract += contribution[v][w];
                final Point2d p2 = atoms[w].getPoint2d();
                final double  x    = p1.x - p2.x;
                final double  y    = p1.y - p2.y;
                final double  len2 = x * x + y * y;
                score += contribution[w][v] = contribution[v][w] = 1 / Math.max(len2, MIN_SCORE);
            }
        }
        score -= subtract;
    }

    /**
     * Update the score considering the atoms have moved (provided). 
     *
     * @param vs visit list
     * @param n number of visited in visit list
     */
    void update(int[] vs, int n) {
        int len = atoms.length;
        double subtract = 0;
        for (int i = 0; i < n; i++) {
            final int v = vs[i];
            final Point2d p1 = atoms[v].getPoint2d();
            for (int w = 0; w < len; w++) {
                if (contribution[v][w] < 0) continue;
                subtract += contribution[v][w];
                final Point2d p2   = atoms[w].getPoint2d();
                final double  x    = p1.x - p2.x;
                final double  y    = p1.y - p2.y;
                final double  len2 = x * x + y * y;
                score += contribution[w][v] = contribution[v][w] = 1 / Math.max(len2, MIN_SCORE);
            }
        }
        score -= subtract;
    }

    /**
     * The congestion score.
     *
     * @return the current score
     */
    double score() {
        return score;
    }

    /**
     * Access the contribution of an atom pair to the congestion.
     *
     * @param i atom idx
     * @param j atom idx
     * @return score
     */
    double contribution(int i, int j) {
        return contribution[i][j];
    }
}
