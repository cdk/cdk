/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.ChemFile;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * This class tests the matching of atom types defined in the
 * CDK atom type list.
 *
 */
class CDKAtomTypeMatcherFilesTest extends AbstractCDKAtomTypeTest {

    private static final Map<String, Integer> testedAtomTypes = new HashMap<>();

    @Test
    void testFile3() throws Exception {
        // 3.cml
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer m = builder.newAtomContainer();
        IAtom a1 = m.newAtom(IElement.C, 0);
        IAtom a2 = m.newAtom(IElement.N, 1);
        IAtom a3 = m.newAtom(IElement.C, 0);
        IAtom a4 = m.newAtom(IElement.N, 1);
        IAtom a6 = m.newAtom(IElement.C, 1);
        IAtom a10 = m.newAtom(IElement.N, 0);
        IAtom a5 = m.newAtom(IElement.O, 1);
        IAtom a7 = m.newAtom(IElement.C, 1);
        IAtom a9 = m.newAtom(IElement.C, 1);
        IAtom a8 = m.newAtom(IElement.C, 1);
        m.newBond(a2, a1, IBond.Order.DOUBLE);
        m.newBond(a1, a3);
        m.newBond(a1, a4);
        m.newBond(a4, a5);
        m.newBond(a3, a6, IBond.Order.DOUBLE);
        m.newBond(a10, a3);
        m.newBond(a6, a7);
        m.newBond(a9, a10, IBond.Order.DOUBLE);
        m.newBond(a7, a8, IBond.Order.DOUBLE);
        m.newBond(a8, a9);
        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "N.sp3", "C.sp2", "N.sp2", "O.sp3", "C.sp2", "C.sp2",
                "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, m);
    }

    /**
     * @cdk.bug 3141611
     */
    @Test
    void testBug3141611() throws Exception {
        String filename = "error.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());

        // test the resulting ChemFile content
        Assertions.assertNotNull(chemFile);
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        String[] expectedTypes = {"C.sp3", "C.sp2", "O.sp2", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "P.ate", "O.sp2",
                "O.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testGermaniumDativeBond() throws Exception {
        String filename = "dativeBond.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());

        // test the resulting ChemFile content
        Assertions.assertNotNull(chemFile);
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        String[] expectedTypes = {"X", "X"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    void testOla28() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer m = builder.newAtomContainer();
        IAtom a1 = m.newAtom(IElement.C, 1);
        IAtom a2 = m.newAtom(IElement.C, 0);
        IAtom a3 = m.newAtom(IElement.C, 1);
        IAtom a4 = m.newAtom(IElement.C, 1);
        IAtom a5 = m.newAtom(IElement.F, 0);
        IAtom a6 = m.newAtom(IElement.C, 0);
        IAtom a7 = m.newAtom(IElement.C, 1);
        IAtom a8 = m.newAtom(IElement.C, 0);
        IAtom a9 = m.newAtom(IElement.O, 0);
        IAtom a10 = m.newAtom(IElement.C, 2);
        IAtom a11 = m.newAtom(IElement.C, 2);
        IAtom a12 = m.newAtom(IElement.C, 2);
        IAtom a13 = m.newAtom(IElement.N, 1);
        a13.setFormalCharge(1);  IAtom a14 = m.newAtom(IElement.C, 2);
        IAtom a15 = m.newAtom(IElement.C, 2);
        IAtom a16 = m.newAtom(IElement.C, 2);
        IAtom a17 = m.newAtom(IElement.C, 2);
        IAtom a18 = m.newAtom(IElement.C, 0);
        IAtom a19 = m.newAtom(IElement.C, 0);
        IAtom a20 = m.newAtom(IElement.O, 1);
        IAtom a21 = m.newAtom(IElement.C, 1);
        IAtom a22 = m.newAtom(IElement.C, 1);
        IAtom a23 = m.newAtom(IElement.C, 1);
        IAtom a24 = m.newAtom(IElement.C, 1);
        IAtom a25 = m.newAtom(IElement.C, 0);
        IAtom a26 = m.newAtom(IElement.Cl, 0);
        m.newBond(a1, a2, IBond.Order.DOUBLE);
        m.newBond(a3, a1);
        m.newBond(a4, a2);
        m.newBond(a2, a5);
        m.newBond(a6, a3, IBond.Order.DOUBLE);
        m.newBond(a7, a4, IBond.Order.DOUBLE);
        m.newBond(a6, a7);
        m.newBond(a8, a6);
        m.newBond(a8, a9, IBond.Order.DOUBLE);
        m.newBond(a10, a8);
        m.newBond(a11, a10);
        m.newBond(a12, a11);
        m.newBond(a13, a12);
        m.newBond(a13, a14);
        m.newBond(a15, a13);
        m.newBond(a16, a14);
        m.newBond(a17, a15);
        m.newBond(a18, a16);
        m.newBond(a18, a17);
        m.newBond(a18, a19);
        m.newBond(a18, a20);
        m.newBond(a19, a21, IBond.Order.DOUBLE);
        m.newBond(a19, a22);
        m.newBond(a21, a23);
        m.newBond(a22, a24, IBond.Order.DOUBLE);
        m.newBond(a23, a25, IBond.Order.DOUBLE);
        m.newBond(a24, a25);
        m.newBond(a25, a26);

        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "F", "C.sp2", "C.sp2", "C.sp2", "O.sp2", "C.sp3",
                "C.sp3", "C.sp3", "N.plus", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp2", "O.sp3", "C.sp2",
                "C.sp2", "C.sp2", "C.sp2", "C.sp2", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, m);
    }

    @Test
    void testSmilesFiles() throws Exception {
        CDKAtomTypeMatcher atomTypeMatcher = CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance());

        // smiles1.cml
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol1 = builder.newAtomContainer();
        {
            IAtom a1 = mol1.newAtom(IElement.C, 3);
            IAtom a2 = mol1.newAtom(IElement.N, 0);
            IAtom a3 = mol1.newAtom(IElement.C, 3);
            IAtom a4 = mol1.newAtom(IElement.C, 2);
            IAtom a5 = mol1.newAtom(IElement.C, 2);
            IAtom a6 = mol1.newAtom(IElement.C, 0);
            IAtom a7 = mol1.newAtom(IElement.C, 1);
            IAtom a8 = mol1.newAtom(IElement.C, 0);
            IAtom a9 = mol1.newAtom(IElement.N, 1);
            IAtom a10 = mol1.newAtom(IElement.C, 0);
            IAtom a11 = mol1.newAtom(IElement.C, 1);
            IAtom a12 = mol1.newAtom(IElement.C, 1);
            IAtom a13 = mol1.newAtom(IElement.C, 0);
            IAtom a14 = mol1.newAtom(IElement.C, 1);
            IAtom a15 = mol1.newAtom(IElement.C, 2);
            IAtom a16 = mol1.newAtom(IElement.C, 1);
            IAtom a17 = mol1.newAtom(IElement.N, 1);
            IAtom a18 = mol1.newAtom(IElement.C, 2);
            IAtom a19 = mol1.newAtom(IElement.C, 0);
            IAtom a20 = mol1.newAtom(IElement.O, 0);
            IAtom a21 = mol1.newAtom(IElement.O, 0);
            mol1.newBond(a2, a1);
            mol1.newBond(a3, a2);
            mol1.newBond(a4, a2);
            mol1.newBond(a5, a4);
            mol1.newBond(a6, a5);
            mol1.newBond(a7, a6, IBond.Order.DOUBLE);
            mol1.newBond(a8, a6);
            mol1.newBond(a9, a7);
            mol1.newBond(a8, a10, IBond.Order.DOUBLE);
            mol1.newBond(a11, a8);
            mol1.newBond(a10, a9);
            mol1.newBond(a12, a10);
            mol1.newBond(a13, a11, IBond.Order.DOUBLE);
            mol1.newBond(a12, a14, IBond.Order.DOUBLE);
            mol1.newBond(a14, a13);
            mol1.newBond(a15, a13);
            mol1.newBond(a16, a15);
            mol1.newBond(a17, a16);
            mol1.newBond(a18, a16);
            mol1.newBond(a19, a17);
            mol1.newBond(a18, a20);
            mol1.newBond(a21, a19, IBond.Order.DOUBLE);
            mol1.newBond(a20, a19);
        }

        // smiles2.cml
        IAtomContainer mol2 = builder.newAtomContainer();
        {
            IAtom a1 = mol2.newAtom(IElement.C, 3);
            IAtom a2 = mol2.newAtom(IElement.N, 0);
            IAtom a3 = mol2.newAtom(IElement.C, 3);
            IAtom a4 = mol2.newAtom(IElement.C, 2);
            IAtom a5 = mol2.newAtom(IElement.C, 2);
            IAtom a6 = mol2.newAtom(IElement.C, 0);
            IAtom a7 = mol2.newAtom(IElement.C, 1);
            IAtom a8 = mol2.newAtom(IElement.C, 0);
            IAtom a9 = mol2.newAtom(IElement.N, 1);
            IAtom a10 = mol2.newAtom(IElement.C, 0);
            IAtom a11 = mol2.newAtom(IElement.C, 1);
            IAtom a12 = mol2.newAtom(IElement.C, 1);
            IAtom a13 = mol2.newAtom(IElement.C, 0);
            IAtom a14 = mol2.newAtom(IElement.C, 1);
            IAtom a15 = mol2.newAtom(IElement.C, 2);
            IAtom a16 = mol2.newAtom(IElement.C, 1);
            IAtom a17 = mol2.newAtom(IElement.N, 1);
            IAtom a18 = mol2.newAtom(IElement.C, 2);
            IAtom a19 = mol2.newAtom(IElement.C, 0);
            IAtom a20 = mol2.newAtom(IElement.O, 0);
            IAtom a21 = mol2.newAtom(IElement.O, 0);
            mol2.newBond(a2, a1);
            mol2.newBond(a3, a2);
            mol2.newBond(a4, a2);
            mol2.newBond(a5, a4);
            mol2.newBond(a6, a5);
            mol2.newBond(a7, a6, IBond.Order.DOUBLE).setIsAromatic(true);
            mol2.newBond(a8, a6).setIsAromatic(true);
            mol2.newBond(a9, a7).setIsAromatic(true);
            mol2.newBond(a8, a10).setIsAromatic(true);
            mol2.newBond(a11, a8).setIsAromatic(true);
            mol2.newBond(a10, a9).setIsAromatic(true);
            mol2.newBond(a12, a10).setIsAromatic(true);
            mol2.newBond(a13, a11).setIsAromatic(true);
            mol2.newBond(a12, a14).setIsAromatic(true);
            mol2.newBond(a14, a13);
            mol2.newBond(a15, a13);
            mol2.newBond(a16, a15);
            mol2.newBond(a17, a16);
            mol2.newBond(a18, a16);
            mol2.newBond(a19, a17);
            mol2.newBond(a18, a20);
            mol2.newBond(a21, a19, IBond.Order.DOUBLE);
            mol2.newBond(a20, a19);
        }

        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        IAtomType[] types2 = atomTypeMatcher.findMatchingAtomTypes(mol2);
        for (int i = 0; i < mol1.getAtomCount(); i++) {
            Assertions.assertNotNull(types1[i], "Atom typing in mol1 failed for atom " + (i + 1));
            Assertions.assertNotNull(types2[i], "Atom typing in mol2 failed for atom " + (i + 1));
            Assertions.assertEquals(types1[i].getAtomTypeName(), types2[i].getAtomTypeName(), "Atom type mismatch for the " + (i + 1) + " atom");
        }
    }
}
