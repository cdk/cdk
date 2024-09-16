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

package org.openscience.cdk.depict;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbbreviationsTest {

    private static final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());

    static IAtomContainer smi(String smi) throws Exception {
        return smipar.parseSmiles(smi);
    }

    @Test
    void potassiumCarbonate() throws Exception {
        IAtomContainer mol = smi("[K+].[O-]C(=O)[O-].[K+]");
        Abbreviations factory = new Abbreviations();
        factory.add("[K+].[O-]C(=O)[O-].[K+] K2CO3");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("K2CO3"));
        assertThat(sgroups.get(0).getAtoms().size(), is(6));
    }

    @Test
    void autoContractPEt3() throws Exception {
        IAtomContainer mol = smi("CCP(CC)CC");
        Abbreviations factory = new Abbreviations();
        factory.add("*CC Et");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON)
               .with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Et3P"));
        assertThat(sgroups.get(0).getAtoms().size(), is(7));
    }

    @Test
    void autoContractPEt3_off() throws Exception {
        IAtomContainer mol = smi("CCP(CC)CC");
        Abbreviations factory = new Abbreviations();
        factory.add("*CC Et");
        factory.without(Abbreviations.Option.ALLOW_SINGLETON)
               .with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(3));
        assertThat(sgroups.get(0).getSubscript(), is("Et"));
        assertThat(sgroups.get(1).getSubscript(), is("Et"));
        assertThat(sgroups.get(2).getSubscript(), is("Et"));
        factory.with(Abbreviations.Option.ALLOW_SINGLETON)
               .without(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(3));
        assertThat(sgroups.get(0).getSubscript(), is("Et"));
        assertThat(sgroups.get(1).getSubscript(), is("Et"));
        assertThat(sgroups.get(2).getSubscript(), is("Et"));
    }

    @Test
    void autoContractNMe2() throws Exception {
        IAtomContainer mol = smi("CCCCCCN(C)C");
        // no abbreviations define but cal still contract
        Abbreviations factory = new Abbreviations();
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("NMe2"));
    }

    @Test
    void autoContractCHEt3() throws Exception {
        IAtomContainer mol = smi("CCC(CC)CC");
        Abbreviations factory = new Abbreviations();
        factory.add("*CC Et");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Et3CH"));
        assertThat(sgroups.get(0).getAtoms().size(), is(7));
    }

    @Test
    void autoContractEt2CO() throws Exception {
        IAtomContainer mol = smi("C(=O)(CC)CC");
        Abbreviations factory = new Abbreviations();
        factory.add("*CC Et");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Et2CO"));
        assertThat(sgroups.get(0).getAtoms().size(), is(6));
    }

    @Test
    void autoContractCEt2_NonTerminal() throws Exception {
        IAtomContainer mol = smi("CCCCCCCC(CC)CC");
        Abbreviations factory = new Abbreviations();
        factory.add("*CC Et");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("CHEt2"));
    }

    @Test
    void testBiphenyl() throws Exception {
        IAtomContainer mol = smi("c1ccccc1c1ccccc1");
        Abbreviations factory = new Abbreviations();
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Ph2"));
        assertThat(sgroups.get(0).getAtoms().size(), is(12));
        factory.without(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        assertThat(sgroups.get(0).getSubscript(), is("Ph"));
        assertThat(sgroups.get(1).getSubscript(), is("Ph"));
    }

    @Test
    void testNEt2() throws Exception {
        IAtomContainer mol = smi("CCN(CC)N(CC)CC");
        Abbreviations factory = new Abbreviations();
        factory.add("*CC Et");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("(NEt2)2"));
        assertThat(sgroups.get(0).getAtoms().size(), is(10));
        factory.without(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        assertThat(sgroups.get(0).getSubscript(), is("NEt2"));
        assertThat(sgroups.get(1).getSubscript(), is("NEt2"));
    }

    @Test
    void testNMe2() throws Exception {
        IAtomContainer mol = smi("CN(C)N(C)C");
        Abbreviations factory = new Abbreviations();
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("(NMe2)2"));
        assertThat(sgroups.get(0).getAtoms().size(), is(6));
        factory.without(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        assertThat(sgroups.get(0).getSubscript(), is("NMe2"));
        assertThat(sgroups.get(1).getSubscript(), is("NMe2"));
    }

    @Test
    void phenyl() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("CCCCCCC(c1ccccc1)(c1ccccc1)c1ccccc1");
        factory.add("*c1ccccc1 Ph");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(3));
        assertThat(sgroups.get(0).getSubscript(), is("Ph"));
        assertThat(sgroups.get(0).getAtoms().size(), is(6));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
        assertThat(sgroups.get(1).getSubscript(), is("Ph"));
        assertThat(sgroups.get(1).getAtoms().size(), is(6));
        assertThat(sgroups.get(1).getBonds().size(), is(1));
        assertThat(sgroups.get(2).getSubscript(), is("Ph"));
        assertThat(sgroups.get(2).getAtoms().size(), is(6));
        assertThat(sgroups.get(2).getBonds().size(), is(1));
    }

    @Test
    void PhMgCl() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1[Mg]Cl");
        factory.add("*c1ccccc1 Ph");
        factory.add("*[Mg]Cl MgCl");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("PhMgCl"));
    }


    @Test
    void PhNnBu3() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1[Sn](CCCC)(CCCC)CCCC");
        factory.add("*c1ccccc1 Ph");
        factory.add("*CCCC nBu");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("nBu3SnPh"));
    }

    @Test
    void PhNnBu3_noSingleton() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1[Sn](CCCC)(CCCC)CCCC");
        factory.add("*c1ccccc1 Ph");
        factory.add("*CCCC nBu");
        factory.without(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("Ph"));
        assertThat(sgroups.get(1).getSubscript(), is("SnnBu3"));
    }

    @Test
    void PhCl() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Clc1ccccc1");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("PhCl"));
    }

    @Test
    void PhCl_keepCl() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Clc1ccccc1");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("PhCl"));

        // block the chlorine atom
        IAtom chlorineAtom = mol.getAtom(0);
        assertEquals(IAtom.Cl, chlorineAtom.getAtomicNumber());
        List<Sgroup> sgroups2 = factory.generate(mol,
                                                 Collections.singletonMap(chlorineAtom, 1));
        assertThat(sgroups2.size(), is(1));
        assertThat(sgroups2.get(0).getSubscript(), is("Ph"));
    }

    @Test
    void PhCl_keepOneC() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Clc1ccccc1");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("PhCl"));

        // block the Ph contraction
        IAtom carbonAtom = mol.getAtom(5);
        assertEquals(IAtom.C, carbonAtom.getAtomicNumber());
        List<Sgroup> sgroups3 = factory.generate(mol,
                                                 Collections.singletonMap(carbonAtom, 1));
        assertThat(sgroups3.size(), is(0));

        // all carbon atoms in same group, only Ph comes out
        Map<IAtom,Integer> atomSets = new HashMap<>();
        for (IAtom atom : mol.atoms()) {
            if (atom.getAtomicNumber() == IAtom.C)
                atomSets.put(atom, 1);
        }
        List<Sgroup> sgroups4 = factory.generate(mol,
                                                 atomSets);
        assertThat(sgroups4.size(), is(1));
        assertThat(sgroups4.get(0).getSubscript(), is("Ph"));

        // all atoms in same group, PhCl comes out
        for (IAtom atom : mol.atoms())
            atomSets.put(atom, 1);
        List<Sgroup> sgroups5 = factory.generate(mol,
                                                 atomSets);
        assertThat(sgroups5.size(), is(1));
        assertThat(sgroups5.get(0).getSubscript(), is("PhCl"));
    }

    @Test
    void PhCl_keepAllC() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Clc1ccccc1");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("PhCl"));

        // all carbon atoms in same group, only Ph comes out
        Map<IAtom,Integer> atomSets = new HashMap<>();
        for (IAtom atom : mol.atoms()) {
            if (atom.getAtomicNumber() == IAtom.C)
                atomSets.put(atom, 1);
        }
        List<Sgroup> sgroups4 = factory.generate(mol,
                                                 atomSets);
        assertThat(sgroups4.size(), is(1));
        assertThat(sgroups4.get(0).getSubscript(), is("Ph"));

        // all atoms in same group, PhCl comes out
        for (IAtom atom : mol.atoms())
            atomSets.put(atom, 1);
        List<Sgroup> sgroups5 = factory.generate(mol,
                                                 atomSets);
        assertThat(sgroups5.size(), is(1));
        assertThat(sgroups5.get(0).getSubscript(), is("PhCl"));
    }

    @Test
    void PhCl_keepAll() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Clc1ccccc1");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("PhCl"));

        // all atoms in same group, PhCl comes out
        Map<IAtom,Integer> atomSets = new HashMap<>();
        for (IAtom atom : mol.atoms())
            atomSets.put(atom, 1);
        List<Sgroup> sgroups5 = factory.generate(mol,
                                                 atomSets);
        assertThat(sgroups5.size(), is(1));
        assertThat(sgroups5.get(0).getSubscript(), is("PhCl"));
    }

    @Test
    void Ph2_keepAll() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1c1ccccc1");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("Ph2"));

        // all atoms in same group, Ph2 still comes out
        Map<IAtom,Integer> atomSets = new HashMap<>();
        for (IAtom atom : mol.atoms())
            atomSets.put(atom, 1);
        List<Sgroup> sgroups5 = factory.generate(mol,
                                                 atomSets);
        assertThat(sgroups5.size(), is(1));
        assertThat(sgroups5.get(0).getSubscript(), is("Ph2"));
    }

    @Test
    void Ph2_keepOne() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1c1ccccc1");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        sgroups.sort(Comparator.comparing(sgroup -> sgroup.getAtoms().size()));
        assertThat(sgroups.get(0).getSubscript(), is("Ph2"));

        // all atoms in same group, only one Ph comes out
        Map<IAtom,Integer> atomSets = new HashMap<>();
        atomSets.put(mol.getAtom(0), 1);
        List<Sgroup> sgroups5 = factory.generate(mol,
                                                 atomSets);
        assertThat(sgroups5.size(), is(1));
        assertThat(sgroups5.get(0).getSubscript(), is("Ph"));
    }

    @Test
    void testDoNotAbbreviateStereochemistry() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("C(C)[C@@H](C1=CC=CC=C1)NC(=O)C1=C(C(=NC2=CC=CC=C12)C1=CC=CC=C1)CN1CCC(CC1)O");
        factory.add("*CC Et");
        factory.add("*c1ccccc1 Ph");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(3));
        assertThat(sgroups.get(0).getSubscript(), is("Ph"));
        assertThat(sgroups.get(1).getSubscript(), is("Ph"));
        assertThat(sgroups.get(2).getSubscript(), is("Et"));

        // removing stereo allows further contraction
        mol.setStereoElements(Collections.emptyList());
        sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        assertThat(sgroups.get(0).getSubscript(), is("Ph"));
        assertThat(sgroups.get(1).getSubscript(), is("CHEtPh"));
    }

    // Don't generate NiPr
    @Test
    void avoidAmbiguity() throws Exception {
        String smi = "C1CCCCC1=NC(C)C";
        Abbreviations factory = new Abbreviations();
        factory.add("*C(C)C iPr");
        IAtomContainer mol = smi(smi);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("iPr"));
    }

    // we need brackets here, so Ni (nickle) does not accidnetly come out
    @Test
    void testNiPr2_needBrackets() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("CCCCN(C(C)C)C(C)C");
        factory.add("*C(C)C iPr");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("N(iPr)2"));
    }

    @Test
    void avoid_CHCH2() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1C=C");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(0));
    }

    @Test
    void avoidPhContraction() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*c1ccccc1 Ph");
        IAtomContainer mol = smi("c1ccccc1CCc1cscc1");
        factory.apply(mol);
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        assertEquals(0, sgroups.size());
    }

    @Test
    void avoidPhContraction2() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*c1ccccc1 Ph");
        IAtomContainer mol = smi("c1ccccc1CBr");
        factory.apply(mol);
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        assertEquals(0, sgroups.size());
    }

    @Test
    void testCOEt() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("C1CCCCC1C(=O)CC");
        factory.add("*CC Et");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("COEt"));
    }


    @Test
    void MeMgCl() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("C[Mg]Cl");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        // could be better
        assertThat(sgroups.get(0).getSubscript(), is("MeMgCl"));
    }

    @Test
    void phenylShouldNotMatchBenzene() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1");
        factory.add("*c1ccccc1 Ph");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(0));
    }

    @Test
    void testAc() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("[nH]1ccc2c1cc(OC(=O)C)cc2");
        factory.add("*OC(=O)C OAc");
        factory.add("*C(=O)C Ac");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        Assertions.assertEquals("OAc", sgroups.get(0).getSubscript());
        Map<IAtom,Integer> aset = new HashMap<>();
        aset.put(mol.getAtom(7), 1); // -O-
        aset.put(mol.getAtom(8), 2); // CH
        aset.put(mol.getAtom(9), 2); // =O
        aset.put(mol.getAtom(10), 2); // Me
        sgroups = factory.generate(mol, aset);
        assertThat(sgroups.size(), is(1));
        Assertions.assertEquals("Ac", sgroups.get(0).getSubscript());
    }

    @Test
    void TFASaltDisconnected() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1c1ccccc1.FC(F)(F)C(=O)O");
        factory.add("*C(F)(F)F CF3");
        factory.add("*C(=O)O CO2H");
        factory.add("FC(F)(F)C(=O)O TFA");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("TFA"));
    }

    @Test
    void TFASaltConnected() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("FC(F)(F)C(=O)O");
        factory.add("*C(F)(F)F CF3");
        factory.add("*C(=O)O CO2H");
        factory.add("FC(F)(F)C(=O)O TFA");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        assertThat(sgroups.get(0).getSubscript(),
                   CoreMatchers.anyOf(is("CF3"), is("CO2H")));
        assertThat(sgroups.get(1).getSubscript(),
                   CoreMatchers.anyOf(is("CF3"), is("CO2H")));
        assertThat(sgroups.get(1).getSubscript(),
                   CoreMatchers.not(is(sgroups.get(0).getSubscript())));
    }

    @Test
    void DcmAndTfa() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("ClCCl.FC(F)(F)C(=O)O");
        factory.add("ClCCl DCM");
        factory.add("FC(F)(F)C(=O)O TFA");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("TFA·DCM"));
    }

    @Test
    void DcmAndTfaNoSingleFrag() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("ClCCl.FC(F)(F)C(=O)O");
        factory.add("ClCCl DCM");
        factory.add("FC(F)(F)C(=O)O TFA");
        factory.without(Abbreviations.Option.ALLOW_SINGLETON);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("DCM"));
    }

    @Test
    void avoidOverZealousAbbreviations() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Clc1ccccc1");
        factory.add("*c1ccccc1 Ph");
        assertThat(factory.apply(mol), is(0));
    }

    @Test
    void phenylShouldNotMatchC4H6() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Oc1ccc(O)cc1");
        factory.add("*c1ccccc1 Ph");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(0));
    }

    @Test
    void phenylShouldAbbreviateExplicitHydrogens() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("CCCCc1ccc([H])cc1");
        factory.add("*c1ccccc1 Ph");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Ph"));
        assertThat(sgroups.get(0).getAtoms().size(), is(7));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
    }

    // some SMARTS foo here :-)
    @Test
    void phenylShouldMatchKekuleForm() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("CCCCC1=CC=CC=C1");
        factory.add("*c1ccccc1 Ph");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Ph"));
        assertThat(sgroups.get(0).getAtoms().size(), is(6));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
    }

    // SMARTS foo not that good
    @Test
    void nitroGroups() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("O=N(=O)CCCC[N+]([O-])=O");
        factory.add("*N(=O)(=O) NO2");
        factory.add("*[N+]([O-])(=O) NO2");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        assertThat(sgroups.get(0).getSubscript(), is("NO2"));
        assertThat(sgroups.get(0).getAtoms().size(), is(3));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
        assertThat(sgroups.get(1).getSubscript(), is("NO2"));
        assertThat(sgroups.get(1).getAtoms().size(), is(3));
        assertThat(sgroups.get(1).getBonds().size(), is(1));
    }

    @Test
    void abbreviationsHavePriority() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1CCC");
        factory.add("*CCC Pr");
        factory.add("*CC Et");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Pr"));
    }

    @Test
    void dontOverwriteExistingSgroups() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*CCC Bu");
        IAtomContainer mol = smi("c1ccccc1CCC");
        Sgroup sgroup = new Sgroup();
        sgroup.addAtom(mol.getAtom(6));
        sgroup.addAtom(mol.getAtom(7));
        sgroup.addAtom(mol.getAtom(8));
        sgroup.setType(SgroupType.CtabAbbreviation);
        sgroup.setSubscript("n-Bu");
        mol.setProperty(CDKConstants.CTAB_SGROUPS, Collections.singletonList(sgroup));
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(0));
    }

    @Test
    void NHBocFromHeteroCollapse() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*C(=O)OC(C)(C)C Boc");
        IAtomContainer mol = smi("c1ccccc1NC(=O)OC(C)(C)C");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("NHBoc"));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
        assertThat(sgroups.get(0).getAtoms().size(), is(8));
    }

    @Test
    void NHBocFromHeteroCollapseExplicitH() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*C(=O)OC(C)(C)C Boc");
        IAtomContainer mol = smi("c1ccccc1N([H])C(=O)OC(C)(C)C");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("NHBoc"));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
        assertThat(sgroups.get(0).getAtoms().size(), is(9));
    }

    @Test
    void NBocClFromHeteroCollapseExplicit() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*C(=O)OC(C)(C)C Boc");
        IAtomContainer mol = smi("c1ccccc1N(Cl)C(=O)OC(C)(C)C");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("NClBoc"));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
        assertThat(sgroups.get(0).getAtoms().size(), is(9));
    }

    @Test
    void NBoc2FromHeteroCollapse() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*C(=O)OC(C)(C)C Boc");
        IAtomContainer mol = smi("c1cc2ccccc2cc1N(C(=O)OC(C)(C)C)C(=O)OC(C)(C)C");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("NBoc2"));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
        assertThat(sgroups.get(0).getAtoms().size(), is(15));
    }

    @Test
    void iPrFromHeteroCollapse() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*C(C)C iPr");
        IAtomContainer mol = smi("[CH3:27][CH:19]([CH3:28])[C:20]1=[N:26][C:23](=[CH:22][S:21]1)[C:24](=[O:25])O");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("iPr"));
        assertThat(sgroups.get(0).getBonds().size(), is(1));
        assertThat(sgroups.get(0).getAtoms().size(), is(3));
    }

    @Test
    void NBocFromHeteroCollapse() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*C(=O)OC(C)(C)C Boc");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_LINKERS);
        IAtomContainer mol = smi("c1cc2ccccc2ccn1C(=O)OC(C)(C)C");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("NBoc"));
        assertThat(sgroups.get(0).getBonds().size(), is(2));
        assertThat(sgroups.get(0).getAtoms().size(), is(8));
    }

    @Test
    void SO3minusFromHeteroCollapseNone() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("*S(=O)(=O)[O-] SO3-");
        IAtomContainer mol = smi("c1ccccc1N(S(=O)(=O)[O-])S(=O)(=O)[O-]");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(2));
        assertThat(sgroups.get(0).getSubscript(), is("SO3-"));
        assertThat(sgroups.get(1).getSubscript(), is("SO3-"));
    }

    @Test
    void hclSaltOfEdci() throws Exception {
        Abbreviations factory = new Abbreviations();
        factory.add("CCN=C=NCCCN(C)C EDCI");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        IAtomContainer mol = smi("CCN=C=NCCCN(C)C.Cl");
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("EDCI·HCl"));
    }

    @Test
    void SnCl2() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("Cl[Sn]Cl");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("SnCl2"));
    }

    @Test
    void HOOH() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("OO");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("HOOH"));
    }

    @Test
    void shouldNotPartiallyContractKetene() throws Exception {
        Abbreviations factory = new Abbreviations();
        IAtomContainer mol = smi("c1ccccc1CC=C=O");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(0));
    }

    @Test
    void testAttachments() throws Exception {
        IAtomContainer mol = smi("*C(=O)OCCC");
        Abbreviations factory = new Abbreviations();
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_LINKERS);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("CO"));
        assertThat(sgroups.get(0).getBonds().size(), is(2));
    }


    @Test
    void multipleDisconnectedAbbreviations() throws Exception {
        String smi = "ClCCl.Cl[Pd]Cl.[Fe+2].c1ccc(P([c-]2cccc2)c2ccccc2)cc1.c1ccc(P([c-]2cccc2)c2ccccc2)cc1";
        Abbreviations factory = new Abbreviations();
        factory.add("ClCCl DCM");
        factory.add("Cl[Pd]Cl.[Fe+2].c1ccc(P([c-]2cccc2)c2ccccc2)cc1.c1ccc(P([c-]2cccc2)c2ccccc2)cc1 Pd(dppf)Cl2");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        IAtomContainer mol = smi(smi);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Pd(dppf)Cl2·DCM"));
    }

    @Test
    void multipleDisconnectedAbbreviations2() throws Exception {
        String smi = "ClCCl.Cl[Pd]Cl.[Fe+2].c1ccc(P([c-]2cccc2)c2ccccc2)cc1.c1ccc(P([c-]2cccc2)c2ccccc2)cc1";
        Abbreviations factory = new Abbreviations();
        factory.add("Cl[Pd]Cl.[Fe+2].c1ccc(P([c-]2cccc2)c2ccccc2)cc1.c1ccc(P([c-]2cccc2)c2ccccc2)cc1 Pd(dppf)Cl2");
        factory.add("Cl[Pd]Cl PdCl2");
        factory.with(Abbreviations.Option.ALLOW_SINGLETON);
        IAtomContainer mol = smi(smi);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(1));
        assertThat(sgroups.get(0).getSubscript(), is("Pd(dppf)Cl2"));
    }

    @Test
    void keepIsotopes() throws Exception {
        String smi = "c1ccccc1C#[15N]";
        Abbreviations factory = new Abbreviations();
        factory.add("*C#N CN");
        factory.with(Abbreviations.Option.AUTO_CONTRACT_TERMINAL);
        factory.with(Abbreviations.Option.AUTO_CONTRACT_HETERO);
        IAtomContainer mol = smi(smi);
        List<Sgroup> sgroups = factory.generate(mol);
        assertThat(sgroups.size(), is(0));
    }

    @Test
    void loadFromFile() throws Exception {
        Abbreviations factory = new Abbreviations();
        assertThat(factory.loadFromFile("obabel_superatoms.smi"), is(27));
        assertThat(factory.loadFromFile("/org/openscience/cdk/depict/obabel_superatoms.smi"), is(27));
    }
}
