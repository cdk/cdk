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
package org.openscience.cdk.forcefield.mmff;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 */
public class MmffTest {

    private static SmilesParser smipar = null;
    private static Mmff         mmff   = null;

    @BeforeClass public static void setUp() {
        smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        mmff = new Mmff();
    }

    @AfterClass public static void tearDown() {
        smipar = null;
        mmff = null;
    }

    @Test public void tetrazoleAnion() throws InvalidSmilesException {
        IAtomContainer mol = loadSmi("[N-]1N=CN=N1");
        assertTrue(mmff.assignAtomTypes(mol));
        assertAtomTypes(mol, "N5M", "N5M", "C5", "N5M", "N5M", "HC");
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol, -0.25, -0.5875, 0.525, -0.5875, -0.25, 0.15);
        assertPartialChargeSum(mol, -1);
    }

    @Test public void tetrazole() throws InvalidSmilesException {
        IAtomContainer mol = loadSmi("N1N=CN=N1");
        assertTrue(mmff.assignAtomTypes(mol));
        assertAtomTypes(mol, "NPYL", "N5A", "C5B", "N5B", "N5A", "HPYL", "HC");
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol, 0.566, -0.7068, 0.366, -0.2272, -0.418, 0.27, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    @Test public void untypedAtom() throws InvalidSmilesException {
        IAtomContainer mol = loadSmi("[Se]C1C=CC=C1");
        assertFalse(mmff.assignAtomTypes(mol));
        assertAtomTypes(mol, "UNK", "CR", "C=C", "C=C", "C=C", "C=C", "HC", "HC", "HC", "HC", "HC");
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol, 0.0, 0.2764, -0.2882, -0.15, -0.15, -0.2882, 0.0, 0.15, 0.15, 0.15, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    @Test public void clearProps() throws InvalidSmilesException {
        IAtomContainer mol = loadSmi("o1cccc1");
        int sizeBefore = mol.getProperties().size();
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        mmff.clearProps(mol);
        assertThat(mol.getProperties().size(), is(sizeBefore));
    }

    @Test public void nitrobenzeneCovalent() throws InvalidSmilesException {
        IAtomContainer mol = loadSmi("c1ccccc1N(=O)=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertAtomTypes(mol, "CB", "CB", "CB", "CB", "CB", "CB", "NO2", "O2N", "O2N", "HC", "HC", "HC", "HC", "HC");
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol, -0.15, -0.15, -0.15, -0.15, -0.15, 0.133, 0.907, -0.52, -0.52, 0.15, 0.15, 0.15, 0.15, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    @Test public void nitrobenzeneChargeSeparated() throws InvalidSmilesException {
        IAtomContainer mol = loadSmi("c1ccccc1[N+](-[O-])=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertAtomTypes(mol, "CB", "CB", "CB", "CB", "CB", "CB", "NO2", "O2N", "O2N", "HC", "HC", "HC", "HC", "HC");
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol, -0.15, -0.15, -0.15, -0.15, -0.15, 0.133, 0.907, -0.52, -0.52, 0.15, 0.15, 0.15, 0.15, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3OH */
    @Test public void methanol() throws Exception {
        IAtomContainer mol = loadSmi("CO");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.28, -0.68, 0.0, 0.0, 0.0, 0.4);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3NH2 */
    @Test public void methylamine() throws Exception {
        IAtomContainer mol = loadSmi("CN");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.27, -0.99, 0.0, 0.0, 0.0, 0.36, 0.36);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3CN */
    @Test public void acetonitrile() throws Exception {
        IAtomContainer mol = loadSmi("CC#N");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.2, 0.357, -0.557, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3OCH3 */
    @Test public void dimethylether() throws Exception {
        IAtomContainer mol = loadSmi("COC");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.28, -0.56, 0.28, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3SH */
    @Test public void methanethiol() throws Exception {
        IAtomContainer mol = loadSmi("CS");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.23, -0.41, 0.0, 0.0, 0.0, 0.18);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3Cl */
    @Test public void chloromethane() throws Exception {
        IAtomContainer mol = loadSmi("CCl");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.29, -0.29, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - C2H6 */
    @Test public void ethane() throws Exception {
        IAtomContainer mol = loadSmi("CC");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3CONH2 (note wrong formula) */
    @Test public void acetamide() throws Exception {
        IAtomContainer mol = loadSmi("O=C(N)C");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.57, 0.569, -0.8, 0.061, 0.37, 0.37, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3COOH */
    @Test public void aceticAcid() throws Exception {
        IAtomContainer mol = loadSmi("CC(O)=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.061, 0.659, -0.65, -0.57, 0.0, 0.0, 0.0, 0.5);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - (CH3)2CO */
    @Test public void acetone() throws Exception {
        IAtomContainer mol = loadSmi("CC(=O)C");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.061, 0.447, -0.57, 0.061, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3COOCH3  */
    @Test public void methylacetate() throws Exception {
        IAtomContainer mol = loadSmi("O=C(OC)C");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.57, 0.659, -0.43, 0.28, 0.061, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - C6H6 */
    @Test public void benzene() throws Exception {
        IAtomContainer mol = loadSmi("c1ccccc1");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.15, -0.15, -0.15, -0.15, -0.15, -0.15, 0.15, 0.15, 0.15, 0.15, 0.15, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - C5H5N */
    @Test public void pyridine() throws Exception {
        IAtomContainer mol = loadSmi("C1=CC=NC=C1");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.15, -0.15, 0.16, -0.62, 0.16, -0.15, 0.15, 0.15, 0.15, 0.15, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - C6H5NH2 */
    @Test public void aniline() throws Exception {
        IAtomContainer mol = loadSmi("C1=CC=C(N)C=C1");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.15, -0.15, -0.15, 0.1, -0.9, -0.15, -0.15, 0.15, 0.15, 0.15, 0.4, 0.4, 0.15, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - imidazole */
    @Test public void imidazole() throws Exception {
        IAtomContainer mol = loadSmi("C=1NC=NC1");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.3016, 0.0332, 0.0365, -0.5653, 0.0772, 0.15, 0.27, 0.15, 0.15);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - H2O */
    @Test public void water() throws Exception {
        IAtomContainer mol = loadSmi("O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.86, 0.43, 0.43);
        assertPartialChargeSum(mol, 0);
    }

    /* TABLE V - CH3CO2- */
    @Test public void acetate() throws Exception {
        IAtomContainer mol = loadSmi("CC([O-])=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.106, 0.906, -0.9, -0.9, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, -1);
    }

    /* TABLE V - CH3NH3(+)  */
    @Test public void methanaminium() throws Exception {
        IAtomContainer mol = loadSmi("C[NH3+]");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.503, -0.853, 0.0, 0.0, 0.0, 0.45, 0.45, 0.45);
        assertPartialChargeSum(mol, +1);
    }

    /* TABLE V - Imidazolium(+) */
    @Test public void imidazolium() throws Exception {
        IAtomContainer mol = loadSmi("[nH+]1c[nH]cc1");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.7, 0.65, -0.7, 0.2, 0.2, 0.45, 0.15, 0.45, 0.15, 0.15);
        assertPartialChargeSum(mol, +1);
    }

    /* TABLE V - (-)O2C(CH2)6NH3(+) */
    @Test public void _7aminoheptanoicAcid() throws Exception {
        IAtomContainer mol = loadSmi("[NH3+]CCCCCCC([O-])=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.853, 0.503, 0.0, 0.0, 0.0, 0.0, -0.106, 0.906, -0.9, -0.9, 0.45, 0.45, 0.45, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    @Test public void ethoxyethane() throws Exception {
        IAtomContainer mol = loadSmi("CCOCC");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.0, 0.28, -0.56, 0.28, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertPartialChargeSum(mol, 0);
    }

    private IAtomContainer loadSmi(String smi) throws InvalidSmilesException {
        IAtomContainer mol = smipar.parseSmiles(smi);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        return mol;
    }

    /* [PO4]3- */
    @Test public void phosphate() throws Exception {
        IAtomContainer mol = loadSmi("[O-]P([O-])([O-])=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -1.075, 1.3, -1.075, -1.075, -1.075);
    }

    /* [HOPO3]2- */
    @Test public void hydrogenPhosphate() throws Exception {
        IAtomContainer mol = loadSmi("OP([O-])([O-])=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.7712, 1.3712, -1.033, -1.033, -1.033, 0.5);
    }

    /* [H2OPO3]- */
    @Test public void dihydrogenPhosphate() throws Exception {
        IAtomContainer mol = loadSmi("OP([O-])(O)=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.7712, 1.4424, -0.95, -0.7712, -0.95, 0.5, 0.5);
    }

    /* H3OPO3 */
    @Test public void phosphoricAcid() throws Exception {
        IAtomContainer mol = loadSmi("OP(O)(O)=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             -0.7712, 1.514, -0.7712, -0.7712, -0.7, 0.5, 0.5, 0.5);
    }
    
    /* SEYWUO - validation suite showing positive charge charging */
    @Test public void SEYWUO() throws Exception {
        IAtomContainer mol = loadSmi("[H]OC(=S)[N-][N+]1=C(N([H])[H])C([H])([H])N([H])C1=O");
        assertTrue(mmff.assignAtomTypes(mol));
        assertAtomTypes(mol,
                        "HOCS", "OC=S", "C=S", "S=C", "NM", "NCN+", "CNN+", "NCN+", "HNN+", "HNN+", "CR", "HC", "HC", "NC=O", "HNCO", "CONN", "O=CN");
        assertTrue(mmff.effectiveCharges(mol));
        assertPartialCharges(mol,
                             0.0, 0.0, -0.25, 0.0, -0.5, 0.25, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertTrue(mmff.partialCharges(mol));
        assertPartialCharges(mol,
                             0.4, -0.55, 0.31, -0.38, -0.179, -0.8364, 0.6038, -0.7544, 0.45, 0.45, 0.4051, 0.0, 0.0, -0.7301, 0.37, 1.011, -0.57);
    }

    private void assertAtomTypes(IAtomContainer mol, String... expected) {
        String[] actual = new String[mol.getAtomCount()];
        for (int i = 0; i < mol.getAtomCount(); i++)
            actual[i] = mol.getAtom(i).getAtomTypeName();
        assertArrayEquals(expected, actual);
    }

    private void assertPartialCharges(IAtomContainer mol, double... expected) {
        double[] actual = new double[mol.getAtomCount()];
        for (int i = 0; i < mol.getAtomCount(); i++)
            actual[i] = mol.getAtom(i).getCharge();
        assertArrayEquals(expected, actual, 0.001);
    }

    private void assertPartialChargeSum(IAtomContainer mol, double expected) {
        double actual = 0;
        for (int i = 0; i < mol.getAtomCount(); i++)
            actual += mol.getAtom(i).getCharge();
        assertEquals("Unexpected partial charge sum",
                     expected, actual, 0.001);
    }
}
