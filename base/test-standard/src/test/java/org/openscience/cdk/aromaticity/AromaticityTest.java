/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
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

package org.openscience.cdk.aromaticity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.diff.AtomContainerDiff;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 * @cdk.module test-standard
 */
class AromaticityTest {

    private final Aromaticity cdk      = new Aromaticity(Aromaticity.Model.CDK_AtomTypes, Cycles.all());
    private final Aromaticity cdkExo   = new Aromaticity(ElectronDonation.cdkAllowingExocyclic(), Cycles.all());
    private final Aromaticity daylight = new Aromaticity(Aromaticity.Model.Daylight, Cycles.all());

    @Test
    void benzene() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC=CC=C1"))).size(), is(6));
        assertThat(daylight.findBonds(smiles("C1=CC=CC=C1")).size(), is(6));
    }

    @Test
    void furan() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC=CO1"))).size(), is(5));
        assertThat(daylight.findBonds(smiles("C1=CC=CO1")).size(), is(5));
    }

    @Test
    void quinone() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1C=CC(=O)C=C1"))).size(), is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=CC(=O)C=C1"))).size(), is(6));
        assertThat(daylight.findBonds(smiles("O=C1C=CC(=O)C=C1")).size(), is(0));
    }

    @Test
    void azulene() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC2=CC=CC=CC2=C1"))).size(), is(10));
        assertThat(daylight.findBonds(smiles("C1=CC2=CC=CC=CC2=C1")).size(), is(10));
    }

    // 4-oxo-1H-pyridin-1-ide
    @Test
    void oxypyridinide() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(), is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(), is(0));
        assertThat(daylight.findBonds(smiles("O=C1C=C[N-]C=C1")).size(), is(6));
    }

    // 2-Pyridone
    @Test
    void pyridinone() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1NC=CC=C1"))).size(), is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(), is(0));
        assertThat(daylight.findBonds(smiles("O=C1NC=CC=C1")).size(), is(6));
    }

    @Test
    void subset() throws Exception {
        assertThat(daylight.findBonds(smiles("[O-][Cu++]123([O-])CN4C=NC5=C4C(N=CN5)=[O+]1.O=S(=O)([OH+]2)[OH+]3"))
                .size(), is(5));
    }

    @Test
    void clearFlags_cyclobutadiene() throws Exception {
        IAtomContainer cyclobutadiene = smiles("c1ccc1");
        daylight.apply(cyclobutadiene);
        for (IBond bond : cyclobutadiene.bonds())
            Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        for (IAtom atom : cyclobutadiene.atoms())
            Assertions.assertFalse(atom.getFlag(IChemObject.AROMATIC));
    }

    @Test
    void clearFlags_quinone() throws Exception {
        IAtomContainer quinone = smiles("O=c1ccc(=O)cc1");
        daylight.apply(quinone);
        for (IBond bond : quinone.bonds())
            Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        for (IAtom atom : quinone.atoms())
            Assertions.assertFalse(atom.getFlag(IChemObject.AROMATIC));
    }

    @Test
    void azuleneMarkAll() throws Exception {
        IAtomContainer mol = smiles("C1=CC2=CC=CC=CC2=C1");
        Aromaticity.apply(Aromaticity.Model.Daylight, mol);
        Assertions.assertEquals(mol.getAtomCount(), aromAtomCount(mol));
        Assertions.assertEquals(mol.getBondCount()-1, aromBondCount(mol));
    }

    @Test
    void fullereneC60() throws Exception {
        IAtomContainer mol = smiles("c12c3c4c5c1c1c6c7c2c2c8c3c3c9c4c4c%10c5c5c1c1c6c6c%11c7c2c2c7c8c3c3c8c9c4c4c9c%10c5c5c1c1c6c6c%11c2c2c7c3c3c8c4c4c9c5c1c1c6c2c3c41");
        Aromaticity.apply(Aromaticity.Model.Daylight, mol);
        Assertions.assertEquals(mol.getAtomCount(), aromAtomCount(mol));
        Assertions.assertEquals(mol.getBondCount(), aromBondCount(mol));
    }

    @Test
    void fullereneC60mod() throws Exception {
        IAtomContainer mol = smiles("O=C(O)CCNC12c3c4c5c6c7c8c(c9c%10c1c1c3c3c%11c4c4c5c5c7c7c%12c8c8c9c9c%10c%10c1c1c3c3c%11c%11c4c4c5c7c5c7c%12c8c8c9c9c%10c1c1c3c3c%11c4c5c4c7c8c9c1c34)C62 CHEMBL1207486");
        Aromaticity.apply(Aromaticity.Model.Daylight, mol);
        Assertions.assertEquals(58, aromAtomCount(mol));
        Assertions.assertEquals(85, aromBondCount(mol));
    }

    static int aromAtomCount(IAtomContainer mol) {
        int count = 0;
        for (IAtom atom : mol.atoms())
            if (atom.isAromatic()) count++;
        return count;
    }

    static int aromBondCount(IAtomContainer mol) {
        int count = 0;
        for (IBond bond : mol.bonds())
            if (bond.isAromatic()) count++;
        return count;
    }

    @Test
    void validSum() throws Exception {
        // aromatic
        Assertions.assertTrue(Aromaticity.checkHuckelSum(2));
        Assertions.assertTrue(Aromaticity.checkHuckelSum(6));
        Assertions.assertTrue(Aromaticity.checkHuckelSum(10));
        Assertions.assertTrue(Aromaticity.checkHuckelSum(14));
        Assertions.assertTrue(Aromaticity.checkHuckelSum(18));

        // anti-aromatic
        Assertions.assertFalse(Aromaticity.checkHuckelSum(4));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(8));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(12));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(16));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(20));

        // other numbers
        Assertions.assertFalse(Aromaticity.checkHuckelSum(0));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(1));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(3));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(5));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(7));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(9));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(11));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(13));
        Assertions.assertFalse(Aromaticity.checkHuckelSum(15));
    }

    @Test
    void electronSum() {
        assertThat(Aromaticity.electronSum(new int[]{0, 1, 2, 3, 0}, new int[]{1, 1, 1, 1}, new int[]{0, 1, 2, 3}),
                   is(4));
    }

    /**
     * @cdk.bug 736
     */
    @Test
    void ensureConsistentRepresentation() throws Exception {
        IAtomContainer a = smiles("C1=CC2=CC3=CC4=C(C=CC=C4)C=C3C=C2C=C1");
        IAtomContainer b = smiles("c1cc2cc3cc4c(cccc4)cc3cc2cc1");
        Aromaticity arom = new Aromaticity(Aromaticity.Model.Daylight,
                                           Cycles.all());
        arom.apply(a);
        arom.apply(b);
        Assertions.assertTrue(AtomContainerDiff.diff(a, b).isEmpty());
    }

    /**
     * @cdk.bug 976
     */
    @Test
    void outOfMemoryExceptionInitialCycles() throws Exception {
        IAtomContainer atomContainer = smiles("C1=CC2=CC3=C1C1=C4C=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=" +
                "C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC5=C(C=C1)C1=" +
                "C(C=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C(C=C1)" +
                "C1=CC=C(C=C1)C1=CC=C(C=C1)C1=CC=C2C=C1)C345");
        Aromaticity aromaticity = new Aromaticity(ElectronDonation.daylight(), Cycles.or(Cycles.all(), Cycles.essential()));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
        Intractable error = Assertions.assertThrows(Intractable.class,
                                                    () -> {
            	aromaticity.apply(atomContainer);
        });
        Assertions.assertNotNull(error);
        Assertions.assertTrue(error.getMessage().contains("Too many relevant cycles cycles"));
    }

    static IAtomContainer smiles(String smi) throws Exception {
        IAtomContainer mol = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
        Cycles.markRingAtomsAndBonds(mol);
        return mol;
    }

    static IAtomContainer type(IAtomContainer molecule) throws Exception {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        return molecule;
    }
}
