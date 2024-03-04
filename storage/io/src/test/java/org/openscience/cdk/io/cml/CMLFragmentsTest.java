/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
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
 *  */
package org.openscience.cdk.io.cml;

import java.io.ByteArrayInputStream;

import javax.vecmath.Vector3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.io.CMLReader;

/**
 * Atomic tests for reading CML documents. All tested CML strings are valid CML 2,
 * as can be determined in cdk/src/org.openscience.cdk/io/cml/cmlTestFramework.xml.
 *
 * @cdk.module test-io
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 */
class CMLFragmentsTest extends CDKTestCase {

    @Test
    void testAtomId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("a1", atom.getID());
    }

    @Test
    void testAtomId2() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1</stringArray></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("a1", atom.getID());
    }

    @Test
    void testAtomId3() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        IAtom atom = mol.getAtom(1);
        Assertions.assertEquals("a2", atom.getID());
    }

    @Test
    void testAtomElementType() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='elementType'>C</stringArray></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
    }

    @Test
    void testAtomElementType2() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'><string builtin='elementType'>C</string></atom></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
    }

    @Test
    void testAtomElementType3() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1' elementType='C'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
    }

    @Test
    void test2dCoord() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'><coordinate2 builtin='xy2'>84 138</coordinate2></atom></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertNull(atom.getPoint3d());
        Assertions.assertNotNull(atom.getPoint2d());
        Assertions.assertEquals(84, (int) atom.getPoint2d().x);
        Assertions.assertEquals(138, (int) atom.getPoint2d().y);
    }

    @Test
    void test2dCoord2() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1</stringArray><floatArray builtin='x2'>2.0833</floatArray><floatArray builtin='y2'>4.9704</floatArray></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertNull(atom.getPoint3d());
        Assertions.assertNotNull(atom.getPoint2d());
        Assertions.assertTrue(2.0833 == atom.getPoint2d().x);
        Assertions.assertTrue(4.9704 == atom.getPoint2d().y);
    }

    @Test
    void testBond() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assertions.assertEquals("a1", atom1.getID());
        Assertions.assertEquals("a2", atom2.getID());
    }

    @Test
    void testBond2() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1 a2</stringArray></atomArray><bondArray><stringArray builtin='atomRefs'>a1</stringArray><stringArray builtin='atomRefs'>a2</stringArray></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assertions.assertEquals("a1", atom1.getID());
        Assertions.assertEquals("a2", atom2.getID());
    }

    @Test
    void testBond3() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1 a2</stringArray></atomArray><bondArray><bond id='b1'><string builtin='atomRef'>a1</string><string builtin='atomRef'>a2</string></bond></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assertions.assertEquals("a1", atom1.getID());
        Assertions.assertEquals("a2", atom2.getID());
    }

    @Test
    void testBond4() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' bondID='b1 b2'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assertions.assertEquals("a1", atom1.getID());
        Assertions.assertEquals("a2", atom2.getID());
        Assertions.assertEquals("b2", mol.getBond(1).getID());
    }

    @Test
    void testBond5() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' order='1 1'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
        bond = mol.getBond(1);
        Assertions.assertEquals(2, bond.getAtomCount());
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
    }

    @Test
    void testBondAromatic() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2'/><bondArray atomRef1='a1' atomRef2='a2' order='A'/></molecule>";
        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
        Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
    }

    @Test
    void testBondId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals("b1", bond.getID());
    }

    @Test
    void testList() throws Exception {
        String cmlString = "<list>"
                + "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>"
                + "<molecule id='m2'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>"
                + "</list>";

        IChemFile chemFile = parseCMLString(cmlString);
        checkForXMoleculeFile(chemFile, 2);
    }

    @Test
    void testCoordinates2D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x2='0.0 0.1' y2='1.2 1.3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertNotNull(mol.getAtom(0).getPoint2d());
        Assertions.assertNotNull(mol.getAtom(1).getPoint2d());
        Assertions.assertNull(mol.getAtom(0).getPoint3d());
        Assertions.assertNull(mol.getAtom(1).getPoint3d());
    }

    @Test
    void testCoordinates3D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x3='0.0 0.1' y3='1.2 1.3' z3='2.1 2.5'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertNull(mol.getAtom(0).getPoint2d());
        Assertions.assertNull(mol.getAtom(1).getPoint2d());
        Assertions.assertNotNull(mol.getAtom(0).getPoint3d());
        Assertions.assertNotNull(mol.getAtom(1).getPoint3d());
    }

    @Test
    void testFractional3D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' xFract='0.0 0.1' yFract='1.2 1.3' zFract='2.1 2.5'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertNull(mol.getAtom(0).getPoint3d());
        Assertions.assertNull(mol.getAtom(1).getPoint3d());
        Assertions.assertNotNull(mol.getAtom(0).getFractionalPoint3d());
        Assertions.assertNotNull(mol.getAtom(1).getFractionalPoint3d());
    }

    @Test
    void testMissing2DCoordinates() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1' xy2='0.0 0.1'/><atom id='a2'/><atom id='a3' xy2='0.1 0.0'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtom(0);
        IAtom atom2 = mol.getAtom(1);
        IAtom atom3 = mol.getAtom(2);

        Assertions.assertNotNull(atom1.getPoint2d());
        Assertions.assertNull(atom2.getPoint2d());
        Assertions.assertNotNull(atom3.getPoint2d());
    }

    @Test
    void testMissing3DCoordinates() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1' xyz3='0.0 0.1 0.2'/><atom id='a2'/><atom id='a3' xyz3='0.1 0.0 0.2'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtom(0);
        IAtom atom2 = mol.getAtom(1);
        IAtom atom3 = mol.getAtom(2);

        Assertions.assertNotNull(atom1.getPoint3d());
        Assertions.assertNull(atom2.getPoint3d());
        Assertions.assertNotNull(atom3.getPoint3d());
    }

    @Test
    void testCrystal() throws Exception {
        StringBuilder cmlStringB = new StringBuilder("  <molecule id=\"m1\">\n");
        cmlStringB.append("    <crystal z=\"4\">\n");
        cmlStringB
                .append("      <scalar id=\"sc1\" title=\"a\" errorValue=\"0.001\" units=\"units:angstrom\">4.500</scalar>\n");
        cmlStringB
                .append("      <scalar id=\"sc2\" title=\"b\" errorValue=\"0.001\" units=\"units:angstrom\">4.500</scalar>\n");
        cmlStringB
                .append("      <scalar id=\"sc3\" title=\"c\" errorValue=\"0.001\" units=\"units:angstrom\">4.500</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc4\" title=\"alpha\" units=\"units:degrees\">90</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc5\" title=\"beta\" units=\"units:degrees\">90</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc6\" title=\"gamma\" units=\"units:degrees\">90</scalar>\n");
        cmlStringB.append("      <symmetry id=\"s1\" spaceGroup=\"Fm3m\"/>\n");
        cmlStringB.append("    </crystal>\n");
        cmlStringB.append("    <atomArray>\n");
        cmlStringB.append("      <atom id=\"a1\" elementType=\"Na\" formalCharge=\"1\" xyzFract=\"0.0 0.0 0.0\"\n");
        cmlStringB.append("        xy2=\"+23.1 -21.0\"></atom>\n");
        cmlStringB
                .append("      <atom id=\"a2\" elementType=\"Cl\" formalCharge=\"-1\" xyzFract=\"0.5 0.0 0.0\"></atom>\n");
        cmlStringB.append("    </atomArray>\n");
        cmlStringB.append("  </molecule>\n");

        IChemFile chemFile = parseCMLString(cmlStringB.toString());
        org.openscience.cdk.interfaces.ICrystal crystal = checkForCrystalFile(chemFile);
        Assertions.assertEquals(4, crystal.getZ().intValue());
        Assertions.assertEquals("Fm3m", crystal.getSpaceGroup());
        Assertions.assertEquals(2, crystal.getAtomCount());
        Vector3d aaxis = crystal.getA();
        Assertions.assertEquals(4.5, aaxis.x, 0.1);
        Assertions.assertEquals(0.0, aaxis.y, 0.1);
        Assertions.assertEquals(0.0, aaxis.z, 0.1);
        Vector3d baxis = crystal.getB();
        Assertions.assertEquals(0.0, baxis.x, 0.1);
        Assertions.assertEquals(4.5, baxis.y, 0.1);
        Assertions.assertEquals(0.0, baxis.z, 0.1);
        Vector3d caxis = crystal.getC();
        Assertions.assertEquals(0.0, caxis.x, 0.1);
        Assertions.assertEquals(0.0, caxis.y, 0.1);
        Assertions.assertEquals(4.5, caxis.z, 0.1);
    }

    @Test
    void testMoleculeId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals("m1", mol.getID());
    }

    @Test
    void testBondArrayCML1() throws Exception {
        String cml1String = "  <molecule title=\"NSC 25\">\n"
                + "   <atomArray>\n"
                + "    <stringArray builtin=\"atomId\">a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13</stringArray>\n"
                + "    <stringArray builtin=\"elementType\">Br N C C C C C C C O C C C</stringArray>\n"
                + "    <integerArray builtin=\"formalCharge\">0 0 0 0 0 0 0 0 0 0 0 0 0</integerArray>\n"
                + "    <floatArray builtin=\"x2\">-2.350500 0.850500 -2.160500 -1.522400 -2.798500 -1.522400 -2.798500 -2.160500 -0.889500 -1.259400 0.850500 0.850500 2.880500</floatArray>\n"
                + "    <floatArray builtin=\"y2\">-2.129900 0.767900 0.769900 0.401900 0.401900 -0.334900 -0.334900 -0.703000 0.767900 1.408800 -0.652000 2.088000 0.767900</floatArray>\n"
                + "   </atomArray>\n" + "   <bondArray>\n"
                + "    <stringArray builtin=\"atomRef\">a2 a2 a2 a2 a3 a3 a4 a4 a5 a6 a7 a9</stringArray>\n"
                + "    <stringArray builtin=\"atomRef\">a9 a11 a12 a13 a5 a4 a6 a9 a7 a8 a8 a10</stringArray>\n"
                + "    <stringArray builtin=\"order\">1 1 1 1 2 1 2 1 1 1 2 2</stringArray>\n" + "   </bondArray>\n"
                + "  </molecule>\n";

        IChemFile chemFile = parseCMLString(cml1String);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(13, mol.getAtomCount());
        Assertions.assertEquals(12, mol.getBondCount());
    }

    private IChemFile parseCMLString(String cmlString) throws Exception {
        IChemFile chemFile;
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        chemFile = reader.read(new org.openscience.cdk.ChemFile());
        reader.close();
        return chemFile;
    }

    /**
     * Tests whether the file is indeed a single molecule file
     */
    private IAtomContainer checkForSingleMoleculeFile(IChemFile chemFile) {
        return checkForXMoleculeFile(chemFile, 1);
    }

    private IAtomContainer checkForXMoleculeFile(IChemFile chemFile, int numberOfMolecules) {
        Assertions.assertNotNull(chemFile);

        Assertions.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);

        Assertions.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assertions.assertNotNull(moleculeSet);

        Assertions.assertEquals(moleculeSet.getAtomContainerCount(), numberOfMolecules);
        IAtomContainer mol = null;
        for (int i = 0; i < numberOfMolecules; i++) {
            mol = moleculeSet.getAtomContainer(i);
            Assertions.assertNotNull(mol);
        }
        return mol;
    }

    private org.openscience.cdk.interfaces.ICrystal checkForCrystalFile(IChemFile chemFile) {
        Assertions.assertNotNull(chemFile);

        Assertions.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);

        Assertions.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
        if (crystal != null) return crystal;

        // null crystal, try and find it in the set
        IAtomContainerSet set = model.getMoleculeSet();
        Assertions.assertNotNull(set);
        for (IAtomContainer container : set.atomContainers()) {
            if (container instanceof ICrystal) {
                crystal = (ICrystal) container;
                return crystal;
            }
        }

        Assertions.fail("no crystal could be found in the ChemModel");
        return crystal;
    }

}
