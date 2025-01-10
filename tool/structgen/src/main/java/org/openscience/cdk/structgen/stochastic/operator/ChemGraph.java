/* Copyright (C) 1997-2007  The CDK project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.cdk.structgen.stochastic.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.math.RandomNumbersTool;

/**
 * @cdk.module     structgen
 * @cdk.githash
 */
public class ChemGraph {

    /* Number of atoms in this structure */
    protected final int acount;
    /* Number of atoms needed to form subgraph */
    protected int           numAtoms;
    protected double[][]    contab;
    /* Number of atoms that have been traversed */
    protected int           travIndex;
    /* Flag: true if atom visited during a traversal */
    protected boolean[]     visited;
    /* Depth first traversal of the graph */
    protected List<Integer> subGraph;

    public ChemGraph(IAtomContainer chrom) {
        acount = chrom.getAtomCount();
        numAtoms = acount / 2;
        contab = new double[acount][acount];
        contab = ConnectionMatrix.getMatrix(chrom);
    }

    public List<Integer> pickDFgraph() {
        if (acount == 0) return Collections.emptyList();
        //depth first search from a randomly selected atom
        travIndex = 0;
        subGraph = new ArrayList<>();
        visited = new boolean[acount];
        for (int atom = 0; atom < acount; atom++)
            visited[atom] = false;
        int seedAtom = RandomNumbersTool.randomInt(0, acount - 1);
        recursiveDFT(seedAtom);

        return subGraph;
    }

    private void recursiveDFT(int atom) {
        if ((travIndex < numAtoms) && (!visited[atom])) {
            subGraph.add(atom);
            travIndex++;
            visited[atom] = true;

            //			for (int nextAtom = 0; nextAtom < dim; nextAtom++) //not generalized
            //				if (contab[atom][nextAtom] != 0) recursiveDFT(nextAtom);
            List<Integer> adjSet = new ArrayList<>();
            for (int nextAtom = 0; nextAtom < acount; nextAtom++) {
                if ((int) contab[atom][nextAtom] != 0) {
                    adjSet.add(nextAtom);
                }
            }
            while (adjSet.size() > 0) {
                int adjIndex = RandomNumbersTool.randomInt(0, adjSet.size() - 1);
                recursiveDFT((Integer) adjSet.get(adjIndex));
                adjSet.remove(adjIndex);
            }

        }
    }

    public List<Integer> pickBFgraph() {
        if (acount == 0) return Collections.emptyList();
        //breadth first search from a randomly selected atom

        travIndex = 0;
        subGraph = new ArrayList<>();
        visited = new boolean[acount];
        for (int atom = 0; atom < acount; atom++)
            visited[atom] = false;
        int seedAtom = RandomNumbersTool.randomInt(0, acount - 1);

        List<Integer> atomQueue = new ArrayList<>();
        atomQueue.add(seedAtom);
        visited[seedAtom] = true;

        while (!atomQueue.isEmpty() && (subGraph.size() < numAtoms)) {
            int foreAtom = (Integer) atomQueue.get(0);
            subGraph.add(foreAtom);
            atomQueue.remove(0);
            travIndex++;

            List<Integer> adjSet = new ArrayList<>();
            for (int nextAtom = 0; nextAtom < acount; nextAtom++) {
                if (((int) contab[foreAtom][nextAtom] != 0) && (!visited[nextAtom])) {
                    adjSet.add(nextAtom);
                }
            }
            while (adjSet.size() > 0) {
                int adjIndex = RandomNumbersTool.randomInt(0, adjSet.size() - 1);
                atomQueue.add(adjSet.get(adjIndex));
                visited[(Integer) adjSet.get(adjIndex)] = true;
                adjSet.remove(adjIndex);
            }

        }
        return subGraph;
    }

    public List<Integer> getSubgraph() {
        return subGraph;
    }

    public void setSubgraph(List<Integer> subgraph) {
        subGraph = subgraph;
    }

    public int getNumAtoms() {
        return numAtoms;
    }

    public void setNumAtoms(int numatoms) {
        numAtoms = numatoms;
    }
}
