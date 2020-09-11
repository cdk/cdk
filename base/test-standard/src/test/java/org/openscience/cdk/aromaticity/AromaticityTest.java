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

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.diff.AtomContainerDiff;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-standard
 */
public class AromaticityTest {

    private final Aromaticity cdk      = new Aromaticity(ElectronDonation.cdk(), Cycles.all());
    private final Aromaticity cdkExo   = new Aromaticity(ElectronDonation.cdkAllowingExocyclic(), Cycles.all());
    private final Aromaticity daylight = new Aromaticity(ElectronDonation.daylight(), Cycles.all());

    @Test
    public void benzene() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC=CC=C1"))).size(), is(6));
        assertThat(daylight.findBonds(smiles("C1=CC=CC=C1")).size(), is(6));
    }

    @Test
    public void furan() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC=CO1"))).size(), is(5));
        assertThat(daylight.findBonds(smiles("C1=CC=CO1")).size(), is(5));
    }

    @Test
    public void quinone() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1C=CC(=O)C=C1"))).size(), is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=CC(=O)C=C1"))).size(), is(6));
        assertThat(daylight.findBonds(smiles("O=C1C=CC(=O)C=C1")).size(), is(0));
    }

    @Test
    public void azulene() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC2=CC=CC=CC2=C1"))).size(), is(10));
        assertThat(daylight.findBonds(smiles("C1=CC2=CC=CC=CC2=C1")).size(), is(10));
    }

    // 4-oxo-1H-pyridin-1-ide
    @Test
    public void oxypyridinide() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(), is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(), is(0));
        assertThat(daylight.findBonds(smiles("O=C1C=C[N-]C=C1")).size(), is(6));
    }

    // 2-Pyridone
    @Test
    public void pyridinone() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1NC=CC=C1"))).size(), is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(), is(0));
        assertThat(daylight.findBonds(smiles("O=C1NC=CC=C1")).size(), is(6));
    }

    @Test
    public void subset() throws Exception {
        assertThat(daylight.findBonds(smiles("[O-][Cu++]123([O-])CN4C=NC5=C4C(N=CN5)=[O+]1.O=S(=O)([OH+]2)[OH+]3"))
                .size(), is(5));
    }

    @Test
    public void clearFlags_cyclobutadiene() throws Exception {
        IAtomContainer cyclobutadiene = smiles("c1ccc1");
        daylight.apply(cyclobutadiene);
        for (IBond bond : cyclobutadiene.bonds())
            assertFalse(bond.getFlag(CDKConstants.ISAROMATIC));
        for (IAtom atom : cyclobutadiene.atoms())
            assertFalse(atom.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test
    public void clearFlags_quinone() throws Exception {
        IAtomContainer quinone = smiles("O=c1ccc(=O)cc1");
        daylight.apply(quinone);
        for (IBond bond : quinone.bonds())
            assertFalse(bond.getFlag(CDKConstants.ISAROMATIC));
        for (IAtom atom : quinone.atoms())
            assertFalse(atom.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test
    public void validSum() throws Exception {
        // aromatic
        assertTrue(Aromaticity.validSum(2));
        assertTrue(Aromaticity.validSum(6));
        assertTrue(Aromaticity.validSum(10));
        assertTrue(Aromaticity.validSum(14));
        assertTrue(Aromaticity.validSum(18));

        // anti-aromatic
        assertFalse(Aromaticity.validSum(4));
        assertFalse(Aromaticity.validSum(8));
        assertFalse(Aromaticity.validSum(12));
        assertFalse(Aromaticity.validSum(16));
        assertFalse(Aromaticity.validSum(20));

        // other numbers
        assertFalse(Aromaticity.validSum(0));
        assertFalse(Aromaticity.validSum(1));
        assertFalse(Aromaticity.validSum(3));
        assertFalse(Aromaticity.validSum(5));
        assertFalse(Aromaticity.validSum(7));
        assertFalse(Aromaticity.validSum(9));
        assertFalse(Aromaticity.validSum(11));
        assertFalse(Aromaticity.validSum(13));
        assertFalse(Aromaticity.validSum(15));
    }

    @Test
    public void electronSum() throws Exception {
        assertThat(Aromaticity.electronSum(new int[]{0, 1, 2, 3, 0}, new int[]{1, 1, 1, 1}, new int[]{0, 1, 2, 3}),
                   is(4));
    }

    /**
     * @cdk.bug 736
     */
    @Test
    public void ensureConsistentRepresentation() throws Exception {
        IAtomContainer a = smiles("C1=CC2=CC3=CC4=C(C=CC=C4)C=C3C=C2C=C1");
        IAtomContainer b = smiles("c1cc2cc3cc4c(cccc4)cc3cc2cc1");
        Aromaticity arom = new Aromaticity(ElectronDonation.daylight(),
                                           Cycles.all());
        arom.apply(a);
        arom.apply(b);
        assertTrue(AtomContainerDiff.diff(a, b).isEmpty());
    }

    static IAtomContainer smiles(String smi) throws Exception {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
    }

    static IAtomContainer type(IAtomContainer molecule) throws Exception {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        return molecule;
    }
}
