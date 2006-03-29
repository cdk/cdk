/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.io.cml;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Atomic tests for the reading CML documents. All tested CML strings are valid CML 2.3,
 * as can be determined in cdk/src/org/openscience/cdk/test/io/cml/cml23TestFramework.xml.
 *
 * @cdk.module test-extra
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class CML23FragmentsTest extends CDKTestCase {

    public CML23FragmentsTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(CMLFragmentsTest.class);
    }

    public void testAtomId() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtomAt(0);
        assertEquals("a1", atom.getID());
    }
    
    
    public void testAtomId3() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(3, mol.getAtomCount());
        IAtom atom = mol.getAtomAt(1);
        assertEquals("a2", atom.getID());
    }

    
    public void testAtomElementType3() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1' elementType='C'/></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtomAt(0);
        assertEquals("C", atom.getSymbol());
    }
    
    public void testBond() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getAtomAt(0);
        IAtom atom2 = bond.getAtomAt(1);
        assertEquals("a1", atom1.getID());
        assertEquals("a2", atom2.getID());
    }

    public void testBond4() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' bondID='b1 b2'/></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(3, mol.getAtomCount());
        assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getAtomAt(0);
        IAtom atom2 = bond.getAtomAt(1);
        assertEquals("a1", atom1.getID());
        assertEquals("a2", atom2.getID());
        assertEquals("b2", mol.getBondAt(1).getID());
    }

    public void testBond5() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' order='1 1'/></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(3, mol.getAtomCount());
        assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBondAt(0);
        assertEquals(2, bond.getAtomCount());
        assertEquals(1.0, bond.getOrder(), 0.0001);
        bond = mol.getBondAt(1);
        assertEquals(2, bond.getAtomCount());
        assertEquals(1.0, bond.getOrder(), 0.0001);
    }

    public void testBondId() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBondAt(0);
        assertEquals("b1", bond.getID());
    }
    
    public void testBondAromatic() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2'/><bondArray atomRef1='a1' atomRef2='a2' order='A'/></molecule>";
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBondAt(0);
        assertEquals(CDKConstants.BONDORDER_AROMATIC, bond.getOrder(), 0.0001);
        assertEquals(true, bond.getFlag(CDKConstants.ISAROMATIC));
    }
    
    public void testList() {
        String cmlString = 
          "<list>" + 
          "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>" +
          "<molecule id='m2'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>" +
          "</list>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        checkForXMoleculeFile(chemFile, 2);
    }

    public void testCoordinates2D() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x2='0.0 0.1' y2='1.2 1.3'/></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(2, mol.getAtomCount());
        assertNotNull(mol.getAtomAt(0).getPoint2d());
        assertNotNull(mol.getAtomAt(1).getPoint2d());
        assertNull(mol.getAtomAt(0).getPoint3d());
        assertNull(mol.getAtomAt(1).getPoint3d());
    }
  
    public void testCoordinates3D() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x3='0.0 0.1' y3='1.2 1.3' z3='2.1 2.5'/></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(2, mol.getAtomCount());
        assertNull(mol.getAtomAt(0).getPoint2d());
        assertNull(mol.getAtomAt(1).getPoint2d());
        assertNotNull(mol.getAtomAt(0).getPoint3d());
        assertNotNull(mol.getAtomAt(1).getPoint3d());
    }
    
    public void testFractional3D() {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' xFract='0.0 0.1' yFract='1.2 1.3' zFract='2.1 2.5'/></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(2, mol.getAtomCount());
        assertNull(mol.getAtomAt(0).getPoint3d());
        assertNull(mol.getAtomAt(1).getPoint3d());
        assertNotNull(mol.getAtomAt(0).getFractionalPoint3d());
        assertNotNull(mol.getAtomAt(1).getFractionalPoint3d());
    }
    
    public void testMissing2DCoordinates() {
        String cmlString = 
          "<molecule id='m1'><atomArray><atom id='a1' xy2='0.0 0.1'/><atom id='a2'/><atom id='a3' xy2='0.1 0.0'/></atomArray></molecule>";
          
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtomAt(0);
        IAtom atom2 = mol.getAtomAt(1);
        IAtom atom3 = mol.getAtomAt(2);
        
        assertNotNull(atom1.getPoint2d());
        assertNull   (atom2.getPoint2d());
        assertNotNull(atom3.getPoint2d());
    }

    public void testMissing3DCoordinates() {
        String cmlString = 
          "<molecule id='m1'><atomArray><atom id='a1' xyz3='0.0 0.1 0.2'/><atom id='a2'/><atom id='a3' xyz3='0.1 0.0 0.2'/></atomArray></molecule>";
          
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);
        
        assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtomAt(0);
        IAtom atom2 = mol.getAtomAt(1);
        IAtom atom3 = mol.getAtomAt(2);
        
        assertNotNull(atom1.getPoint3d());
        assertNull   (atom2.getPoint3d());
        assertNotNull(atom3.getPoint3d());
    }
    
    public void testMoleculeId() {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";
        
        IChemFile chemFile = parseCMLString(cmlString);
        IMolecule mol = checkForSingleMoleculeFile(chemFile);

        assertEquals("m1", mol.getID());
    }
    
    private IChemFile parseCMLString(String cmlString) {
        IChemFile chemFile = null;
        try {
            CMLReader reader = new CMLReader(new StringReader(cmlString));
            chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        } catch (CDKException exception) {
            fail();
        }
        return chemFile;
    }

    /**
     * Tests wether the file is indeed a single molecule file
     */
    private IMolecule checkForSingleMoleculeFile(IChemFile chemFile) {
        return checkForXMoleculeFile(chemFile, 1);
    }
    
    private IMolecule checkForXMoleculeFile(IChemFile chemFile, int numberOfMolecules) {
        assertNotNull(chemFile);
        
        assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        
        assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        
        org.openscience.cdk.interfaces.ISetOfMolecules moleculeSet = model.getSetOfMolecules();
        assertNotNull(moleculeSet);
        
        assertEquals(moleculeSet.getMoleculeCount(), numberOfMolecules);
        IMolecule mol = null;
        for (int i=0; i<numberOfMolecules; i++) {
            mol = moleculeSet.getMolecule(i);
            assertNotNull(mol);
        }
        return mol;
    }

//    private ICrystal checkForCrystalFile(IChemFile chemFile) {
//        assertNotNull(chemFile);
//        
//        assertEquals(chemFile.getChemSequenceCount(), 1);
//        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
//        assertNotNull(seq);
//        
//        assertEquals(seq.getChemModelCount(), 1);
//        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
//        assertNotNull(model);
//        
//        org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
//        assertNotNull(crystal);
//        
//        return crystal;
//    }

}
