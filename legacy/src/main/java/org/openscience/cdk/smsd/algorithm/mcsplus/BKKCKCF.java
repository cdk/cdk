/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * You should have received index copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * This class implements Bron-Kerbosch clique detection algorithm as it is
 * described in [F. Cazals, vertexOfCurrentClique. Karande: An Algorithm for reporting maximal c-cliques;
 * processedVertex.Comp. Sc. (2005); vol 349; pp.
 * 484-490]
 *
 *
 * BronKerboschCazalsKarandeKochCliqueFinder.java
 *
 * @cdk.githash
 * @cdk.module smsd
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class BKKCKCF {

    private List<List<Integer>> maxCliquesSet      = null;
    /***********************************************************************/
    private List<Integer>       cEdges             = null;
    private List<Integer>       dEdges             = null;
    private int                 bestCliqueSize     = 0;
    private List<Integer>       compGraphNodes     = null;
    private double              dEdgeIterationSize = 0;
    private double              cEdgeIterationSize = 0;

    /**
     * Creates index new instance of Bron Kerbosch Cazals Karande Koch Clique Finder
     * This class implements Bron-Kerbosch clique detection algorithm as it is
     * described in [F. Cazals, vertexOfCurrentClique. Karande: An Algorithm for reporting maximal c-cliques;
     * processedVertex.Comp. Sc. (2005); vol 349; pp.
     * 484-490]
     * @param compGraphNodesOrg
     * @param cEdgesOrg C-Edges set of allowed edges
     * @param dEdgesOrg D-Edges set of prohibited edges
     */
    protected BKKCKCF(List<Integer> compGraphNodesOrg, List<Integer> cEdgesOrg, List<Integer> dEdgesOrg) {
        MCSPlus.setTimeManager(new TimeManager());
        this.compGraphNodes = compGraphNodesOrg;
        this.cEdges = cEdgesOrg;
        this.dEdges = dEdgesOrg;
        bestCliqueSize = 0;
        //Orignal assignment as per paper
        dEdgeIterationSize = dEdges.size() / 2;

        //Orignal assignment as per paper
        cEdgeIterationSize = cEdges.size() / 2;

        //reset Degdes and Cedges if required
        //        setEdges();

        //Initialization maxCliquesSet

        maxCliquesSet = new ArrayList<List<Integer>>();

        init();

    }

    /*
     * Call the wrapper for ENUMERATE_CLIQUES
     */
    private void init() {

        /********************************************************************/
        /*
         * vertex: stored all the vertices for the Graph G vertex[G] nodes of
         * vector compGraphNodes are stored in vertex
         */
        List<Integer> vertex = new ArrayList<Integer>(); //Initialization of ArrayList vertex

        int vertexCount = compGraphNodes.size() / 3;

        //System.out.println("ArrayList vertex is initialized");
        for (int a = 0; a < vertexCount; a++) {
            vertex.add(compGraphNodes.get(a * 3 + 2));
            //System.out.print("vertex[" + index + "]: " + compGraphNodes.get(index * 3 + 2) + " ");
        }
        //System.out.println();

        vertex.add(0);
        // System.out.println("ArrayList vertex :" + vertex);

        /*
         * processedVertex: is index set of vertices which have already been
         * used
         */
        List<Integer> processedVertex = new ArrayList<Integer>();
        /*
         * Let processedVertex be the set of Nodes already been used in the
         * initialization
         */
        initIterator(vertex, processedVertex);
        processedVertex.clear();
        //System.out.println("maxCliquesSet: " + maxCliquesSet);

    }

    private int enumerateCliques(List<Integer> vertexOfCurrentClique, Stack<Integer> potentialCVertex,
            List<Integer> potentialDVertex, List<Integer> excludedVertex, List<Integer> excludedCVertex) {
        List<Integer> potentialVertex = new ArrayList<Integer>();//Defined as potentialCVertex' in the paper
        for (Integer i : potentialCVertex) {
            potentialVertex.add(i);
        }

        if ((potentialCVertex.size() == 1) && (excludedVertex.isEmpty())) {

            //store best solutions in stack maxCliquesSet
            int cliqueSize = vertexOfCurrentClique.size();

            if (cliqueSize >= bestCliqueSize) {
                if (cliqueSize > bestCliqueSize) {

                    maxCliquesSet.clear();
                    bestCliqueSize = cliqueSize;

                }
                if (cliqueSize == bestCliqueSize) {
                    //System.out.println("vertexOfCurrentClique-Clique " + vertexOfCurrentClique);
                    maxCliquesSet.add(vertexOfCurrentClique);
                }
            }
            return 0;
        }
        findCliques(potentialVertex, vertexOfCurrentClique, potentialCVertex, potentialDVertex, excludedVertex,
                excludedCVertex);
        return 0;
    }

    private List<Integer> findNeighbors(int centralNode) {

        List<Integer> neighborVertex = new ArrayList<Integer>();

        for (int a = 0; a < cEdgeIterationSize; a++) {
            if (cEdges.get(a * 2 + 0) == centralNode) {
                //          System.out.println( cEdges.get(index*2+0) + " " + cEdges.get(index*2+1));
                neighborVertex.add(cEdges.get(a * 2 + 1));
                neighborVertex.add(1); // 1 means: is connected via C-edge
            } else if (cEdges.get(a * 2 + 1) == centralNode) {
                //           System.out.println(cEdges.get(index*2+0) + " " + cEdges.get(index*2+1));
                neighborVertex.add(cEdges.get(a * 2 + 0));
                neighborVertex.add(1); // 1 means: is connected via C-edge
            }

        }
        for (int a = 0; a < dEdgeIterationSize; a++) {
            if (dEdges.get(a * 2 + 0) == centralNode) {
                //       System.out.println( dEdges.get(index*2+0) + " " + dEdges.get(index*2+1));
                neighborVertex.add(dEdges.get(a * 2 + 1));
                neighborVertex.add(2); // 2 means: is connected via D-edge
            } else if (dEdges.get(a * 2 + 1) == centralNode) {
                //        System.out.println(dEdges.get(index*2+0) + " " + dEdges.get(index*2+1));
                neighborVertex.add(dEdges.get(a * 2 + 0));
                neighborVertex.add(2); // 2 means: is connected via D-edge
            }
        }
        return neighborVertex;
    }

    protected int getBestCliqueSize() {
        return bestCliqueSize;
    }

    protected Stack<List<Integer>> getMaxCliqueSet() {
        Stack<List<Integer>> solution = new Stack<List<Integer>>();
        solution.addAll(maxCliquesSet);
        return solution;
    }

    private void findCliques(List<Integer> potentialVertex, List<Integer> vertexOfCurrentClique,
            Stack<Integer> potentialCVertex, List<Integer> potentialDVertex, List<Integer> excludedVertex,
            List<Integer> excludedCVertex) {
        int index = 0;
        List<Integer> neighbourVertex = new ArrayList<Integer>(); ////Initialization ArrayList neighbourVertex

        while (potentialVertex.get(index) != 0) {
            int potentialVertexIndex = potentialVertex.get(index);

            potentialCVertex.removeElement(potentialVertexIndex);

            List<Integer> rCopy = new ArrayList<Integer>(vertexOfCurrentClique);
            Stack<Integer> pCopy = new Stack<Integer>();
            List<Integer> qCopy = new ArrayList<Integer>(potentialDVertex);
            List<Integer> xCopy = new ArrayList<Integer>(excludedVertex);
            List<Integer> yCopy = new ArrayList<Integer>(excludedCVertex);

            neighbourVertex.clear();

            for (Integer obj : potentialCVertex) {
                pCopy.add(obj);
            }

            pCopy.pop();
            //find the neighbors of the central node from potentialCVertex
            //System.out.println("potentialVertex.elementAt(index): " + potentialVertex.elementAt(index));

            neighbourVertex = findNeighbors(potentialVertexIndex);
            groupNeighbors(index, pCopy, qCopy, xCopy, yCopy, neighbourVertex, potentialDVertex, potentialVertex,
                    excludedVertex, excludedCVertex);
            Stack<Integer> pCopyNIntersec = new Stack<Integer>();
            List<Integer> qCopyNIntersec = new ArrayList<Integer>();
            List<Integer> xCopyNIntersec = new ArrayList<Integer>();
            List<Integer> yCopyNIntersec = new ArrayList<Integer>();

            copyVertex(neighbourVertex, pCopyNIntersec, pCopy, qCopyNIntersec, qCopy, xCopyNIntersec,
                    xCopy, yCopyNIntersec, yCopy);

            pCopyNIntersec.push(0);
            rCopy.add(potentialVertexIndex);
            enumerateCliques(rCopy, pCopyNIntersec, qCopyNIntersec, xCopyNIntersec, yCopyNIntersec);
            excludedVertex.add(potentialVertexIndex);
            index++;
        }
    }

    private void copyVertex(List<Integer> neighbourVertex, Stack<Integer> pCopyNIntersec, Stack<Integer> pCopy,
            List<Integer> qCopyNIntersec, List<Integer> qCopy, List<Integer> xCopyNIntersec,
            List<Integer> xCopy, List<Integer> yCopyNIntersec, List<Integer> yCopy) {
        int nElement = -1;
        int nSize = neighbourVertex.size();

        for (int sec = 0; sec < nSize; sec += 2) {

            nElement = neighbourVertex.get(sec);

            if (pCopy.contains(nElement)) {
                pCopyNIntersec.push(nElement);
            }
            if (qCopy.contains(nElement)) {
                qCopyNIntersec.add(nElement);
            }
            if (xCopy.contains(nElement)) {
                xCopyNIntersec.add(nElement);
            }
            if (yCopy.contains(nElement)) {
                yCopyNIntersec.add(nElement);
            }
        }
    }

    private void groupNeighbors(int index, Stack<Integer> pCopy, List<Integer> qCopy, List<Integer> xCopy,
            List<Integer> yCopy, List<Integer> neighbourVertex, List<Integer> potentialDVertex,
            List<Integer> potentialVertex, List<Integer> excludedVertex, List<Integer> excludedCVertex) {

        int nSize = neighbourVertex.size();

        //System.out.println("Neighbors: ");

        for (int b = 0; b < nSize; b += 2) {
            // neighbourVertex[index] is node v
            //Grouping of the neighbors:

            Integer nElementAtB = neighbourVertex.get(b);

            if (neighbourVertex.get(b + 1) == 1) {
                //u and v are adjacent via index C-edge

                if (potentialDVertex.contains(nElementAtB)) {

                    pCopy.push(nElementAtB);
                    //delete neighbourVertex[index] bzw. potentialDVertex[c] from set qCopy, remove C-edges
                    qCopy.remove(nElementAtB);

                }
                if (excludedCVertex.contains(nElementAtB)) {
                    if (excludedVertex.contains(nElementAtB)) {
                        xCopy.add(nElementAtB);
                    }
                    yCopy.remove(nElementAtB);
                }
            }

            //find respective neighbor position in potentialVertex, which is needed for the deletion from potentialVertex

            if (potentialVertex.indexOf(nElementAtB) <= index && potentialVertex.indexOf(nElementAtB) > -1) {
                --index;
            }
            potentialVertex.remove(nElementAtB);
        }
    }

    private void setEdges() {
        boolean dEdgeFlag = false;

        if (dEdges.size() > cEdges.size()) {
            if (dEdges.size() > 10000000 && cEdges.size() > 100000) {
                dEdgeIterationSize = (float) dEdges.size() * 0.000001;
                dEdgeFlag = true;
            } else if (dEdges.size() > 10000000 && cEdges.size() > 5000) {
                dEdgeIterationSize = (float) dEdges.size() * 0.001;
                dEdgeFlag = true;
            }

            //        else if (dEdges.size() > 5000000 && dEdges.size() > cEdges.size()) {
            //            dEdgeIterationSize = (float) dEdges.size() * 0.0001;
            //            dEdgeFlag = true;
            //
            //        } else if (dEdges.size() > 100000 && dEdges.size() > cEdges.size()) {
            //            dEdgeIterationSize = (float) dEdges.size() * 0.1;
            //            dEdgeFlag = true;
            //        }

            //        }

            //        else if (dEdges.size() >= 10000 && 500 >= cEdges.size()) {
            //            dEdgeIterationSize = (float) dEdges.size() * 0.1;
            //            dEdgeFlag = true;
            //        }
            //
            //
            //

        }

        if (dEdgeFlag) {
            checkLowestEdgeCount();
        }
    }

    private void initIterator(List<Integer> vertex, List<Integer> processedVertex) {
        /*
         * vertexOfCurrentClique: set of vertices belonging to the current
         * clique
         */
        List<Integer> vertexOfCurrentClique = new ArrayList<Integer>();
        /*
         * potentialCVertex: is index set of vertices which <index>can</index>
         * be addedto vertexOfCurrentClique, because they are neighbours of
         * vertex u via <i>c-edges</i>
         */
        Stack<Integer> potentialCVertex = new Stack<Integer>();
        /*
         * potentialDVertex: is index set of vertices which
         * <index>cannot</index> be added tovertexOfCurrentClique, because they
         * are neighbours of vertex u via <i>d-edges</i>
         */

        List<Integer> potentialDVertex = new ArrayList<Integer>();
        /*
         * excludedVertex: set of vertices which are not allowed to be added to
         * vertexOfCurrentClique
         */
        List<Integer> excludedVertex = new ArrayList<Integer>();

        /*
         * excludedCVertex: set of vertices which are not allowed to be added to
         * C
         */

        List<Integer> excludedCVertex = new ArrayList<Integer>();

        /*
         * neighbourVertex[u]: set of neighbours of vertex u in Graph G
         */

        List<Integer> neighbourVertex = new ArrayList<Integer>();

        int index = 0;
        while (vertex.get(index) != 0) {
            int centralNode = vertex.get(index);
            potentialCVertex.clear();
            potentialDVertex.clear();
            excludedVertex.clear();
            vertexOfCurrentClique.clear();

            //find the neighbors of the central node from vertex
            neighbourVertex = findNeighbors(centralNode);

            for (int c = 0; c < neighbourVertex.size(); c = c + 2) {
                /*
                 * u and v are adjacent via index vertexOfCurrentClique-edge
                 */
                Integer neighbourVertexOfC = neighbourVertex.get(c);

                //find respective neighbor position in potentialCVertex, which is needed for the deletion from vertex
                //delete neighbor from set vertex

                if (neighbourVertex.get(c + 1) == 1) {
                    if (processedVertex.contains(neighbourVertexOfC)) {
                        excludedVertex.add(neighbourVertexOfC);
                    } else {
                        potentialCVertex.push(neighbourVertexOfC);
                    }
                } else if (neighbourVertex.get(c + 1) == 2) {
                    // u and v are adjacent via index potentialDVertex-edge
                    //System.out.println("u and v are adjacent via index potentialDVertex-edge: " + neighbourVertex.elementAt(c));

                    if (processedVertex.contains(neighbourVertexOfC)) {
                        excludedCVertex.add(neighbourVertexOfC);
                    } else {
                        potentialDVertex.add(neighbourVertexOfC);
                    }
                }

                if (vertex.indexOf(neighbourVertexOfC) <= index && vertex.indexOf(neighbourVertexOfC) > -1) {
                    --index;
                }
                vertex.remove(neighbourVertexOfC);
                //System.out.println("Elements Removed from vertex:" + neighbourVertexOfC);
            }

            potentialCVertex.add(0);
            vertexOfCurrentClique.add(centralNode);

            enumerateCliques(vertexOfCurrentClique, potentialCVertex, potentialDVertex, excludedVertex, excludedCVertex);
            //enumerateCliques(vertexOfCurrentClique, potentialCVertex, potentialDVertex, excludedVertex);
            processedVertex.add(centralNode);
            index++;
        }
    }

    private void checkLowestEdgeCount() {
        if (dEdgeIterationSize < 1 && cEdges.size() <= 5000) {
            dEdgeIterationSize = 2;
        } else if (dEdgeIterationSize < 1) {
            dEdgeIterationSize = 1;
        }
    }
}
