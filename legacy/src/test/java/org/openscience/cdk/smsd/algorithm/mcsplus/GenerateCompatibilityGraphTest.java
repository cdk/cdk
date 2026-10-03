/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;

/**
 * @author Asad
 */
class GenerateCompatibilityGraphTest {

    public GenerateCompatibilityGraphTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    void testSomeMethod() throws IOException {
        // TODO review the generated test code and remove the default call to fail.
        Assertions.assertNotNull(new GenerateCompatibilityGraph(DefaultChemObjectBuilder.getInstance().newAtomContainer(), DefaultChemObjectBuilder.getInstance().newAtomContainer(), true));
    }
    @Test
    void handlesAtomsWithMoreThanSixNeighbours() throws IOException {
        org.openscience.cdk.interfaces.IAtomContainer molecule =
                DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new org.openscience.cdk.Atom("Fe"));
        for (int i = 1; i <= 7; i++) {
            molecule.addAtom(new org.openscience.cdk.Atom("N"));
            molecule.addBond(0, i, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        }
        GenerateCompatibilityGraph graph = new GenerateCompatibilityGraph(molecule, molecule, true);
        Assertions.assertFalse(graph.getCompGraphNodes().isEmpty());
        Assertions.assertFalse(graph.getCEgdes().isEmpty());
    }

    @Test
    void keepsTheCentralElementSeparateFromNeighbourLabels() throws Exception {
        org.openscience.cdk.interfaces.IAtomContainer molecule =
                new org.openscience.cdk.smiles.SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles("CN");
        GenerateCompatibilityGraph graph = new GenerateCompatibilityGraph(molecule, molecule, true);
        java.util.List<Integer> nodes = graph.getCompGraphNodes();
        Assertions.assertEquals(6, nodes.size());
        for (int i = 0; i < nodes.size(); i += 3) {
            Assertions.assertEquals(molecule.getAtom(nodes.get(i)).getSymbol(),
                    molecule.getAtom(nodes.get(i + 1)).getSymbol());
        }
    }

    @Test
    void preservesCompatibleNonBondedPairsInPathCliques() throws Exception {
        org.openscience.cdk.interfaces.IAtomContainer molecule =
                new org.openscience.cdk.smiles.SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles("CCC");
        GenerateCompatibilityGraph graph = new GenerateCompatibilityGraph(molecule, molecule, true);
        Assertions.assertFalse(graph.getDEgdes().isEmpty());
        BKKCKCF cliques = new BKKCKCF(graph.getCompGraphNodes(), graph.getCEgdes(), graph.getDEgdes());
        Assertions.assertEquals(3, cliques.getBestCliqueSize());
        Assertions.assertEquals(2, cliques.getMaxCliqueSet().size());
    }

    @Test
    void allowsAtomPairsWithDifferentOriginalNeighbourhoods() throws Exception {
        org.openscience.cdk.smiles.SmilesParser parser =
                new org.openscience.cdk.smiles.SmilesParser(DefaultChemObjectBuilder.getInstance());
        GenerateCompatibilityGraph graph = new GenerateCompatibilityGraph(
                parser.parseSmiles("CCC(C)C"), parser.parseSmiles("CCCCC"), true);
        Assertions.assertEquals(25 * 3, graph.getCompGraphNodes().size());
    }

    @Test
    void preservesPredicatesInOrdinaryContainers() throws Exception {
        org.openscience.cdk.interfaces.IAtomContainer query =
                DefaultChemObjectBuilder.getInstance().newAtomContainer();
        query.addAtom(new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                new org.openscience.cdk.isomorphism.matchers.Expr(
                        org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT, 6)));
        org.openscience.cdk.interfaces.IAtomContainer target =
                new org.openscience.cdk.smiles.SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles("CO");
        GenerateCompatibilityGraph graph = new GenerateCompatibilityGraph(query, target, false);
        Assertions.assertEquals(java.util.Arrays.asList(0, 0, 1), graph.getCompGraphNodes());
    }

}
