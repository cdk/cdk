/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test.io.cml;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.*;
import java.io.*;
import junit.framework.*;
import java.util.Iterator;

/**
 * Atomic tests for the reading CML documents. All tested CML strings are valid CML 2,
 * as can be determined in cdk/src/org/openscience/cdk/test/io/cml/cmlTestFramework.xml.
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class CMLFragmentsTest extends TestCase {

    public CMLFragmentsTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(CMLFragmentsTest.class);
    }

    public void testAtomId() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("a1", atom.getID());
    }

    
    public void testAtomId2() {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1</stringArray></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("a1", atom.getID());
    }
    
    public void testAtomId3() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(3, mol.getAtomCount());
        Atom atom = mol.getAtomAt(1);
        assertEquals("a2", atom.getID());
    }

    
    public void testAtomElementType() {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='elementType'>C</stringArray></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("C", atom.getSymbol());
    }
    
    public void testAtomElementType2() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'><string builtin='elementType'>C</string></atom></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("C", atom.getSymbol());
    }
    
    public void testAtomElementType3() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1' elementType='C'/></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("C", atom.getSymbol());
    }
    
    public void test2dCoord() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'><coordinate2 builtin='xy2'>84 138</coordinate2></atom></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertNull(atom.getPoint3D());
        assertNotNull(atom.getPoint2D());
        assertEquals(84, (int)atom.getX2D());
        assertEquals(138, (int)atom.getY2D());
    }
    
    public void test2dCoord2() {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1</stringArray><floatArray builtin='x2'>2.0833</floatArray><floatArray builtin='y2'>4.9704</floatArray></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertNull(atom.getPoint3D());
        assertNotNull(atom.getPoint2D());
        assertTrue(2.0833 == atom.getX2D());
        assertTrue(4.9704 == atom.getY2D());
    }
    
    public void testBond() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        Bond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        Atom atom1 = bond.getAtomAt(0);
        Atom atom2 = bond.getAtomAt(1);
        assertEquals("a1", atom1.getID());
        assertEquals("a2", atom2.getID());
    }

    public void testBond2() {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1 a2</stringArray></atomArray><bondArray><stringArray builtin='atomRefs'>a1</stringArray><stringArray builtin='atomRefs'>a2</stringArray></bondArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        Bond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        Atom atom1 = bond.getAtomAt(0);
        Atom atom2 = bond.getAtomAt(1);
        assertEquals("a1", atom1.getID());
        assertEquals("a2", atom2.getID());
    }
    
    public void testBond3() {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1 a2</stringArray></atomArray><bondArray><bond id='b1'><string builtin='atomRef'>a1</string><string builtin='atomRef'>a2</string></bond></bondArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        Bond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        Atom atom1 = bond.getAtomAt(0);
        Atom atom2 = bond.getAtomAt(1);
        assertEquals("a1", atom1.getID());
        assertEquals("a2", atom2.getID());
    }
    
    public void testBond4() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' bondID='b1 b2'/></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(3, mol.getAtomCount());
        assertEquals(2, mol.getBondCount());
        Bond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        Atom atom1 = bond.getAtomAt(0);
        Atom atom2 = bond.getAtomAt(1);
        assertEquals("a1", atom1.getID());
        assertEquals("a2", atom2.getID());
        assertEquals("b2", mol.getBondAt(1).getID());
    }

    
    public void testBondId() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        Bond bond = mol.getBondAt(0);
        assertEquals("b1", bond.getID());
    }
    
    public void testList() {
        String cmlString = 
          "<list>" + 
          "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>" +
          "<molecule id='m2'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>" +
          "</list>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        checkForXMoleculeFile(chemFile, 2);
    }

    public void testCoordinates2D() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x2='0.0 0.1' y2='1.2 1.3'/></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(2, mol.getAtomCount());
        assertNotNull(mol.getAtomAt(0).getPoint2D());
        assertNotNull(mol.getAtomAt(1).getPoint2D());
        assertNull(mol.getAtomAt(0).getPoint3D());
        assertNull(mol.getAtomAt(1).getPoint3D());
    }
  
    public void testCoordinates3D() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x3='0.0 0.1' y3='1.2 1.3' z3='2.1 2.5'/></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(2, mol.getAtomCount());
        assertNull(mol.getAtomAt(0).getPoint2D());
        assertNull(mol.getAtomAt(1).getPoint2D());
        assertNotNull(mol.getAtomAt(0).getPoint3D());
        assertNotNull(mol.getAtomAt(1).getPoint3D());
    }
    
    public void testFractional3D() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' xFract='0.0 0.1' yFract='1.2 1.3' zFract='2.1 2.5'/></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(2, mol.getAtomCount());
        assertNull(mol.getAtomAt(0).getPoint3D());
        assertNull(mol.getAtomAt(1).getPoint3D());
        assertNotNull(mol.getAtomAt(0).getFractionalPoint3D());
        assertNotNull(mol.getAtomAt(1).getFractionalPoint3D());
    }
    
    public void testMissing2DCoordinates() {
        String cmlString = 
          "<molecule id='m1'><atomArray><atom id='a1' xy2='0.0 0.1'/><atom id='a2'/><atom id='a3' xy2='0.1 0.0'/></atomArray></molecule>";
          
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(3, mol.getAtomCount());
        Atom atom1 = mol.getAtomAt(0);
        Atom atom2 = mol.getAtomAt(1);
        Atom atom3 = mol.getAtomAt(2);
        
        assertNotNull(atom1.getPoint2D());
        assertNull   (atom2.getPoint2D());
        assertNotNull(atom3.getPoint2D());
    }

    public void testMissing3DCoordinates() {
        String cmlString = 
          "<molecule id='m1'><atomArray><atom id='a1' xyz3='0.0 0.1 0.2'/><atom id='a2'/><atom id='a3' xyz3='0.1 0.0 0.2'/></atomArray></molecule>";
          
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(3, mol.getAtomCount());
        Atom atom1 = mol.getAtomAt(0);
        Atom atom2 = mol.getAtomAt(1);
        Atom atom3 = mol.getAtomAt(2);
        
        assertNotNull(atom1.getPoint3D());
        assertNull   (atom2.getPoint3D());
        assertNotNull(atom3.getPoint3D());
    }
    
    public void testCrystal() {
        StringBuffer cmlStringB = new StringBuffer("  <molecule id=\"m1\">\n");
        cmlStringB.append("    <crystal z=\"4\">\n");
        cmlStringB.append("      <scalar id=\"sc1\" title=\"a\" errorValue=\"0.001\" units=\"units:angstrom\">4.500</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc2\" title=\"b\" errorValue=\"0.001\" units=\"units:angstrom\">4.500</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc3\" title=\"c\" errorValue=\"0.001\" units=\"units:angstrom\">4.500</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc4\" title=\"alpha\" units=\"units:degrees\">90</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc5\" title=\"beta\" units=\"units:degrees\">90</scalar>\n");
        cmlStringB.append("      <scalar id=\"sc6\" title=\"gamma\" units=\"units:degrees\">90</scalar>\n");
        cmlStringB.append("      <symmetry id=\"s1\" spaceGroup=\"Fm3m\"/>\n");
        cmlStringB.append("    </crystal>\n");
        cmlStringB.append("    <atomArray>\n");
        cmlStringB.append("      <atom id=\"a1\" elementType=\"Na\" formalCharge=\"1\" xyzFract=\"0.0 0.0 0.0\"\n");
        cmlStringB.append("        xy2=\"+23.1 -21.0\"></atom>\n");
        cmlStringB.append("      <atom id=\"a2\" elementType=\"Cl\" formalCharge=\"-1\" xyzFract=\"0.5 0.0 0.0\"></atom>\n");
        cmlStringB.append("    </atomArray>\n");
        cmlStringB.append("  </molecule>\n");
        
        ChemFile chemFile = parseCMLString(cmlStringB.toString());
        Crystal crystal = checkForCrystalFile(chemFile);
        assertEquals(4, crystal.getZ());
        assertEquals("Fm3m", crystal.getSpaceGroup());
        assertEquals(2, crystal.getAtomCount());
        double[] aaxis = crystal.getA();
        assertEquals(4.5, aaxis[0], 0.1);
        assertEquals(0.0, aaxis[1], 0.1);
        assertEquals(0.0, aaxis[2], 0.1);
        double[] baxis = crystal.getB();
        assertEquals(0.0, baxis[0], 0.1);
        assertEquals(4.5, baxis[1], 0.1);
        assertEquals(0.0, baxis[2], 0.1);
        double[] caxis = crystal.getC();
        assertEquals(0.0, caxis[0], 0.1);
        assertEquals(0.0, caxis[1], 0.1);
        assertEquals(4.5, caxis[2], 0.1);
    }

    public void testMoleculeId() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals("m1", mol.getID());
    }

    private ChemFile parseCMLString(String cmlString) {
        ChemFile chemFile = null;
        try {
            CMLReader reader = new CMLReader(new StringReader(cmlString));
            chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (CDKException exception) {
            fail();
        }
        return chemFile;
    }

    /**
     * Tests wether the file is indeed a single molecule file
     */
    private Molecule checkForSingleMoleculeFile(ChemFile chemFile) {
        return checkForXMoleculeFile(chemFile, 1);
    }
    
    private Molecule checkForXMoleculeFile(ChemFile chemFile, int numberOfMolecules) {
        assertNotNull(chemFile);
        
        assertEquals(chemFile.getChemSequenceCount(), 1);
        ChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        
        assertEquals(seq.getChemModelCount(), 1);
        ChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        
        SetOfMolecules moleculeSet = model.getSetOfMolecules();
        assertNotNull(moleculeSet);
        
        assertEquals(moleculeSet.getMoleculeCount(), numberOfMolecules);
        Molecule mol = null;
        for (int i=0; i<numberOfMolecules; i++) {
            mol = moleculeSet.getMolecule(i);
            assertNotNull(mol);
        }
        return mol;
    }

    private Crystal checkForCrystalFile(ChemFile chemFile) {
        assertNotNull(chemFile);
        
        assertEquals(chemFile.getChemSequenceCount(), 1);
        ChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        
        assertEquals(seq.getChemModelCount(), 1);
        ChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        
        Crystal crystal = model.getCrystal();
        assertNotNull(crystal);
        
        return crystal;
    }

}
