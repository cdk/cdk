/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.atomtype;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * This class tests the matching of atom types defined in the
 * CDK atom type list, starting from SMILES strings.
 *
 * @cdk.module test-core
 */
public class CDKAtomTypeMatcherSMILESTest extends AbstractCDKAtomTypeTest {

    private static SmilesParser       smilesParser;
    private static CDKAtomTypeMatcher atomTypeMatcher;

    @BeforeClass
    public static void setup() {
        smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        atomTypeMatcher = CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance());
    }

    /**
     * @cdk.bug 2826961
     */
    @Test
    public void testIdenticalTypes() throws Exception {
        String smiles1 = "CN(C)CCC1=CNC2=C1C=C(C=C2)CC1NC(=O)OC1";
        String smiles2 = "CN(C)CCC1=CNc2c1cc(cc2)CC1NC(=O)OC1";

        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
        IAtomContainer mol2 = smilesParser.parseSmiles(smiles2);

        Assert.assertEquals(mol1.getAtomCount(), mol2.getAtomCount());
        Assert.assertEquals(mol1.getBondCount(), mol2.getBondCount());

        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        IAtomType[] types2 = atomTypeMatcher.findMatchingAtomTypes(mol2);
        for (int i = 0; i < mol1.getAtomCount(); i++) {
            Assert.assertEquals(types1[i].getAtomTypeName(), types2[i].getAtomTypeName());
        }
    }

    @Test
    public void testNitrogen() throws Exception {
        String smiles1 = "c1c2cc[NH]cc2nc1";

        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);

        Assert.assertEquals(9, mol1.getAtomCount());

        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        for (IAtomType type : types1) {
            Assert.assertNotNull(type.getAtomTypeName());
        }
    }

    @Test
    public void testNitrogen_SP2() throws Exception {
        String smiles1 = "c1c2cc[nH]cc2nc1";

        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);

        Assert.assertEquals(9, mol1.getAtomCount());

        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        for (IAtomType type : types1) {
            Assert.assertNotNull(type.getAtomTypeName());
        }
    }

    /**
     * @cdk.bug 2976054
     */
    @Test
    public void testAnotherNitrogen_SP2() throws Exception {
        String smiles1 = "c1cnc2s[cH][cH]n12";
        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);

        Assert.assertEquals(8, mol1.getAtomCount());
        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        for (IAtomType type : types1) {
            Assert.assertNotNull(type.getAtomTypeName());
        }
    }

    /**
     * @cdk.bug 1294
     */
    @Test
    public void testBug1294() throws Exception {
        String smiles1 = "c2c1ccccc1c[nH]2";
        String smiles2 = "C2=C1C=CC=CC1=CN2";

        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
        IAtomContainer mol2 = smilesParser.parseSmiles(smiles2);

        Assert.assertEquals(mol1.getAtomCount(), mol2.getAtomCount());
        Assert.assertEquals(mol1.getBondCount(), mol2.getBondCount());

        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        IAtomType[] types2 = atomTypeMatcher.findMatchingAtomTypes(mol2);
        for (int i = 0; i < mol1.getAtomCount(); i++) {
            Assert.assertEquals(types1[i].getAtomTypeName(), types2[i].getAtomTypeName());
        }
    }

    /**
     * @cdk.bug 3093644
     */
    @Test
    public void testBug3093644() throws Exception {
        String smiles1 = "[H]C5(CCC(N)=O)(C=1N=C(C=C4N=C(C(C)=C3[N-]C(C)(C2N=C(C=1(C))C(C)"
                + "(CCC(=O)NCC(C)O)C2([H])(CC(N)=O))C(C)(CC(N)=O)C3([H])(CCC(N)=O))"
                + "C(C)(CC(N)=O)C4([H])(CCC(N)=O))C5(C)(C)).[H][C-]([H])C3([H])(OC([H])"
                + "(N2C=NC=1C(N)=NC=NC=12)C([H])(O)C3([H])(O)).[Co+3]";

        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        for (IAtomType type : types1) {
            Assert.assertNotNull(type.getAtomTypeName());
        }
    }

    @Test
    public void testPlatinum4() throws Exception {
        String smiles1 = "Cl[Pt]1(Cl)(Cl)(Cl)NC2CCCCC2N1";

        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        Assert.assertEquals(13, mol1.getAtomCount());
        Assert.assertEquals("Pt.6", mol1.getAtom(1).getAtomTypeName());
    }

    @Test
    public void testPlatinum6() throws Exception {
        String smiles1 = "[Pt](Cl)(Cl)(N)N";

        IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        Assert.assertEquals(5, mol1.getAtomCount());
        Assert.assertEquals("Pt.4", mol1.getAtom(0).getAtomTypeName());
    }

    @Test
    public void testAmineOxide() throws Exception {
        String smiles = "CN(C)(=O)CCC=C2c1ccccc1CCc3ccccc23";

        IAtomContainer mol = smilesParser.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        Assert.assertEquals("N.oxide", mol.getAtom(1).getAtomTypeName());
    }

    @Test
    public void testYetAnotherNitrogen() throws Exception {
        String smiles = "CCCN1CC(CSC)CC2C1Cc3c[nH]c4cccc2c34";

        IAtomContainer mol = smilesParser.parseSmiles(smiles);
        IAtomType[] types = atomTypeMatcher.findMatchingAtomTypes(mol);
        for (IAtomType type : types) {
            Assert.assertNotNull(type.getAtomTypeName());
        }
    }

    @Test
    public void test4Sulphur() throws Exception {
        String smiles = "Br.Br.CS(CCC(N)C#N)C[C@H]1OC([C@H](O)[C@@H]1O)n2cnc3c(N)ncnc23";

        IAtomContainer mol = smilesParser.parseSmiles(smiles);
        IAtomType[] types = atomTypeMatcher.findMatchingAtomTypes(mol);
        for (IAtomType type : types) {
            Assert.assertNotNull(type.getAtomTypeName());
        }
    }

    @Test
    public void testTellaneLike() throws Exception {
        String smiles = "Clc1cccc(N2CCN(CCCCNC(=O)C3=Cc4ccccc4[Te]3)CC2)c1Cl";
        IAtomContainer mol = smilesParser.parseSmiles(smiles);
        for (IAtom atom : mol.atoms())
            Assert.assertNotSame("X", atom.getAtomTypeName());
    }

}
