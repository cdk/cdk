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
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("a1", atom.getID());
    }

    
    public void testAtomId2() {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='id'>a1</stringArray></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("a1", atom.getID());
    }
    
    public void testAtomElementType() {
        String cmlString = "<molecule id='m1'><atomArray><stringArray builtin='elementType'>C</stringArray></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("C", atom.getSymbol());
    }
    
    public void testAtomElementType2() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'><string builtin='elementType'>C</string></atom></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

        assertEquals(1, mol.getAtomCount());
        Atom atom = mol.getAtomAt(0);
        assertEquals("C", atom.getSymbol());
    }
    
    public void test2dCoord() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'><coordinate2 builtin='xy2'>84 138</coordinate2></atom></atomArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

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
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

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
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

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
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

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
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        Bond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        Atom atom1 = bond.getAtomAt(0);
        Atom atom2 = bond.getAtomAt(1);
        assertEquals("a1", atom1.getID());
        assertEquals("a2", atom2.getID());
    }
    
    public void testBondId() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";
        
        ChemFile chemFile = parseCMLString(cmlString);
        Molecule mol = checkForSingleMoleculeFile(chemFile);;

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        Bond bond = mol.getBondAt(0);
        assertEquals("b1", bond.getID());
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
        assertNotNull(chemFile);
        
        assertEquals(chemFile.getChemSequenceCount(), 1);
        ChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        
        assertEquals(seq.getChemModelCount(), 1);
        ChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        
        SetOfMolecules moleculeSet = model.getSetOfMolecules();
        assertNotNull(moleculeSet);
        
        assertEquals(moleculeSet.getMoleculeCount(), 1);
        Molecule mol = moleculeSet.getMolecule(0);
        assertNotNull(mol);
        return mol;
    }
    
}
