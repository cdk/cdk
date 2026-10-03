/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your container code files, and to any copyright notice that you may distribute
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
package org.openscience.cdk.smsd.algorithm.mcgregor;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;

class McGregorChecksTest {

    @Test
    void excludesEveryMappedAtomFromBothMolecules() {
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        for (int i = 0; i < 5; i++) {
            molecule.addAtom(new Atom("C"));
        }
        List<Integer> mapping = Arrays.asList(0, 4, 2, 1, 4, 3);
        Assertions.assertEquals(Arrays.asList(1, 3),
                McGregorChecks.markUnMappedAtoms(true, molecule, mapping, 3));
        Assertions.assertEquals(Arrays.asList(0, 2),
                McGregorChecks.markUnMappedAtoms(false, molecule, mapping, 3));
    }
    @Test
    void labelsMappedBondEndpointAndItsCorrespondingAtom() throws Exception {
        org.openscience.cdk.smiles.SmilesParser parser =
                new org.openscience.cdk.smiles.SmilesParser(DefaultChemObjectBuilder.getInstance());
        for (int mapped = 0; mapped < 2; mapped++) {
            IAtomContainer source = parser.parseSmiles("CC");
            IAtomContainer target = parser.parseSmiles("CC");
            QueryProcessor processor = new QueryProcessor(
                    McGregorChecks.generateCTabCopy(source), McGregorChecks.generateCTabCopy(target),
                    new String[]{"$1"}, 0, 0, new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), 1, new java.util.ArrayList<>(), new java.util.ArrayList<>());
            processor.process(source, target, Arrays.asList(1 - mapped),
                    Arrays.asList(mapped, 1 - mapped), 0);
            Assertions.assertEquals("$1", processor.getCTab1().get(mapped));
            Assertions.assertEquals("C", processor.getCTab1().get(1 - mapped));
            Assertions.assertEquals("$1", processor.getCTab2().get(1 - mapped));
            Assertions.assertEquals("C", processor.getCTab2().get(mapped));
        }
    }

    @Test
    void recursiveLabelsKeepTheMappedEndpointAndCorrespondingAtom() {
        for (int mappedPosition = 0; mappedPosition < 2; mappedPosition++) {
            List<Integer> sourceBonds = mappedPosition == 0
                    ? Arrays.asList(1, 2, 1) : Arrays.asList(2, 1, 1);
            List<Integer> targetBonds = mappedPosition == 0
                    ? Arrays.asList(2, 1, 1) : Arrays.asList(1, 2, 1);
            QueryProcessor processor = new QueryProcessor(
                    new java.util.ArrayList<>(Arrays.asList("C", "C", "X", "X")),
                    new java.util.ArrayList<>(Arrays.asList("C", "C", "X", "X")),
                    new String[]{"$1", "$2"}, 0, 0, new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), 2, new java.util.ArrayList<>(), new java.util.ArrayList<>());
            // The seed has already grown from atom 0 to atom 1. Bond 1--2 remains.
            processor.process(1, 1, sourceBonds, targetBonds, Arrays.asList(2),
                    Arrays.asList(0, 0, 1, 1), 0);
            Assertions.assertEquals("$2", processor.getCTab1().get(mappedPosition));
            Assertions.assertEquals("C", processor.getCTab1().get(1 - mappedPosition));
            Assertions.assertEquals("$2", processor.getCTab2().get(1 - mappedPosition));
            Assertions.assertEquals("C", processor.getCTab2().get(mappedPosition));
        }
    }

    @Test
    void mappedBoundaryAtomsUseDistinctCorrespondingLabels() throws Exception {
        org.openscience.cdk.smiles.SmilesParser parser =
                new org.openscience.cdk.smiles.SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer source = parser.parseSmiles("CCC");
        IAtomContainer target = parser.parseSmiles("CCC");
        List<Integer> mapping = Arrays.asList(0, 2, 2, 0);
        String[] labels = {"$left", "$right"};
        QueryProcessor query = new QueryProcessor(
                McGregorChecks.generateCTabCopy(source), McGregorChecks.generateCTabCopy(target), labels,
                0, 0, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                2, new java.util.ArrayList<>(), new java.util.ArrayList<>());
        query.process(source, target, Arrays.asList(1), mapping, 0);
        Assertions.assertEquals("$left", query.getCTab1().get(0));
        Assertions.assertEquals("$right", query.getCTab1().get(5));
        Assertions.assertEquals("$right", query.getCTab2().get(0));
        Assertions.assertEquals("$left", query.getCTab2().get(5));
        Assertions.assertEquals("C", query.getCTab1().get(1));
        Assertions.assertEquals("C", query.getCTab1().get(4));

        TargetProcessor targetProcess = new TargetProcessor(
                McGregorChecks.generateCTabCopy(source), McGregorChecks.generateCTabCopy(target), labels,
                0, 0, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                0, new java.util.ArrayList<>(), new java.util.ArrayList<>());
        targetProcess.process(target, Arrays.asList(1), 2, new java.util.ArrayList<>(),
                new java.util.ArrayList<>(), mapping, 0);
        Assertions.assertEquals("$right", targetProcess.getCTab2().get(0));
        Assertions.assertEquals("$left", targetProcess.getCTab2().get(5));
        Assertions.assertEquals("C", targetProcess.getCTab2().get(1));
        Assertions.assertEquals("C", targetProcess.getCTab2().get(4));
    }

    @Test
    void recursiveTargetLabelsUseTheStableMappedPairPosition() {
        for (int mappedPosition = 0; mappedPosition < 2; mappedPosition++) {
            List<Integer> targetBonds = mappedPosition == 0
                    ? Arrays.asList(1, 2, 1) : Arrays.asList(2, 1, 1);
            TargetProcessor target = new TargetProcessor(
                    new java.util.ArrayList<>(Arrays.asList("C", "C", "X", "X")),
                    new java.util.ArrayList<>(Arrays.asList("C", "C", "X", "X")),
                    new String[]{"$1", "$2"}, 0, 0, new java.util.ArrayList<>(),
                    new java.util.ArrayList<>(), 0, new java.util.ArrayList<>(), new java.util.ArrayList<>());
            target.process(1, Arrays.asList(2), 2, targetBonds,
                    new java.util.ArrayList<>(Arrays.asList("C", "C", "X", "X")),
                    Arrays.asList(0, 0, 1, 1), 0, new java.util.ArrayList<>(), new java.util.ArrayList<>());
            Assertions.assertEquals("$2", target.getCTab2().get(mappedPosition));
            Assertions.assertEquals("C", target.getCTab2().get(1 - mappedPosition));
        }
    }

    @Test
    void recursiveGrowthRetainsExistingLabelsAndAssignsTheNextPairLabel() {
        List<Integer> bonds = Arrays.asList(0, 2, 1, 1, 3, 1);
        List<Integer> mapping = Arrays.asList(0, 0, 1, 1);
        List<String> existing = Arrays.asList("$1", "C", "C", "X", "C", "C", "X", "X");
        QueryProcessor query = new QueryProcessor(new java.util.ArrayList<>(existing),
                new java.util.ArrayList<>(existing), new String[]{"$1", "$2"},
                0, 0, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                2, new java.util.ArrayList<>(), new java.util.ArrayList<>());
        query.process(2, 2, bonds, bonds, Arrays.asList(2, 3), mapping, 0);
        Assertions.assertEquals("$1", query.getCTab1().get(0));
        Assertions.assertEquals("$2", query.getCTab1().get(4));
        Assertions.assertEquals("$1", query.getCTab2().get(0));
        Assertions.assertEquals("$2", query.getCTab2().get(4));
        TargetProcessor target = new TargetProcessor(query.getCTab1(), query.getCTab2(),
                new String[]{"$1", "$2"}, 0, 0, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                query.getNeighborBondNumA(), query.getIBondNeighboursA(), query.getCBondNeighborsA());
        target.process(2, Arrays.asList(2, 3), 2, bonds, query.getCTab2(), mapping, 0,
                new java.util.ArrayList<>(), new java.util.ArrayList<>());
        Assertions.assertEquals("$1", target.getCTab2().get(0));
        Assertions.assertEquals("$2", target.getCTab2().get(4));
    }

    @Test
    void bothBoundaryProbesPropagateAtomPredicateFailures() {
        IllegalStateException failure = new IllegalStateException("Atom predicate evaluation failed");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(new Atom("C"));
        org.openscience.cdk.isomorphism.matchers.QueryAtom predicate =
                new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                        new org.openscience.cdk.isomorphism.matchers.Expr(
                                org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE)) {
            @Override
            public boolean matches(org.openscience.cdk.interfaces.IAtom atom) {
                throw failure;
            }
        };
        predicate.setSymbol("C");
        source.addAtom(predicate);
        source.addBond(0, 1, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(new Atom("C"));
        target.addAtom(new Atom("C"));
        target.addBond(0, 1, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        List<Integer> triples = Arrays.asList(0, 1, 1);
        List<String> labels = Arrays.asList("$1", "C", "C", "X");
        Assertions.assertSame(failure, Assertions.assertThrows(IllegalStateException.class,
                () -> McGregorChecks.isFurtherMappingPossible(source, target, 1, 1,
                        triples, triples, labels, labels, true)));
        McgregorHelper helper = new McgregorHelper(false, 1, Arrays.asList(0, 0), 1, 1,
                triples, triples, labels, labels, 0, 0, java.util.Collections.emptyList(),
                java.util.Collections.emptyList(), java.util.Collections.emptyList(),
                java.util.Collections.emptyList());
        Assertions.assertSame(failure, Assertions.assertThrows(IllegalStateException.class,
                () -> McGregorChecks.isFurtherMappingPossible(source, target, helper, true)));
    }

}
