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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.ChemFile;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * This class tests the matching of atom types defined in the
 * CDK atom type list.
 *
 * @cdk.module test-core
 */
public class CDKAtomTypeMatcherFilesTest extends AbstractCDKAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    @Test
    public void testFile3() throws Exception {
        String filename = "data/cml/3.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "N.sp3", "C.sp2", "N.sp2", "O.sp3", "C.sp2", "C.sp2",
                "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 3141611
     */
    @Test
    public void testBug3141611() throws Exception {
        String filename = "data/mdl/error.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        String[] expectedTypes = {"C.sp3", "C.sp2", "O.sp2", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "P.ate", "O.sp2",
                "O.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testOla28() throws Exception {
        String filename = "data/cml/mol28.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "F", "C.sp2", "C.sp2", "C.sp2", "O.sp2", "C.sp3",
                "C.sp3", "C.sp3", "N.plus", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp2", "O.sp3", "C.sp2",
                "C.sp2", "C.sp2", "C.sp2", "C.sp2", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSmilesFiles() throws Exception {
        CDKAtomTypeMatcher atomTypeMatcher = CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance());

        // Read the first file
        String filename = "data/cml/smiles1.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        IAtomContainer mol1 = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        // Read the second file
        filename = "data/cml/smiles2.cml";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new CMLReader(ins);
        chemFile = (IChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        IAtomContainer mol2 = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomTypes(mol1);
        IAtomType[] types2 = atomTypeMatcher.findMatchingAtomTypes(mol2);
        for (int i = 0; i < mol1.getAtomCount(); i++) {
            Assert.assertNotNull("Atom typing in mol1 failed for atom " + (i + 1), types1[i]);
            Assert.assertNotNull("Atom typing in mol2 failed for atom " + (i + 1), types2[i]);
            Assert.assertEquals("Atom type mismatch for the " + (i + 1) + " atom", types1[i].getAtomTypeName(),
                    types2[i].getAtomTypeName());
        }
    }
}
