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

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
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
public class Jumbo46CMLFragmentsTest extends CDKTestCase {

    @Test
    public void testAtomId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assert.assertEquals("a1", atom.getID());
    }

    @Test
    public void testAtomId3() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(3, mol.getAtomCount());
        IAtom atom = mol.getAtom(1);
        Assert.assertEquals("a2", atom.getID());
    }

    @Test
    public void testAtomElementType3() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1' elementType='C'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assert.assertEquals("C", atom.getSymbol());
    }

    @Test
    public void testBond() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assert.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assert.assertEquals("a1", atom1.getID());
        Assert.assertEquals("a2", atom2.getID());
    }

    @Test
    public void testBond4() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' bondID='b1 b2'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(3, mol.getAtomCount());
        Assert.assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assert.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assert.assertEquals("a1", atom1.getID());
        Assert.assertEquals("a2", atom2.getID());
        Assert.assertEquals("b2", mol.getBond(1).getID());
    }

    @Test
    public void testBond5() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' order='1 1'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(3, mol.getAtomCount());
        Assert.assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assert.assertEquals(2, bond.getAtomCount());
        Assert.assertEquals(IBond.Order.SINGLE, bond.getOrder());
        bond = mol.getBond(1);
        Assert.assertEquals(2, bond.getAtomCount());
        Assert.assertEquals(IBond.Order.SINGLE, bond.getOrder());
    }

    @Test
    public void testBondAromatic() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2'/><bondArray atomRef1='a1' atomRef2='a2' order='A'/></molecule>";
        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assert.assertEquals(Order.SINGLE, bond.getOrder());
        Assert.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test
    public void testBondId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assert.assertEquals("b1", bond.getID());
    }

    @Test
    public void testList() throws Exception {
        String cmlString = "<list>"
                + "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>"
                + "<molecule id='m2'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>"
                + "</list>";

        IChemFile chemFile = parseCMLString(cmlString);
        checkForXMoleculeFile(chemFile, 2);
    }

    @Test
    public void testCoordinates2D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x2='0.0 0.1' y2='1.2 1.3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertNotNull(mol.getAtom(0).getPoint2d());
        Assert.assertNotNull(mol.getAtom(1).getPoint2d());
        Assert.assertNull(mol.getAtom(0).getPoint3d());
        Assert.assertNull(mol.getAtom(1).getPoint3d());
    }

    @Test
    public void testCoordinates3D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x3='0.0 0.1' y3='1.2 1.3' z3='2.1 2.5'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertNull(mol.getAtom(0).getPoint2d());
        Assert.assertNull(mol.getAtom(1).getPoint2d());
        Assert.assertNotNull(mol.getAtom(0).getPoint3d());
        Assert.assertNotNull(mol.getAtom(1).getPoint3d());
    }

    @Test
    public void testFractional3D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' xFract='0.0 0.1' yFract='1.2 1.3' zFract='2.1 2.5'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertNull(mol.getAtom(0).getPoint3d());
        Assert.assertNull(mol.getAtom(1).getPoint3d());
        Assert.assertNotNull(mol.getAtom(0).getFractionalPoint3d());
        Assert.assertNotNull(mol.getAtom(1).getFractionalPoint3d());
    }

    @Test
    public void testMissing2DCoordinates() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1' xy2='0.0 0.1'/><atom id='a2'/><atom id='a3' xy2='0.1 0.0'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtom(0);
        IAtom atom2 = mol.getAtom(1);
        IAtom atom3 = mol.getAtom(2);

        Assert.assertNotNull(atom1.getPoint2d());
        Assert.assertNull(atom2.getPoint2d());
        Assert.assertNotNull(atom3.getPoint2d());
    }

    @Test
    public void testMissing3DCoordinates() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1' xyz3='0.0 0.1 0.2'/><atom id='a2'/><atom id='a3' xyz3='0.1 0.0 0.2'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtom(0);
        IAtom atom2 = mol.getAtom(1);
        IAtom atom3 = mol.getAtom(2);

        Assert.assertNotNull(atom1.getPoint3d());
        Assert.assertNull(atom2.getPoint3d());
        Assert.assertNotNull(atom3.getPoint3d());
    }

    @Test
    public void testCrystal() throws Exception {
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
        Assert.assertEquals(4, crystal.getZ().intValue());
        Assert.assertEquals("Fm3m", crystal.getSpaceGroup());
        Assert.assertEquals(2, crystal.getAtomCount());
        Vector3d aaxis = crystal.getA();
        Assert.assertEquals(4.5, aaxis.x, 0.1);
        Assert.assertEquals(0.0, aaxis.y, 0.1);
        Assert.assertEquals(0.0, aaxis.z, 0.1);
        Vector3d baxis = crystal.getB();
        Assert.assertEquals(0.0, baxis.x, 0.1);
        Assert.assertEquals(4.5, baxis.y, 0.1);
        Assert.assertEquals(0.0, baxis.z, 0.1);
        Vector3d caxis = crystal.getC();
        Assert.assertEquals(0.0, caxis.x, 0.1);
        Assert.assertEquals(0.0, caxis.y, 0.1);
        Assert.assertEquals(4.5, caxis.z, 0.1);
    }

    @Test
    public void testMoleculeId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals("m1", mol.getID());
    }

    private IChemFile parseCMLString(String cmlString) throws Exception {
        IChemFile chemFile;
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        chemFile = reader.read(new org.openscience.cdk.ChemFile());
        return chemFile;
    }

    /**
     * Tests whether the file is indeed a single molecule file
     */
    private IAtomContainer checkForSingleMoleculeFile(IChemFile chemFile) {
        return checkForXMoleculeFile(chemFile, 1);
    }

    private IAtomContainer checkForXMoleculeFile(IChemFile chemFile, int numberOfMolecules) {
        Assert.assertNotNull(chemFile);

        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);

        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assert.assertNotNull(moleculeSet);

        Assert.assertEquals(moleculeSet.getAtomContainerCount(), numberOfMolecules);
        IAtomContainer mol = null;
        for (int i = 0; i < numberOfMolecules; i++) {
            mol = moleculeSet.getAtomContainer(i);
            Assert.assertNotNull(mol);
        }
        return mol;
    }

    private org.openscience.cdk.interfaces.ICrystal checkForCrystalFile(IChemFile chemFile) {
        Assert.assertNotNull(chemFile);

        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);

        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
        if (crystal != null) return crystal;

        // null crystal, try and find it in the set
        IAtomContainerSet set = model.getMoleculeSet();
        Assert.assertNotNull(set);
        for (IAtomContainer container : set.atomContainers()) {
            if (container instanceof ICrystal) {
                crystal = (ICrystal) container;
                return crystal;
            }
        }

        Assert.fail("no crystal could be found in the ChemModel");
        return crystal;
    }

}
