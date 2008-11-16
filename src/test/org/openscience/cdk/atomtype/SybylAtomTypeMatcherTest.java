/* $Revision: 11293 $ $Author: rajarshi $ $Date: 2008-06-06 22:46:01 +0200 (Fri, 06 Jun 2008) $
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * This class tests the perception of Sybyl atom types, which uses
 * CDK atom type perception and mapping of CDK atom types to Sybyl
 * atom types.
 *
 * @cdk.module test-atomtype
 */
public class SybylAtomTypeMatcherTest extends AbstractSybylAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    static {
        // do not complain about a few non-tested atom types
        // so, just mark them as tested
        testedAtomTypes.put("LP", 1);
        testedAtomTypes.put("Du", 1);
        testedAtomTypes.put("Du.C", 1);
        testedAtomTypes.put("Any", 1);
        testedAtomTypes.put("Hal", 1);
        testedAtomTypes.put("Het", 1);
        testedAtomTypes.put("Hev", 1);
        testedAtomTypes.put("X", 1);
        testedAtomTypes.put("Het", 1);
        testedAtomTypes.put("H.t3p", 1);
        testedAtomTypes.put("H.spc", 1);
        testedAtomTypes.put("O.t3p", 1);
        testedAtomTypes.put("O.spc", 1);
    }

	@Test public void testGetInstance_IChemObjectBuilder() {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
	}
	
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
		Molecule ethane = MoleculeFactory.makeAlkane(2);
		String[] expectedTypes = {"C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, ethane);
	}

  @Test public void testFindMatchingAtomType_IAtomContainer() throws Exception {
      String filename = "data/mol2/atomtyping.mol2";
      InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
      Mol2Reader reader = new Mol2Reader(ins);
      IMolecule mol = (IMolecule)reader.read(new Molecule());

      // just check consistency; other methods do perception testing
      SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
      IAtomType[] types = matcher.findMatchingAtomType(mol);
      for (int i=0; i<types.length; i++) {
          IAtomType type = matcher.findMatchingAtomType(mol, mol.getAtom(i));
          Assert.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
      }
  }

    @Test public void testAtomTyping() throws Exception {
        String filename = "data/mol2/atomtyping.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtoms.next().getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    /**
     * Uses findMatchingAtomType(IAtomContainer, IAtom) type.
     */
    @Test public void testBenzene() throws Exception {
        IMolecule benzene = MoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomType(benzene);
        for (IAtomType type : types) {
            Assert.assertEquals("C.ar", type.getAtomTypeName());
        }
    }

    @Test public void testAdenine() throws Exception {
        IMolecule mol = MoleculeFactory.makeAdenine();
          String[] expectedTypes = {"C.ar", "C.ar", "C.ar", "N.ar", "N.ar", "N.ar",
            "N.ar", "N.3", "C.ar", "C.ar"
          };
          SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(mol.getBuilder());
          IAtomType[] types = matcher.findMatchingAtomType(mol);
          for (int i=0; i<expectedTypes.length; i++) {
              assertAtomType(testedAtomTypes,
                  "Incorrect perception for atom " + i,
                  expectedTypes[i], types[i]
              );
          }
      }

    /**
     * Uses findMatchingAtomType(IAtomContainer) type.
     */
    @Test public void testBenzene_AtomContainer() throws Exception {
        IMolecule benzene = MoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomType(benzene);
        for (IAtomType type : types) {
          Assert.assertEquals("C.ar", type.getAtomTypeName());
        }
    }

	@Test public void testAtomTyping4() throws Exception {
        String filename = "data/mol2/atomtyping4.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            IAtom refAtom = refAtoms.next();
            String refName = refAtom.getAtomTypeName();
            if (refName.endsWith(".ar")) {
                refName = refName.substring(0, refName.indexOf(".")) + ".2";
                refAtom.setAtomTypeName(refName);
            }
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    @Test public void testAtomTyping3() throws Exception {
        String filename = "data/mol2/atomtyping3.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	IAtom refAtom = refAtoms.next();
          String refName = refAtom.getAtomTypeName();
        	if (refName.endsWith(".ar")) {
        	    refName = refName.substring(0, refName.indexOf(".")) + ".2";
        	    refAtom.setAtomTypeName(refName);
        	}
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    private void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws CDKException {
    	SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(container.getBuilder());
        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
        	IAtom atom = atoms.next();
        	atom.setAtomTypeName(null);
        	IAtomType matched = matcher.findMatchingAtomType(container, atom);
        	if (matched != null) AtomTypeManipulator.configure(atom, matched);
        }
	}

    @Test public void testForDuplicateDefinitions() {
    	super.testForDuplicateDefinitions();
    }

    @Test public void testDummy() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new PseudoAtom("R");
        mol.addAtom(atom);

        String[] expectedTypes = {"X"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testEthene() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.2", "C.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testImine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.2", "N.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testPropyne() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);
        mol.addBond(2,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.1", "C.1", "C.3"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHalogenatedMethane() throws Exception {
        IMolecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("Cl"));
        mol.addAtom(new Atom("I"));
        mol.addAtom(new Atom("Br"));
        mol.addBond(0,1,IBond.Order.SINGLE);
        mol.addBond(0,2,IBond.Order.SINGLE);
        mol.addBond(0,3,IBond.Order.SINGLE);
        mol.addBond(0,4,IBond.Order.SINGLE);

        String[] expectedTypes = {"C.3", "F", "Cl", "I", "Br"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMnF4() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Mn");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        mol.addAtom(atom); atom.setFormalCharge(-1);
        mol.addAtom(atom2); atom2.setFormalCharge(+2);
        mol.addAtom(atom3); atom3.setFormalCharge(-1);
        mol.addAtom(atom4); atom4.setFormalCharge(-1);
        mol.addAtom(atom5); atom5.setFormalCharge(-1);

        String[] expectedTypes = {"F", "Mn", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testAmide() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.2", "C.2", "N.am"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMethylAmine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"N.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testAmmonia() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        IAtom atom5 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"H", "N.4", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMethanol() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testDMSO() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.2", "S.O", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testDMSOO() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,4,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.2", "O.2", "S.O2", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testCarbokation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("C"); atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"H", "C.cat", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    @Test public void testSilicon() throws Exception {
    	IMolecule mol = new Molecule();
    	IAtom a1 = mol.getBuilder().newAtom("Si"); mol.addAtom(a1);
    	IAtom a2 = mol.getBuilder().newAtom("O"); mol.addAtom(a2);
    	IAtom a3 = mol.getBuilder().newAtom("O"); mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newAtom("O"); mol.addAtom(a4);
    	IAtom a5 = mol.getBuilder().newAtom("C"); mol.addAtom(a5);
    	IAtom a6 = mol.getBuilder().newAtom("C"); mol.addAtom(a6);
    	IAtom a7 = mol.getBuilder().newAtom("C"); mol.addAtom(a7);
    	IAtom a8 = mol.getBuilder().newAtom("H"); mol.addAtom(a8);
    	IAtom a9 = mol.getBuilder().newAtom("H"); mol.addAtom(a9);
    	IAtom a10 = mol.getBuilder().newAtom("H"); mol.addAtom(a10);
    	IAtom a11 = mol.getBuilder().newAtom("H"); mol.addAtom(a11);
    	IAtom a12 = mol.getBuilder().newAtom("H"); mol.addAtom(a12);
    	IAtom a13 = mol.getBuilder().newAtom("H"); mol.addAtom(a13);
    	IAtom a14 = mol.getBuilder().newAtom("H"); mol.addAtom(a14);
    	IAtom a15 = mol.getBuilder().newAtom("H"); mol.addAtom(a15);
    	IAtom a16 = mol.getBuilder().newAtom("H"); mol.addAtom(a16);
    	IAtom a17 = mol.getBuilder().newAtom("H"); mol.addAtom(a17);
    	IBond b1 = mol.getBuilder().newBond(a1, a2, IBond.Order.SINGLE); mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newBond(a1, a3, IBond.Order.SINGLE); mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newBond(a1, a4, IBond.Order.SINGLE); mol.addBond(b3);
    	IBond b4 = mol.getBuilder().newBond(a2, a5, IBond.Order.SINGLE); mol.addBond(b4);
    	IBond b5 = mol.getBuilder().newBond(a3, a6, IBond.Order.SINGLE); mol.addBond(b5);
    	IBond b6 = mol.getBuilder().newBond(a4, a7, IBond.Order.SINGLE); mol.addBond(b6);
    	IBond b7 = mol.getBuilder().newBond(a5, a8, IBond.Order.SINGLE); mol.addBond(b7);
    	IBond b8 = mol.getBuilder().newBond(a5, a9, IBond.Order.SINGLE); mol.addBond(b8);
    	IBond b9 = mol.getBuilder().newBond(a5, a10, IBond.Order.SINGLE); mol.addBond(b9);
    	IBond b10 = mol.getBuilder().newBond(a6, a11, IBond.Order.SINGLE); mol.addBond(b10);
    	IBond b11 = mol.getBuilder().newBond(a6, a12, IBond.Order.SINGLE); mol.addBond(b11);
    	IBond b12 = mol.getBuilder().newBond(a6, a13, IBond.Order.SINGLE); mol.addBond(b12);
    	IBond b13 = mol.getBuilder().newBond(a7, a14, IBond.Order.SINGLE); mol.addBond(b13);
    	IBond b14 = mol.getBuilder().newBond(a7, a15, IBond.Order.SINGLE); mol.addBond(b14);
    	IBond b15 = mol.getBuilder().newBond(a7, a16, IBond.Order.SINGLE); mol.addBond(b15);
    	IBond b16 = mol.getBuilder().newBond(a1, a17, IBond.Order.SINGLE); mol.addBond(b16);

    	String[] expectedTypes = {"Si", "O.3", "O.3", "O.3", "C.3", "C.3", "C.3",
    			"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testThioAmide() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("S");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"S.2", "C.2", "N.am"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    @Test public void countTestedAtomTypes() {
        super.countTestedAtomTypes(testedAtomTypes);
    }

}
