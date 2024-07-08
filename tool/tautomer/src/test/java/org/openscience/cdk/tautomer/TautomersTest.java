/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

package org.openscience.cdk.tautomer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

final class TautomersTest {

    @Test
    void cytosine() throws Exception {
        IAtomContainer cytosine = fromSmi("NC1=NC(=O)NC=C1");
        assertTautomers(Tautomers.generate(cytosine),
                        "N=C1NC(O)=NC=C1",
                        "N=C1N=C(O)NC=C1",
                        "NC1=NC(O)=NC=C1",
                        "N=C1NC(=O)NC=C1",
                        "NC=1NC(=O)N=CC1",
                        "NC1=NC(=O)NC=C1");
    }

    @Test
    void nitro() throws Exception {
        assertTautomers(Tautomers.generate(fromSmi("[O-][N+]1=NC=CC(O)=C1")),
                        "[O-][N+]1=NC=CC(O)=C1",
                        "[O-][N+]=1NC=CC(=O)C1");
        assertTautomers(Tautomers.generate(fromSmi("O=N1=NC=CC(O)=C1")),
                        "O=N=1NC=CC(=O)C1",
                        "O=N1=NC=CC(O)=C1");
    }

    @Test
    void adenine() throws Exception {
        IAtomContainer adenine = fromSmi("NC1=C2N=CNC2=NC=N1");
        assertTautomers(Tautomers.generate(adenine),
                        "N=C1C=2NC=NC2NC=N1",
                        "N=C1C=2NC=NC2N=CN1",
                        "NC1=C2NC=NC2=NC=N1",
                        "N=C1C=2N=CNC2NC=N1",
                        "N=C1C=2N=CNC2N=CN1",
                        "NC1=C2N=CNC2=NC=N1",
                        "NC1=C2N=CN=C2NC=N1",
                        "NC1=C2N=CN=C2N=CN1");
    }

    @Test
    void guanine() throws Exception {
        IAtomContainer guanine = fromSmi("OC1=C2N=CNC2=NC(=N)N1");
        assertTautomers(Tautomers.generate(guanine),
                        "OC1=C2N=CN=C2NC(=N)N1",
                        "OC1=C2NC=NC2=NC(=N)N1",
                        "OC1=C2N=CNC2=NC(=N)N1",
                        "OC1=C2N=CN=C2N=C(N)N1",
                        "OC=1C=2NC=NC2NC(=N)N1",
                        "OC=1C=2N=CNC2NC(=N)N1",
                        "OC1=C2N=CN=C2NC(N)=N1",
                        "OC1=C2NC=NC2=NC(N)=N1",
                        "OC1=C2N=CNC2=NC(N)=N1",
                        "O=C1C=2NC=NC2NC(=N)N1",
                        "O=C1C=2N=CNC2NC(=N)N1",
                        "O=C1C=2NC=NC2N=C(N)N1",
                        "O=C1C=2N=CNC2N=C(N)N1",
                        "O=C1C=2NC=NC2NC(N)=N1",
                        "O=C1C=2N=CNC2NC(N)=N1");
    }

    @Test
    void guanineCanon() throws Exception {
        assertCanonicalTautomers("OC1=C2N=CNC2=NC(=N)N1");
    }

    @Test
    void guanineClosure() throws Exception {
        assertClosureTautomers("OC1=C2N=CNC2=NC(=N)N1");
    }

    @Test
    void CO2H() throws Exception {
        IAtomContainer mol = fromSmi("OC(=O)C1C(C(=O)O)CC(C(=O)O)CC1");
        assertTautomers(Tautomers.generate(mol)); // 0 tautomers
    }

    @Test
    void guanineKetoEnol() throws Exception {
        // 43 Keto-Enol Tautomers of guanine
        IAtomContainer guanine = fromSmi("OC1=C2N=CNC2=NC(=N)N1");
        Iterable<IAtomContainer> generater = Tautomers.generate(guanine,
                                                                EnumSet.of(Tautomers.Type.CARBON_SHIFTS),
                                                                Tautomers.Order.SEQUENTIAL);
        // dumpTautomers(generater);
        Assertions.assertEquals(StreamSupport.stream(generater.spliterator(), false)
                                             .count(),
                                43);
    }

    @Tag("SlowTest")
    @Test
    void cid14054218() throws Exception {
        assertClosureTautomers("C(NC(CC1=CC=CC=C1)=C(O)NCCC(O)=O)(CC2=CC=CC=C2)C(=O)OCOC(=O)C(C)(C)C",
                               Tautomers.Type.CARBON_SHIFTS);
    }

    @Test
    void ketoEnolConsistency() throws Exception {
        assertClosureTautomers("CCC=CO", Tautomers.Type.CARBON_SHIFTS);
        assertClosureTautomers("CCCC=O", Tautomers.Type.CARBON_SHIFTS);
        assertClosureTautomers("CC=CCO", Tautomers.Type.CARBON_SHIFTS);

        assertClosureTautomers("C1CC=CCC1=O", Tautomers.Type.CARBON_SHIFTS);
        assertClosureTautomers("C1CC=CC=C1O", Tautomers.Type.CARBON_SHIFTS);
        assertClosureTautomers("C=1CC=CCC1O", Tautomers.Type.CARBON_SHIFTS);
        assertClosureTautomers("C=1CC=CCC1O", Tautomers.Type.CARBON_SHIFTS);
    }

    void assertTautomers(Iterable<IAtomContainer> stream, String... smis) throws CDKException {
        List<String> expected = new ArrayList<>(Arrays.asList(smis));
        List<String> actual = new ArrayList<>();
        for (IAtomContainer container : stream)
            actual.add(toSmi(container));
        Collections.sort(expected);
        Collections.sort(actual);
        Assertions.assertEquals(expected, actual);
    }

    void assertCanonicalTautomers(String input) throws CDKException, CloneNotSupportedException {
        String expected = null;
        for (IAtomContainer tautomer : Tautomers.generate(fromSmi(input),
                                                          Tautomers.Order.SEQUENTIAL)) {
            String smiles = toSmi(Tautomers.generate(tautomer.clone(), Tautomers.Order.CANONICAL).iterator().next());
            if (expected == null)
                expected = smiles;
            else
                Assertions.assertEquals(expected, smiles);
        }
        Assertions.assertNotNull(expected);
    }

    /**
     * Ensure we generate the same tautomers for each output/input. The tautomer
     * is "closed"  in that for a given input we generate a group of outputs, and
     * from each of those outputs we generate the same group.
     *
     * @param input the input smiles
     * @throws CDKException exception occurred
     */
    void assertClosureTautomers(String input, Tautomers.Type ... types) throws CDKException {
        Set<Tautomers.Type> options = EnumSet.noneOf(Tautomers.Type.class);
        Collections.addAll(options, types);
        List<String> firstGen = new ArrayList<>();
        for (IAtomContainer tautomer : Tautomers.generate(fromSmi(input),
                                                          options,
                                                          Tautomers.Order.SEQUENTIAL)) {
            firstGen.add(toSmi(tautomer));
        }
        Assertions.assertFalse(firstGen.isEmpty(), "No tautomers");
        Collections.sort(firstGen);
        for (String smiles : firstGen) {
            List<String> otherGen = new ArrayList<>();
            for (IAtomContainer tautomer : Tautomers.generate(fromSmi(smiles),
                                                              options,
                                                              Tautomers.Order.SEQUENTIAL)) {
                otherGen.add(toSmi(tautomer));
            }
            Collections.sort(otherGen);
            Assertions.assertEquals(firstGen, otherGen);
        }
    }

    int countTautomers(Iterable<IAtomContainer> stream) throws CDKException {
        int count = 0;
        for (IAtomContainer container : stream)
            count++;
        return count;
    }


    void dumpTautomers(Iterable<IAtomContainer> stream) throws CDKException {
        List<String> actual = new ArrayList<>();
        for (IAtomContainer container : stream)
            actual.add(toSmi(container) + " " + MolecularFormulaManipulator.getString(MolecularFormulaManipulator.getMolecularFormula(container)));
        System.err.println(actual.size() + " tautomers (first 50):");
        System.err.println(actual.subList(0, Math.min(50, actual.size())).stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining("\n")));
    }


    IAtomContainer fromSmi(String smi) throws InvalidSmilesException {
        IAtomContainer mol = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
        Cycles.markRingAtomsAndBonds(mol);
        return mol;
    }

    String toSmi(IAtomContainer container) throws CDKException {
        for (IBond bond : container.bonds())
            if (bond.getOrder() == null)
                bond.setOrder(IBond.Order.QUADRUPLE);
        String res = new SmilesGenerator(SmiFlavor.Generic + SmiFlavor.AtomAtomMap).create(container);
        for (IBond bond : container.bonds())
            if (bond.getOrder() == IBond.Order.QUADRUPLE)
                bond.setOrder(null);
        return res;
    }

}