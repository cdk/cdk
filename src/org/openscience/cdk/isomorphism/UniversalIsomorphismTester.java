/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  This code has been kindly provided by Stephane Werner
 *  and Thierry Hanser from IXELIS mail@ixelis.net
 *
 *  IXELIS sarl - Semantic Information Systems
 *  17 rue des C???res 67200 Strasbourg, France
 *  Tel/Fax : +33(0)3 88 27 81 39 Email: mail@ixelis.net
 *
 *  CDK Contact: cdk-devel@lists.sf.net
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.isomorphism;

import java.util.*;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.io.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.isomorphism.mcss.*;
import org.openscience.cdk.tools.ChemModelManipulator;

/**
 *  This class implements a multipurpose structure comparaison tool.
 *  It allows to find maximal common substructure, find the
 *  mapping of a substructure in another structure, and the mapping of
 *  2 isomorph structure.
 *  Structure comparaison may be associated to bond constraints
 *  (mandatory bonds, e.g. scaffolds, reaction cores,...) on each source graph.
 *  The constraint flexibility allows a number of interesting queries.
 *  The substructure analysis relies on the RGraph generic class (see: RGraph)
 *  This class implements the link between the RGraph model and the
 *  the CDK model in this way the RGraph remains independant and may be used
 *  in other contexts.
 *  This algorithm derives from the algorithm described in "Machine Learning of
 *  of generic Reactions : 3. An efficient Algorithm for Maximal Common
 *  Substructure determination" C. Tonnelier, Ph. Jauffret, T. Hanser
 *  and G. Kaufmann. Tetrahedron Vol. 3, No 6, pp. 351-358, 1990.
 *  and modified in the These of T. Hanser "Apprentissage automatique
 *  de m???hodes de synth???e ???partir d'exemples". Universit???Louis Pasteur
 *  STRASBOURG 1993.
 *
 *  < FONT COLOR="#FF0000">
 *     warning :  As a result of the adjacency perception used in this algorithm
 *                there is a single limitation : cyclopropane and isobutane are seen as isomorph
 *                This is due to the fact that these two compounds are the only ones where
 *                each bond is connected two each other bond (bonds are fully conected)
 *                with the same number of bonds and still they have different structures
 *                The algotihm could be easily enhanced with a simple atom mapping manager
 *                to provide an atom level overlap definition that would reveal this case.
 *                We decided not to penalize the whole procedure because of one single
 *                exception query. Furthermore isomorphism may be discarded since  the number of atoms are
 *                not the same (3 != 4) and in most case this will be already
 *                screened out by a fingerprint based filtering.
 *                It is possible to add a special treatment for these special query.
 *   < /FONT>
 *
 *
 * @author     Stephane Werner from IXELIS mail@ixelis.net
 * @created    2002-07-17
 *
 * @since Java 1.4
 */
public class UniversalIsomorphismTester {
  final static int ID1 = 0;
  final static int ID2 = 1;


  ///////////////////////////////////////////////////////////////////////////
  //                            Query Methods
  //
  // This methods are simple applications of the RGraph model on atom containers
  // using different constrains and search options. They give an exemple of the
  // most common queries but of course it is possible to define other type of
  // queries exploiting the constrain and option combinations
  //

  ////
  // Isomorphism search

  /**
   *  Tests if  g1 and g2 are isomorph
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     true if the 2 molecule are isomorph
   */
  public static boolean isIsomorph(AtomContainer g1, AtomContainer g2) {
    return (getIsomorphMap(g1, g2) != null);
  }


  /**
   *  Returns the first isomorph mapping found or null
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first isomorph mapping found projected of g1. This is a List of RMap objects containing Ids of matching bonds.
   */
  public static List getIsomorphMap(AtomContainer g1, AtomContainer g2) {
    List result = null;

    List rMapsList = search(g1, g2, getBitSet(g1),
        getBitSet(g1), false, false);

    if (!rMapsList.isEmpty()) {
      result = (List) rMapsList.get(0);
    }

    return result;
  }


  /**
   *  Returns the first isomorph 'atom mapping' found for g2 in g1.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first isomorph atom mapping found projected on g1. This is a List of RMap objects containing Ids of matching atoms.
   */
  public static List getIsomorphAtomsMap(AtomContainer g1, AtomContainer g2) {
    return (makeAtomsMapOfBondsMap(UniversalIsomorphismTester.getIsomorphMap(g1, g2), g1, g2));
  }


  /**
   *  Returns all the isomorph 'mappings' found between two
   *  atom containers.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the list of all the 'mappings'
   */
  public static List getIsomorphMaps(AtomContainer g1, AtomContainer g2) {
    return search(g1, g2, getBitSet(g1),
        getBitSet(g1), true, true);
  }


  /////
  // Subgraph search

  /**
   *  Returns all the subgraph 'bond mappings' found for g2 in g1
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the list of all the 'mappings' found projected of g1
   *             (list of AtomContainer )
   */
  public static List getSubgraphMaps(AtomContainer g1, AtomContainer g2) {
    return search(g1, g2, new BitSet(), getBitSet(g2), true, true);
  }


  /**
   *  Returns the first subgraph 'bond mapping' found for g2 in g1.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first subgraph bond mapping found projected on g1. This is a List of RMap objects containing Ids of matching bonds.
   */
  public static List getSubgraphMap(AtomContainer g1, AtomContainer g2) {
    List result = null;
    List rMapsList = search(g1, g2, new BitSet(),
        getBitSet(g2), false, false);

    if (!rMapsList.isEmpty()) {
      result = (List) rMapsList.get(0);
    }

    return result;
  }


  /**
   *  Returns the first subgraph 'atom mapping' found for g2 in g1.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first subgraph atom mapping found projected on g1. This is a List of RMap objects containing Ids of matching atoms.
   */
  public static List getSubgraphAtomsMap(AtomContainer g1, AtomContainer g2) {
    return (makeAtomsMapOfBondsMap(UniversalIsomorphismTester.getSubgraphMap(g1, g2), g1, g2));
  }


  /**
   *  Tests if g2 a subgraph of g1
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     true if g2 a subgraph on g1
   */
  public static boolean isSubgraph(AtomContainer g1, AtomContainer g2) {
    return (getSubgraphMap(g1, g2) != null);
  }


  ////
  // Maximum common substructure search

  /**
   *  Returns all the maximal common substructure between 2 atom containers
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the list of all the maximal common substructure
   *             found projected of g1 (list of AtomContainer )
   */
  public static List getOverlaps(AtomContainer g1, AtomContainer g2) {
    List rMapsList = search(g1, g2, new BitSet(),
        new BitSet(), true, false);

    // projection on G1
    ArrayList graphList = projectList(rMapsList, g1, ID1);

    // reduction of set of solution (isomorphism and substructure
    // with different 'mappings'
    ArrayList reducedGraphList = getMaximum(graphList);

    return reducedGraphList;
  }


  /**
   *  Transforms an AtomContainer into a BitSet (which's size = number of bond
   *  in the atomContainer, all the bit are set to true)
   *
   * @param  ac  AtomContainer to transform
   * @return     The bitSet
   */
  public static BitSet getBitSet(AtomContainer ac) {
    BitSet bs = null;
    int n = ac.getBondCount();

    if (n != 0) {
      bs = new BitSet(n);
      for (int i = 0; i < n; i++) { bs.set(i); };
    } else {
      bs = new BitSet();
    }

    return bs;
  }


  //////////////////////////////////////////////////
  //          Internal methods

  /**
   *  Builds the RGraph ( resolution graph ), from two atomContainer
   * (description of the two molecules to compare)
   * This is the interface point between the CDK model and
   * the generic MCSS algorithm based on the RGRaph.
   *
   * @param  g1  Description of the first molecule
   * @param  g2  Description of the second molecule
   * @return     the rGraph
   */
  public static RGraph buildRGraph(AtomContainer g1, AtomContainer g2) {
    RGraph rGraph = new RGraph();
    nodeConstructor(rGraph, g1, g2);
    arcConstructor(rGraph, g1, g2);
    return rGraph;
  }


  /**
   *  General Rgraph parsing method (usually not used directly)
   *  This method is the entry point for the recursive search
   *  adapted to the atom container input.
   *
   * @param  g1                first molecule
   * @param  g2                second molecule
   * @param  c1                initial condition ( bonds from g1 that
   *                           must be contains in the solution )
   * @param  c2                initial condition ( bonds from g2 that
   *                           must be contains in the solution )
   * @param  findAllStructure  if false stop at the first structure found
   * @param  findAllMap        if true search all the 'mappings' for one same
   *                           structure
   * @return                   a list of rMapList that represent the serach solutions
   */
  public static List search(AtomContainer g1, AtomContainer g2, BitSet c1,
      BitSet c2, boolean findAllStructure, boolean findAllMap) {
    // reset result
    ArrayList rMapsList = new ArrayList();

    // build the RGraph corresponding to this problem
    RGraph rGraph = buildRGraph(g1, g2);

    // parse the RGraph with the given constrains and options
    rGraph.parse(c1, c2, findAllStructure, findAllMap);
    List solutionList = rGraph.getSolutions();

    // convertions of RGraph's internal solutions to G1/G2 mappings
    for (Iterator i = solutionList.iterator(); i.hasNext(); ) {
      BitSet set = (BitSet) i.next();
      rMapsList.add(rGraph.bitSetToRMap(set));
    }

    return rMapsList;
  }


  //////////////////////////////////////
  //    Manipulation tools

  /**
   *  Projects a list of RMap on a molecule
   *
   * @param  rMapList  the list to project
   * @param  g         the molecule on which project
   * @param  id        the id in the RMap of the molecule g
   * @return           an AtomContainer
   */
  public static AtomContainer project(List rMapList, AtomContainer g, int id) {
    AtomContainer ac = new AtomContainer();

    Bond[] bondList = g.getBonds();

    Hashtable table = new Hashtable();
    Atom a1 = null;
    Atom a2 = null;
    Atom a = null;
    Bond bond = null;

    for (Iterator i = rMapList.iterator(); i.hasNext(); ) {
      RMap rMap = (RMap) i.next();
      if (UniversalIsomorphismTester.ID1 == 0) {
        bond = bondList[rMap.getId1()];
      } else {
        bond = bondList[rMap.getId2()];
      }

      a = bond.getAtomAt(0);
      a1 = (Atom) table.get(a);

      if (a1 == null) {
        a1 = (Atom) a.clone();
        ac.addAtom(a1);
        table.put(a, a1);
      }

      a = bond.getAtomAt(1);
      a2 = (Atom) table.get(a);

      if (a2 == null) {
        a2 = (Atom) a.clone();
        ac.addAtom(a2);
        table.put(a, a2);
      }

      ac.addBond(new Bond(a1, a2, bond.getOrder()));
    }
    return ac;
  }


  /**
   *  Project a list of RMapsList on a molecule
   *
   * @param  rMapsList  list of RMapsList to project
   * @param  g          the molecule on which project
   * @param  id         the id in the RMap of the molecule g
   * @return            a list of AtomContainer
   */
  public static ArrayList projectList(List rMapsList, AtomContainer g, int id) {
    ArrayList graphList = new ArrayList();

    for (Iterator i = rMapsList.iterator(); i.hasNext(); ) {
      List rMapList = (List) i.next();
      AtomContainer ac = project(rMapList, g, id);
      graphList.add(ac);
    }
    return graphList;
  }


  /**
   * Test utility on command line
   *
   * Usage : java RTools g1.mol g2.mol
   *
   * @param  args  The command line arguments
   */
  public static void main(String[] args) {
    // loading the source graphs
    System.out.println("Loading... ");
    AtomContainer g1 = loadFile(new File(args[0]));
    AtomContainer g2 = loadFile(new File(args[1]));
    System.out.println("Searching... ");
    long start = System.currentTimeMillis();

    // some trivial queries
    start = System.currentTimeMillis();
    System.out.println("isIsomorph(g1,g2) : " + isIsomorph(g1, g2) + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("isIsomorph(g1,g1) : " + isIsomorph(g1, g1) + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("isIsomorph(g2,g2) : " + isIsomorph(g2, g2) + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("isSubGraph(g1,g2) : " + isSubgraph(g1, g2) + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("isSubGraph(g2,g1) : " + isSubgraph(g2, g1) + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("isSubGraph(g1,g1) : " + isSubgraph(g1, g1) + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("isSubGraph(g2,g2) : " + isSubgraph(g2, g2) + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("getOverlaps(g1,g2) : " + getOverlaps(g1, g2).size() + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("getOverlaps(g2,g1) : " + getOverlaps(g2, g1).size() + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("getOverlaps(g1,g1) : " + getOverlaps(g1, g1).size() + " (" + (System.currentTimeMillis() - start) + " ms)");
    start = System.currentTimeMillis();
    System.out.println("getOverlaps(g1,g1) : " + getOverlaps(g2, g2).size() + " (" + (System.currentTimeMillis() - start) + " ms)");
  }


  /**
   *  remove all redundant solution
   *
   * @param  graphList  the list of structure to clean
   * @return            the list cleaned
   */
  private static ArrayList getMaximum(ArrayList graphList) {
    ArrayList reducedGraphList = (ArrayList) graphList.clone();

    for (int i = 0; i < graphList.size(); i++) {
      AtomContainer gi = (AtomContainer) graphList.get(i);

      for (int j = i + 1; j < graphList.size(); j++) {
        AtomContainer gj = (AtomContainer) graphList.get(j);

        if (i != j) {
          // if G1 isomorph to G2 or G1 included in G2 then Gi
          // is discarded (redondant or irrevelant)
          if (isIsomorph(gi, gj) || isSubgraph(gi, gj)) {
            reducedGraphList.remove(gj);
          }
        }
      }
    }
    return reducedGraphList;
  }


  /**
   *  This makes a map of matching atoms out of a map of matching bonds as produced by the get(Subgraph|Ismorphism)Map(s) methods.
   *
   * @param  l   The list produced by the getMap method.
   * @param  g1  The first atom container.
   * @param  g2  The second one (first and second as in getMap)
   * @return     The mapping found projected on g1. This is a List of RMap objects containing Ids of matching atoms.
   */
  private static List makeAtomsMapOfBondsMap(List l, AtomContainer g1, AtomContainer g2) {
    Bond[] bonds1 = g1.getBonds();
    Bond[] bonds2 = g2.getBonds();
    List result = new Vector();
    for (int i = 0; i < l.size(); i++) {
      Bond bond1 = bonds1[((RMap) l.get(i)).getId1()];
      Bond bond2 = bonds2[((RMap) l.get(i)).getId2()];
      Atom[] atom1 = bond1.getAtoms();
      Atom[] atom2 = bond2.getAtoms();
      for (int j = 0; j < 2; j++) {
        Bond[] bondsConnectedToAtom1j = g1.getConnectedBonds(atom1[j]);
        for (int k = 0; k < bondsConnectedToAtom1j.length; k++) {
          if (bondsConnectedToAtom1j[k] != bond1) {
            Bond testBond = bondsConnectedToAtom1j[k];
            for (int m = 0; m < l.size(); m++) {
              Bond testBond2 = null;
              if (((RMap) l.get(m)).getId1() == g1.getBondNumber(testBond)) {
                testBond2 = bonds2[((RMap) l.get(m)).getId2()];
                for (int n = 0; n < 2; n++) {
                  Vector bondsToTest = g2.getConnectedBondsVector(atom2[n]);
                  if (bondsToTest.contains(testBond2)) {
                    RMap map;
                    if (j == n) {
                      map = new RMap(g1.getAtomNumber(atom1[0]), g2.getAtomNumber(atom2[0]));
                    } else {
                      map = new RMap(g1.getAtomNumber(atom1[1]), g2.getAtomNumber(atom2[0]));
                    }
                    if (!result.contains(map)) {
                      result.add(map);
                    }
                    RMap map2;
                    if (j == n) {
                      map2 = new RMap(g1.getAtomNumber(atom1[1]), g2.getAtomNumber(atom2[1]));
                    } else {
                      map2 = new RMap(g1.getAtomNumber(atom1[0]), g2.getAtomNumber(atom2[1]));
                    }
                    if (!result.contains(map2)) {
                      result.add(map2);
                    }
                  }
                }
              }
            }
            break;
          }
        }
      }
    }
    return (result);
  }


  /**
   *  Builds  the nodes of the RGraph ( resolution graph ), from
   *  two atom containers (description of the two molecules to compare)
   *
   * @param  gr   the target RGraph
   * @param  ac1  description of the first molecule
   * @param  ac2  description of the second molecule
   */
  private static void nodeConstructor(RGraph gr, AtomContainer ac1, AtomContainer ac2) {
    // resets the target graph.
    gr.clear();
    Bond[] bondsA1 = ac1.getBonds();
    Bond[] bondsA2 = ac2.getBonds();
    int k = 0;

    // compares each bond of G1 to each bond of G2
    for (int i = 0; i < bondsA1.length; i++) {
      for (int j = 0; j < bondsA2.length; j++) {
        // if both bonds are compatible then create an association node
        // in the resolution graph
        if ((bondsA1[i].getOrder() == bondsA2[j].getOrder() || (bondsA1[i].getFlag(CDKConstants.ISAROMATIC) && bondsA2[j].getFlag(CDKConstants.ISAROMATIC)))
             && ((bondsA1[i].getAtomAt(0).getSymbol().equals(bondsA2[j].getAtomAt(0).getSymbol())
             && bondsA1[i].getAtomAt(1).getSymbol().equals(bondsA2[j].getAtomAt(1).getSymbol()))
             || (bondsA1[i].getAtomAt(0).getSymbol().equals(bondsA2[j].getAtomAt(1).getSymbol())
             && bondsA1[i].getAtomAt(1).getSymbol().equals(bondsA2[j].getAtomAt(0).getSymbol()))
            )
            ) {
          gr.addNode(new RNode(i, j));
        }
      }
    }
  }


  /**
   *  Build edges of the RGraphs
   *  This method create the edge of the RGraph and
   *  calculates the incompatibility and neighbourhood
   *  relationships between RGraph nodes.
   *
   * @param  gr   the rGraph
   * @param  ac1  Description of the first molecule
   * @param  ac2  Description of the second molecule
   */
  private static void arcConstructor(RGraph gr, AtomContainer ac1, AtomContainer ac2) {
    // each node is incompatible with himself
    for (int i = 0; i < gr.getGraph().size(); i++) {
      RNode x = (RNode) gr.getGraph().get(i);
      x.getForbidden().set(i);
    }

    Bond a1 = null;
    Bond a2 = null;
    Bond b1 = null;
    Bond b2 = null;

    Bond[] bondsA1 = ac1.getBonds();
    Bond[] bondsA2 = ac2.getBonds();

    gr.setFirstGraphSize(ac1.getBondCount());
    gr.setSecondGraphSize(ac2.getBondCount());

    for (int i = 0; i < gr.getGraph().size(); i++) {
      RNode x = (RNode) gr.getGraph().get(i);

      // two nodes are neighbours if their adjacency
      // relationship in are equivalent in G1 and G2
      // else they are incompatible.
      for (int j = i + 1; j < gr.getGraph().size(); j++) {
        RNode y = (RNode) gr.getGraph().get(j);

        a1 = bondsA1[((RNode) gr.getGraph().get(i)).getRMap().getId1()];
        a2 = bondsA2[((RNode) gr.getGraph().get(i)).getRMap().getId2()];
        b1 = bondsA1[((RNode) gr.getGraph().get(j)).getRMap().getId1()];
        b2 = bondsA2[((RNode) gr.getGraph().get(j)).getRMap().getId2()];

        if (a1.equals(b1) || a2.equals(b2) ||
            (!adjacency(a1, b1).equals(adjacency(a2, b2)))) {
          x.getForbidden().set(j);
          y.getForbidden().set(i);
        } else if (!adjacency(a1, b1).equals("")) {
          x.getExtension().set(j);
          y.getExtension().set(i);
        }
      }
    }
  }


  /**
   *  Determines if 2 bond have 1 atom in common
   *
   * @param  a  first bond
   * @param  b  second bond
   * @return    the symbol of the common atom or "" if
   *            the 2 bonds have no common atom
   */
  private static String adjacency(Bond a, Bond b) {
    String symbol = "";

    if (a.contains(b.getAtomAt(0))) {
      symbol = b.getAtomAt(0).getSymbol();
    } else if (a.contains(b.getAtomAt(1))) {
      symbol = b.getAtomAt(1).getSymbol();
    }
    return symbol;
  }


  /**
   * Test purpose file reading method
   * Load a molfile into an atom container
   *
   * @param  file  the file to load
   * @return       Description of the Returned Value
   */
  private static AtomContainer loadFile(File file) {
    ChemFile outFile = null;
    ChemObjectReader reader = null;
    ChemFile chemFile = new ChemFile();

    try {
      FileReader fileReader = null;

      if (file.exists()) {
        fileReader = new FileReader(file);
        reader = new MDLReader(fileReader);
      } else {
        System.err.println("Target file doas not exist:" + file);
      }

      try {
        outFile = (ChemFile) reader.read((ChemObject) new ChemFile());
      } catch (CDKException ex) {
        System.out.println(ex);
      }

      fileReader.close();
    } catch (IOException e) {
      System.out.println(e);
    }

    return ChemModelManipulator.getAllInOneContainer(outFile.getChemSequence(0).getChemModel(0));
  }
}

