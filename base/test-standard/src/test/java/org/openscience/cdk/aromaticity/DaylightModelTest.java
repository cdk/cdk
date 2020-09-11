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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test the electron contribution using the Daylight aromatic model.
 *
 * @author John May
 * @cdk.module test-standard
 */
public class DaylightModelTest {

    private static ElectronDonation model = ElectronDonation.daylight();

    @Test
    public void benzene() throws Exception {
        test(addHydrogens(TestMoleculeFactory.makeBenzene()), 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void triazole() throws Exception {
        test(addHydrogens(TestMoleculeFactory.make123Triazole()), 1, 2, 1, 1, 1);
    }

    @Test
    public void furan() throws Exception {
        test(smiles("O1C=CC=C1"), 2, 1, 1, 1, 1);
    }

    @Test
    public void pyrrole() throws Exception {
        test(smiles("N1C=CC=C1"), 2, 1, 1, 1, 1);
    }

    @Test
    public void methylpyrrole() throws Exception {
        test(smiles("CN1C=CC=C1"), -1, 2, 1, 1, 1, 1);
    }

    @Test
    public void pyridine() throws Exception {
        test(smiles("C1=CC=NC=C1"), 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void hexamethylidenecyclohexane() throws Exception {
        test(smiles("C=C1C(=C)C(=C)C(=C)C(=C)C1=C"), -1, 1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1);
    }

    @Test
    public void cyclopentadienyl() throws Exception {
        test(smiles("[CH-]1C=CC=C1"), 2, 1, 1, 1, 1);
    }

    @Test
    public void pyridineOxide() throws Exception {
        test(addHydrogens(TestMoleculeFactory.makePyridineOxide()), 1, 1, 1, 1, 1, 1, -1);
    }

    @Test
    public void isoindole() throws Exception {
        test(smiles("C2=C1C=CC=CC1=CN2"), 1, 1, 1, 1, 1, 1, 1, 1, 2);
    }

    @Test
    public void azulene() throws Exception {
        test(addHydrogens(TestMoleculeFactory.makeAzulene()), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void indole() throws Exception {
        test(addHydrogens(TestMoleculeFactory.makeIndole()), 1, 1, 1, 1, 1, 1, 1, 1, 2);
    }

    @Test
    public void thiazole() throws Exception {
        test(addHydrogens(TestMoleculeFactory.makeThiazole()), 1, 1, 1, 2, 1);
    }

    @Test
    public void tetradehydrodecaline() throws Exception {
        test(smiles("C1CCC2=CC=CC=C2C1"), -1, -1, -1, 1, 1, 1, 1, 1, 1, -1);
    }

    @Test
    public void tropyliumcation() throws Exception {
        test(smiles("[CH+]1C=CC=CC=C1"), 0, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void tropone() throws Exception {
        test(smiles("O=C1C=CC=CC=C1"), -1, 0, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void porphyrine() throws Exception {
        test(smiles("N1C2=CC=C1C=C1C=CC(C=C3NC(C=C3)=CC3=NC(C=C3)=C2)=N1"), 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void quinone() throws Exception {
        test(smiles("O=C1C=CC(=O)C=C1"), -1, 0, 1, 1, 0, -1, 1, 1);
    }

    @Test
    public void cyclobutadiene() throws Exception {
        test(smiles("C1=CC=C1"), 1, 1, 1, 1);
    }

    @Test
    public void aminomethylpyridine() throws Exception {
        test(smiles("CC1=NC=CC=C1N"), -1, 1, 1, 1, 1, 1, 1, -1);
    }

    @Test
    public void indolizine() throws Exception {
        test(smiles("C1=CN2C=CC=CC2=C1"), 1, 1, 2, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void imidazothiazole() throws Exception {
        test(smiles("S1C=CN2C=CN=C12"), 2, 1, 1, 2, 1, 1, 1, 1);
    }

    // 1-oxide pyridine
    @Test
    public void oxidepyridine() throws Exception {
        test(smiles("O=N1=CC=CC=C1"), -1, 1, 1, 1, 1, 1, 1);
    }

    // 2-Pyridone
    @Test
    public void pyridinone() throws Exception {
        test(smiles("O=C1NC=CC=C1"), -1, 0, 2, 1, 1, 1, 1);
    }

    // 1H-oxepin-1-ium
    @Test
    public void oxepinium() throws Exception {
        test(smiles("[OH+]1C=CC=CC=C1"), -1, 1, 1, 1, 1, 1, 1);
    }

    // 4-oxo-1H-pyridin-1-ide
    @Test
    public void oxypyridinide() throws Exception {
        test(smiles("O=C1C=C[N-]C=C1"), -1, 0, 1, 1, 2, 1, 1);
    }

    @Test
    public void abnormalValence_carbon() throws Exception {
        test(smiles("[C]1[C][C][C][C]1"), -1, -1, -1, -1, -1);
    }

    @Test
    public void abnormalValence_nitrogen() throws Exception {
        test(smiles("[N]1[N][N][N][N]1"), -1, -1, -1, -1, -1);
    }

    @Test
    public void abnormalValence_phosphorus() throws Exception {
        test(smiles("[P]1[P][P][P][P]1"), -1, -1, -1, -1, -1);
    }

    /**
     * A 3 valent nitrogen cation should be aromatic, otherwise when we make it
     * lower case we can not convert it back.
     *
     * @cdk.bug 1332
     */
    @Test
    public void abnormalValence_nitrogenCation() throws Exception {
        test(smiles("C1=CC(=C([N+]=C1)N)[N+](=O)[O-]"), 1, 1, 1, 1, -1, 1, -1, -1, -1, -1);
    }

    @Test public void thiazole1oxide() throws Exception {
        test(smiles("O=S1C=NC=C1"), -1, 2, 1, 1, 1, 1);
    }

    static IAtomContainer addHydrogens(IAtomContainer container) throws CDKException {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder.getInstance(container.getBuilder()).addImplicitHydrogens(container);
        return container;
    }

    static IAtomContainer smiles(String smi) throws Exception {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
    }

    /** Check the electron contribution is the same as expected. */
    static void test(IAtomContainer m, int... expected) throws CDKException {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
        assertThat(model.contribution(m, new RingSearch(m)), is(expected));
    }
}
